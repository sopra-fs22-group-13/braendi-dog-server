package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.rest.data.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.HeartBeatDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs22.heartbeat.HeartBeatManager;
import ch.uzh.ifi.hase.soprafs22.heartbeat.HeartBeatType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers( HttpServletRequest request) {

    userService.checkIfLoggedIn(request);

    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @GetMapping("/users/{usertoken}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(HttpServletRequest request, @PathVariable String usertoken) {
      userService.checkIfLoggedIn(request);

      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUserByToken(usertoken));
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(HttpServletResponse response, @RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);

    // convert internal representation of user back to API
    response.addHeader("Access-Control-Expose-Headers", "Authorization"); //allows the Auth header to be seen
    response.addHeader("Authorization", "Basic " + createdUser.getToken());

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PutMapping("/users/{userID}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void updateUser(HttpServletRequest request, @PathVariable Long userID, @RequestBody UserPutDTO userPutDTO) {
      User client = userService.checkIfLoggedIn(request);
      if (client.getId() != userID) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot edit someone else's profile.");

      User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
      userInput.setId(userID);

      userService.updateUser(userInput);
  }


    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(HttpServletResponse response, @RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User loginUser = userService.loginUser(userInput);
        response.addHeader("Access-Control-Expose-Headers", "Authorization"); //allows the Auth header to be seen
        response.addHeader("Authorization", "Basic " + loginUser.getToken());
        // convert internal representation of user back to API


        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loginUser);
    }

    @PostMapping("/heartbeat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateHeartbeat(HttpServletRequest request, @RequestBody HeartBeatDTO heartBeatDTO) {
      HeartBeatManager hbm = HeartBeatManager.getInstance();
      User client = userService.checkIfLoggedIn(request);
      hbm.addHeartBeat(client.getToken(), heartBeatDTO.getType());
    }
}
