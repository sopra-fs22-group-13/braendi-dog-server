package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {
    private static User owner = new User();
    private static Lobby lobby;

    @BeforeEach
    public void setup() {
        lobby = new Lobby(owner);
    }

    @Test
    public void createLobbyTest() {
        assertEquals(List.of(owner), lobby.getPlayers());
        int startId = lobby.getId();

        User newOwner = new User();
        Lobby newLobby = new Lobby(newOwner);
        assertEquals(startId + 1, newLobby.getId());
        assertEquals(owner, lobby.getOwner());
    }

    @Test
    public void addPlayerTest() {
        User newPlayer = new User();
        lobby.addPlayer(newPlayer);
        assertEquals(Arrays.asList(owner, newPlayer), lobby.getPlayers());

        User anotherPlayer = new User();
        assertEquals(3, lobby.addPlayer(anotherPlayer));

        User andAnotherOne = new User();
        lobby.addPlayer(andAnotherOne);

        User lastOne = new User();
        assertThrows(ResponseStatusException.class, () -> lobby.addPlayer(lastOne));
    }

    @Test
    public void invitesTest() {
        //creates 2 new users and adds them as invitees
        User newPlayer = new User();
        User anotherPlayer = new User();
        lobby.addInvitee(newPlayer);
        lobby.addInvitee(anotherPlayer);

        assertEquals(List.of(newPlayer, anotherPlayer), lobby.getPendingInvites());

        //removes one invitee
        lobby.deleteInvite(newPlayer);
        assertEquals(List.of(anotherPlayer), lobby.getPendingInvites());
    }
}