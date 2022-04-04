package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.websocket.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class LobbyControllerTest {
    LobbyController lobbyController;

    @BeforeEach
    public void setup() {
        lobbyController = new LobbyController();
    }

    @Test
    public void creationTest() {
        User owner = new User();
        Lobby newLobby = lobbyController.getLobbyByID(lobbyController.openLobby(owner.getToken()));

        assertEquals(owner, newLobby.getOwner());
        assertEquals(newLobby, lobbyController.getLobbyByID(newLobby.getId()));
    }

    @Test
    public void inviteTest() {
        User owner = new User();
        User invitee = new User();
        int lobbyID = lobbyController.openLobby(owner.getToken());
        Lobby newLobby = lobbyController.getLobbyByID(lobbyID);
        lobbyController.invitePlayer(lobbyID, owner.getToken(), invitee.getToken());

        assertEquals(List.of(invitee), newLobby.getPendingInvites());
    }

    @Test
    public void invResponseTest() {
        User owner = new User();
        User invitee = new User();
        int lobbyID = lobbyController.openLobby(owner.getToken());
        Lobby newLobby = lobbyController.getLobbyByID(lobbyID);
        lobbyController.invitePlayer(lobbyID, owner.getToken(), invitee.getToken());
        lobbyController.inviteResponse(lobbyID, invitee.getToken(), true);

        assertEquals(List.of(owner, invitee, null, null), newLobby.getPlayers());
        assertTrue(newLobby.getPendingInvites().isEmpty());
    }
}