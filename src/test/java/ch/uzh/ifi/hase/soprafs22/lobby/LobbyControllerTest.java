package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.websocket.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LobbyControllerTest {
    LobbyController lobbyController;
    User owner;
    User invitee;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        lobbyController = new LobbyController();
        owner = new User();
        owner.setUsername(RandomString.make(6));
        owner.setPassword(RandomString.make(10));
        owner = userService.createUser(owner);

        invitee = new User();
        invitee.setUsername(RandomString.make(6));
        invitee.setPassword(RandomString.make(10));
        invitee = userService.createUser(invitee);
    }


    @Test
    public void creationTest() {
        Lobby newLobby = lobbyController.getLobbyByID(lobbyController.openLobby(owner.getToken()));

        assertThat(owner).usingRecursiveComparison().isEqualTo(newLobby.getOwner());
        assertEquals(newLobby, lobbyController.getLobbyByID(newLobby.getId()));
    }

    @Test
    public void inviteTest() {
        int lobbyID = lobbyController.openLobby(owner.getToken());
        Lobby newLobby = lobbyController.getLobbyByID(lobbyID);
        lobbyController.invitePlayer(lobbyID, owner.getToken(), invitee.getToken());

        assertSameUserList(List.of(invitee), newLobby.getPendingInvites());
    }

    @Test
    public void invResponseTest() {
        int lobbyID = lobbyController.openLobby(owner.getToken());
        Lobby newLobby = lobbyController.getLobbyByID(lobbyID);
        lobbyController.invitePlayer(lobbyID, owner.getToken(), invitee.getToken());
        lobbyController.inviteResponse(lobbyID, invitee.getToken(), true);

        assertEquals(2, newLobby.getPlayers().size());

        assertSameUserList(List.of(owner, invitee), newLobby.getPlayers());

        assertTrue(newLobby.getPendingInvites().isEmpty());
    }

    private void assertSameUserList(List<User> expected, List<User> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertThat(expected.get(i)).usingRecursiveComparison().isEqualTo(actual.get(i));
        }
    }
}