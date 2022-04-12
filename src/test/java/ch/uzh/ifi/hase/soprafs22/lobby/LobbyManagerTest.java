package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LobbyManagerTest {
    LobbyManager lobbyController;
    User owner;
    User invitee;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        lobbyController = new LobbyManager();
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