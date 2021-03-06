/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.rest.data.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.data.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.rest.service.GameHistoryService;
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
import java.util.Objects;
import java.util.stream.Collectors;

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
  private final GameHistoryService gameHistoryService;

  UserController(UserService userService, GameHistoryService gameHistoryService) {
    this.userService = userService;
    this.gameHistoryService = gameHistoryService;
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

  @GetMapping("/users/{userID}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(HttpServletRequest request, @PathVariable Long userID) {
      userService.checkIfLoggedIn(request);

      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUserById(userID));
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
      if (!Objects.equals(client.getId(), userID)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot edit someone else's profile.");

      User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
      userInput.setId(userID);

      userService.updateUser(userInput);
  }

  @GetMapping("/users/{userID}/history")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<GameHistoryGetDTO> getMatchHistory(HttpServletRequest request, @PathVariable Long userID) {
      userService.checkIfLoggedIn(request);

      User client = userService.getUserById(userID);

      //oh boy
      List<GameHistoryGetDTO> gameHistoryGetDTOs =
              gameHistoryService.getPlayedGames(client)
                      .stream()
                      .map(gameHistory -> {
                          return new GameHistoryGetDTO(
                                  gameHistory.getId(),
                                  gameHistory.getStartDate(),
                                  gameHistory.getWinner() != null && Objects.equals(gameHistory.getWinner().getId(), client.getId()),

                                  Objects.equals(gameHistory.getUser1().getId(), client.getId())? gameHistory.getUser1_goals() :
                                          Objects.equals(gameHistory.getUser2().getId(), client.getId())? gameHistory.getUser2_goals() :
                                                  Objects.equals(gameHistory.getUser3().getId(), client.getId()) ? gameHistory.getUser3_goals() :
                                                          gameHistory.getUser4_goals()
                          );
                      }).collect(Collectors.toList());

      if (gameHistoryGetDTOs.size() > 15) { //this does less than nothing as the SQL query has a limit of 15...
          gameHistoryGetDTOs = gameHistoryGetDTOs.subList(0, 15);
      }

      return gameHistoryGetDTOs;
  }

  @GetMapping("/users/leaderboard")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<User> getLeaderboard(HttpServletRequest request){
      User client = userService.checkIfLoggedIn(request);

      List<User> users = userService.getLeaderboard();

      return users;
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
