package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private GameHistoryService gameHistoryService;


    /**
     * the following tests verify the behaviour of
     * GET /users
     */

  //failed authorization
  @Test
  public void getUsers_Test_Unauthorized() throws Exception {
      // when
      MockHttpServletRequestBuilder getRequest = get("/users");

      // then
      basicAuthorization_Test(getRequest);
  }

  //happy weather
  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    String token=UUID.randomUUID().toString();
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);


    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);
    given (userService.checkIfLoggedIn(Mockito.any())).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }


    /**
     * the following tests verify the behaviour of
     * GET /users/{userID}
     */

  //failed authorization
  @Test
  public void getUserByUserID_Test_Unauthorized() throws Exception {
      //when
      MockHttpServletRequestBuilder getRequest = get("/users/1");

      //then
      basicAuthorization_Test(getRequest);
  }

  //happy weather
  @Test
  public void getUserByUserID_Test() throws Exception {
      //given
      String token = UUID.randomUUID().toString();
      User user = new User();
      user.setId(1L);

      given(userService.getUserById(1L)).willReturn(user);
      given(userService.checkIfLoggedIn(Mockito.any())).willReturn(user);

      //when
      MockHttpServletRequestBuilder getRequest = get("/users/1")
              .header("Authorization", token)
              .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

      //then
      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())));
  }

    /**
     * the following tests verify the behaviour of
     * POST /users
     */

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(header().exists("Authorization"))
        .andExpect(header().string("Authorization", "Basic 1"))
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }


    /**
     * the following tests verify the behaviour of
     * PUT /users/{userID}
     */

  //failed basic authorization
  @Test
  public void putUserByUserID_Test_Unauthorized() throws Exception {
      UserPutDTO userPutDTO = new UserPutDTO();

      //when
      MockHttpServletRequestBuilder putRequest = put("/users/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      //then
      basicAuthorization_Test(putRequest);
  }

  // put request trying to edit someone else's profile
  @Test
  public void putUserByUserID_Test_AccessDenied() throws Exception {
      // given
      User user = new User();
      user.setId(2L);

      UserPutDTO userPutDTO = new UserPutDTO();

      given(userService.checkIfLoggedIn(Mockito.any())).willReturn(user);

      //when
      MockHttpServletRequestBuilder putRequest = put("/users/1")
              .header("Authorization", "testToken")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      //then
      mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
  }

  //valid put request
  @Test
  public void putUserByUserID_Test_Valid() throws Exception {
      // given
      User user = new User();
      user.setId(1L);

      UserPutDTO userPutDTO = new UserPutDTO();

      given(userService.checkIfLoggedIn(Mockito.any())).willReturn(user);

      //when
      MockHttpServletRequestBuilder putRequest = put("/users/1")
              .header("Authorization", "testToken")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      //then
      mockMvc.perform(putRequest).andExpect(status().isOk());

      Mockito.verify(userService, times(1)).updateUser(Mockito.any());
  }


    /**
     * the following tests verify the behaviour of
     * GET /users/{userID}/history
     */

    //failed basic authorization
    @Test
    public void getMatchHistory_Test_Unauthorized() throws Exception {
        //when
        MockHttpServletRequestBuilder getRequest = get("/users/1/history");

        //then
        basicAuthorization_Test(getRequest);
    }

    @Test
    public void getMatchHistory_Test() throws Exception {
        // given
        User user = new User();
        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(user);

        List<GameHistory> providedHistory = new ArrayList<>();
        for (int i=0; i<20; i++) providedHistory.add(randomGameHistory());

        given(gameHistoryService.getPlayedGames(user)).willReturn(providedHistory);

        //when
        MockHttpServletRequestBuilder getRequest = get("/users/1/history")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void getLeaderboard_Test() throws Exception{
        //given
        User user = new User();
        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(user);

        User testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setPassword("testName");
        testUser1.setUsername("testUsername");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("testName2");
        testUser2.setUsername("testUsername2");

        ArrayList<User> u = new ArrayList<>(Arrays.asList(testUser1, testUser2));

        given(userService.getLeaderboard()).willReturn(u); //the leaderboard to return

        //when
        MockHttpServletRequestBuilder getRequest = get("/users/leaderboard")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }


    /**
     * the following tests verify the behaviour of
     * POST /login
     */

    @Test
    public void login_Test() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test User");
        userPostDTO.setUsername("testUsername");

        given(userService.loginUser(Mockito.any())).willReturn(user);

        // when
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", "Basic 1"))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }


    /**
     * tests if the method fails, given a failed basic authorization
     */
    private void basicAuthorization_Test(MockHttpServletRequestBuilder mockBuilder) throws Exception {
        given(userService.checkIfLoggedIn(Mockito.any())).willThrow( new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(mockBuilder)
                .andExpect(status().isUnauthorized());
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

  private GameHistory randomGameHistory() {
      GameHistory gh = new GameHistory();
      gh.setStartDate(new Random().nextLong());

      return gh;
  }
}