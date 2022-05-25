package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.InvitationPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.InvitationResponsePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
@ActiveProfiles("test")
class LobbyControllerTest {
    @Autowired
    private LobbyController lobbyController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LobbyManager lobbyManager;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        lobbyController.insertLobbyManager(lobbyManager);
    }

    /**
     * the following tests verify the behaviour of
     * GET /lobby/{lobbyID}
     */

    @Test
    public void getLobbyByLobbyID_Test_Unauthorized() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/lobby/1");

        // then
        basicAuthorization_Test(getRequest);
    }

    @Test
    public void getLobbyByLobbyID_Test_NotFound() throws Exception {
        //given
        User client = new User();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(1)).willReturn(null);

        //when
        MockHttpServletRequestBuilder getRequest = get("/lobby/1")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getLobbyByLobbyID_Test_NotInLobby() throws Exception {
        //given
        User client = new User(); client.setToken("clientToken");
        User owner = new User(); owner.setToken("ownerToken");
        Lobby lobby = new Lobby(owner);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(1)).willReturn(lobby);

        //when
        MockHttpServletRequestBuilder getRequest = get("/lobby/1")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getLobbyByLobbyID_Test_Valid() throws Exception {
        //given
        User client = new User();
        client.setToken("clientToken");
        client.setId(1L);
        client.setUsername("Client");
        Lobby lobby = new Lobby(client);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(1)).willReturn(lobby);

        //when
        MockHttpServletRequestBuilder getRequest = get("/lobby/1")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userIDs[0]", is(client.getId().intValue())))
                .andExpect(jsonPath("$.usernames[0]", is(client.getUsername())));
    }


    /**
     * the following tests verify the behaviour of
     * POST /lobby
     */

    @Test
    public void postLobby_Test_Unauthorized() throws Exception {
        // when
        MockHttpServletRequestBuilder postRequest = post("/lobby");

        // then
        basicAuthorization_Test(postRequest);
    }

    @Test
    public void postLobby_Test_Valid() throws Exception {
        //given
        User client = new User();
        client.setToken("testToken");
        Lobby lobby = new Lobby(client);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.openLobby(client.getToken())).willReturn(lobby.getId());

        //when
        MockHttpServletRequestBuilder postRequest = post("/lobby")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobbyID", is(lobby.getId())));
    }


    /**
     * the following tests verify the behaviour of
     * DELETE /lobby/{lobbyID}
     */

    @Test
    public void deleteLobbyByLobbyID_Test_Unauthorized() throws Exception {
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobby/1");

        // then
        basicAuthorization_Test(deleteRequest);
    }

    @Test
    public void deleteLobbyByLobbyID_Test_NotFound() throws Exception {
        //given
        User client = new User();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(1)).willReturn(null);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobby/1")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteLobbyByLobbyID_Test_NotOwned() throws Exception {
        //given
        User client = new User(); client.setToken("clientToken");
        User owner = new User(); owner.setToken("ownerToken");
        Lobby lobby = new Lobby(owner);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(1)).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobby/1")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteLobbyByLobbyID_Test_Valid() throws Exception {
        //given
        User client = new User(); client.setToken("clientToken");
        Lobby lobby = new Lobby(client);

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(1)).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/lobby/1")
                .header("Authorization", "testToken");

        //then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(lobbyManager, times(1)).closeLobby(1);
    }


    /**
     * the following tests verify the behaviour of
     * POST /game
     */

    @Test
    public void postGame_Test_Unauthorized() throws Exception {
        //given
        GamePostDTO gamePostDTO = new GamePostDTO();

        //when
        MockHttpServletRequestBuilder postRequest = post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));

        // then
        basicAuthorization_Test(postRequest);
    }

    @Test
    public void postGame_Test_NotFound() throws Exception {
        User client = new User();
        GamePostDTO gamePostDTO = new GamePostDTO();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(Mockito.any())).willReturn(null);

        // when
        MockHttpServletRequestBuilder postRequest = post("/game")
                .header("Authorization", "testToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    public void postGame_Test_NotOwned() throws Exception {
        User client = new User(); client.setToken("clientToken");
        User owner = new User(); owner.setToken("ownerToken");
        Lobby lobby = new Lobby(owner);
        GamePostDTO gamePostDTO = new GamePostDTO();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(Mockito.any())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder postRequest = post("/game")
                .header("Authorization", "testToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void postGame_Test_Valid() throws Exception {
        User client = new User(); client.setToken("clientToken");
        Lobby lobby = new Lobby(client);
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setLobbyID(lobby.getId());

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(lobbyManager.getLobbyByID(Mockito.any())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder postRequest = post("/game")
                .header("Authorization", "testToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated());

        Mockito.verify(lobbyManager, times(1)).startGame(lobby.getId(), client.getToken());
    }


    /**
     * the following tests verify the behaviour of
     * PUT /lobby/{lobbyID}/invitations
     */

    @Test
    public void putNewInvitation_Test_Unauthorized() throws Exception {
        //given
        InvitationPutDTO invitationPutDTO = new InvitationPutDTO();

        //when
        MockHttpServletRequestBuilder putRequest = put("/lobby/1/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invitationPutDTO));

        // then
        basicAuthorization_Test(putRequest);
    }

    @Test
    public void putNewInvitation_Test_PlayerNotFound() throws Exception {
        //given
        InvitationPutDTO invitationPutDTO = new InvitationPutDTO();
        invitationPutDTO.setInviteeID(1L);
        User client = new User();

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(userService.getUserById(1L)).willReturn(null);

        //when
        MockHttpServletRequestBuilder putRequest = put("/lobby/1/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invitationPutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    public void putNewInvitation_Test_Valid() throws Exception {
        //given
        InvitationPutDTO invitationPutDTO = new InvitationPutDTO();
        invitationPutDTO.setInviteeID(1L);
        User client = new User(); client.setToken("clientToken");
        User invitee = new User(); client.setToken("inviteeToken");

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);
        given(userService.getUserById(1L)).willReturn(invitee);

        //when
        MockHttpServletRequestBuilder putRequest = put("/lobby/1/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invitationPutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isOk());

        Mockito.verify(lobbyManager, times(1)).invitePlayer(1, client.getToken(), invitee.getToken());
    }


    /**
     * the following tests verify the behaviour of
     * PUT /invitations
     */

    @Test
    public void putInvitationResponse_Test_Unauthorized() throws Exception {
        //given
        InvitationResponsePutDTO invitationResponsePutDTO = new InvitationResponsePutDTO(1, true);

        //when
        MockHttpServletRequestBuilder putRequest = put("/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invitationResponsePutDTO));

        // then
        basicAuthorization_Test(putRequest);
    }

    @Test
    public void putInvitationResponse_Test_Valid() throws Exception {
        //given
        InvitationResponsePutDTO invitationResponsePutDTO = new InvitationResponsePutDTO(1, true);
        User client = new User(); client.setToken("clientToken");

        given(userService.checkIfLoggedIn(Mockito.any())).willReturn(client);

        //when
        MockHttpServletRequestBuilder putRequest = put("/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invitationResponsePutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isOk());

        Mockito.verify(lobbyManager, times(1)).inviteResponse(1, client.getToken(), true);
    }


    /**
     * tests if the method fails, given a failed basic authorization
     */
    private void basicAuthorization_Test(MockHttpServletRequestBuilder mockBuilder) throws Exception {
        given(userService.checkIfLoggedIn(Mockito.any())).willThrow( new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(mockBuilder)
                .andExpect(status().isUnauthorized());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}