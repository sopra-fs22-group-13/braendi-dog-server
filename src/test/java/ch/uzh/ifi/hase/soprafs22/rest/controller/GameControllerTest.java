package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.MovePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(GameController.class)
@ActiveProfiles("test")
public class GameControllerTest {
    @Autowired
    private GameController gameController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private GameManager gameManager;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        gameController.insertGameManager(gameManager);
    }

    /**
     * the following tests verify the behaviour of
     * GET /game/{gametoken}/board
     */

    @Test
    public void getBoardByGametoken_Test_Unauthorized() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/board");

        // then
        basicAuthorization_Test(getRequest);
    }

    @Test
    public void getBoardByGametoken_Test_GameNotFound() throws Exception {
        //given
        User client = new User();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(gameManager.getGameByToken("testGametoken")).willReturn(null);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/board")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBoardByGametoken_Test_NotInParty() throws Exception {
        //given
        User client = new User(); client.setToken("clientToken");
        Game game = Mockito.mock(Game.class);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(gameManager.getGameByToken("testGametoken")).willReturn(game);
        given(game.getPlayerByToken("clientToken")).willReturn(null);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/board")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getBoardByGametoken_Test_Valid() throws Exception {
        //given
        User client = new User(); client.setToken("clientToken");
        Game game = Mockito.mock(Game.class);
        Player player = Mockito.mock(Player.class);
        BoardData boardData = new BoardData(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                1, 2, 3, 4
        );

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(gameManager.getGameByToken("testGametoken")).willReturn(game);
        given(game.getPlayerByToken(Mockito.any())).willReturn(player);
        given(game.gameState()).willReturn(boardData);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/board")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                //just an excerpt. testing the whole object here would be overkill
                .andExpect(jsonPath("$.redBase", is(boardData.getRedBase())))
                .andExpect(jsonPath("$.blueBase", is(boardData.getBlueBase())));
    }


    /**
     * the following tests verify the behaviour of
     * GET /game/{gametoken}/players
     */

    @Test
    public void getPlayersByGametoken_Test_Unauthorized() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/players");

        // then
        basicAuthorization_Test(getRequest);
    }

    @Test
    public void getPlayersByGametoken_Test_GameNotFound() throws Exception {
        //given
        User client = new User();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(gameManager.getGameByToken("testGametoken")).willReturn(null);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/players")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPlayersByGametoken_Test_Valid() throws Exception {
        //given
        User client = new User(); client.setToken("clientToken");
        Game game = Mockito.mock(Game.class);

        ArrayList<String> visibleCards = new ArrayList<>();
        visibleCards.add("testCard");
        PlayerData playerData = new PlayerData();
        playerData.setVisibleCards(visibleCards);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(gameManager.getGameByToken("testGametoken")).willReturn(game);
        given(game.getPlayerStates(client.getToken())).willReturn(playerData);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/testGametoken/players")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visibleCards[0]", is(visibleCards.get(0))));
    }


    /**
     * the following tests verify the behaviour of
     * PUT /game/{gametoken}/board
     */

    @Test
    public void putBoard_Test_InvalidRequest() throws Exception {
        //given
        MovePutDTO movePutDTO = new MovePutDTO();
        movePutDTO.setToken("testDTOtoken");
        ArrayList<Integer> fromPos = new ArrayList<>(); fromPos.add(1);
        movePutDTO.set_fromPos(fromPos);
        movePutDTO.set_toPos(new ArrayList<>());
        movePutDTO.set_fromPosInGoal(new ArrayList<>());
        movePutDTO.set_toPosInGoal(new ArrayList<>());
        movePutDTO.setCard("testCard");

        //when
        MockHttpServletRequestBuilder putRequest = put("/game/testGametoken/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movePutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putBoard_Test_GameNotFound() throws Exception {
        //given
        MovePutDTO movePutDTO = new MovePutDTO();
        movePutDTO.setToken("testDTOtoken");
        movePutDTO.set_fromPos(new ArrayList<>());
        movePutDTO.set_toPos(new ArrayList<>());
        movePutDTO.set_fromPosInGoal(new ArrayList<>());
        movePutDTO.set_toPosInGoal(new ArrayList<>());
        movePutDTO.setCard("testCard");

        given(gameManager.getGameByToken("testGametoken")).willReturn(null);

        //when
        MockHttpServletRequestBuilder putRequest = put("/game/testGametoken/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movePutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void putBoard_Test_InvalidMove() throws Exception {
        //given
        MovePutDTO movePutDTO = new MovePutDTO();
        movePutDTO.setToken("testDTOtoken");
        movePutDTO.set_fromPos(new ArrayList<>());
        movePutDTO.set_toPos(new ArrayList<>());
        movePutDTO.set_fromPosInGoal(new ArrayList<>());
        movePutDTO.set_toPosInGoal(new ArrayList<>());
        movePutDTO.setCard("testCard");

        Game game = Mockito.mock(Game.class);

        given(gameManager.getGameByToken("testGametoken")).willReturn(game);
        doThrow(InvalidMoveException.class)
                .when(game)
                .playerMove(Mockito.any());

        //when
        MockHttpServletRequestBuilder putRequest = put("/game/testGametoken/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movePutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putBoard_Test_Valid() throws Exception {
        //given
        MovePutDTO movePutDTO = new MovePutDTO();
        movePutDTO.setToken("testDTOtoken");
        movePutDTO.set_fromPos(new ArrayList<>());
        movePutDTO.set_toPos(new ArrayList<>());
        movePutDTO.set_fromPosInGoal(new ArrayList<>());
        movePutDTO.set_toPosInGoal(new ArrayList<>());
        movePutDTO.setCard("testCard");

        Game game = Mockito.mock(Game.class);

        given(gameManager.getGameByToken("testGametoken")).willReturn(game);

        //when
        MockHttpServletRequestBuilder putRequest = put("/game/testGametoken/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movePutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        Mockito.verify(game, times(1)).playerMove(Mockito.any());
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


    /**
     * tests if the method fails, given a failed basic authorization
     */
    private void basicAuthorization_Test(MockHttpServletRequestBuilder mockBuilder) throws Exception {
        given(userService.checkIfLoggedIn(Mockito.any())).willThrow( new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(mockBuilder)
                .andExpect(status().isUnauthorized());
    }
}