package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
     * tests if the method fails, given a failed basic authorization
     */
    private void basicAuthorization_Test(MockHttpServletRequestBuilder mockBuilder) throws Exception {
        given(userService.checkIfLoggedIn(Mockito.any())).willThrow( new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(mockBuilder)
                .andExpect(status().isUnauthorized());
    }
}