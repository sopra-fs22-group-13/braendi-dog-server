package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)

public class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  //@Test
  public void wrongGameTokenTest() throws Exception {

    String gametoken="ciao";
    String userToken=UUID.randomUUID().toString();
    User user1= new User();
    User user2= new User();
    User user3= new User();
    User user4= new User();

    ArrayList<User> fakeUser= new ArrayList<>();
    user1= userService.createUser(user1);
    user2= userService.createUser(user2);
    user3= userService.createUser(user3);
    user4= userService.createUser(user4);
    fakeUser.add(user1);
    fakeUser.add(user2);
    fakeUser.add(user3);
    fakeUser.add(user4);


    GameManager gameManager=    GameManager.getInstance();


    given(userService.checkIfLoggedIn(Mockito.any())).willReturn(null);
    //when (gameManager.getGameByPlayer(userToken)).thenReturn(game);

    // when
    MockHttpServletRequestBuilder getRequest = get("/game/board/"+gametoken)
                    .header("Authorization", userToken)
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    // then
    mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}