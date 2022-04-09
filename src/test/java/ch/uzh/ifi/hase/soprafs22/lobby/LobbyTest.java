package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        assertEquals(Arrays.asList(owner), lobby.getPlayers());
        assertEquals(1, lobby.getId());

        User newOwner = new User();
        Lobby newLobby = new Lobby(newOwner);
        assertEquals(2, newLobby.getId());
    }

    @Test
    public void addPlayerTest() {
        User newPlayer = new User();
        lobby.addPlayer(newPlayer);
        assertEquals(Arrays.asList(owner, newPlayer), lobby.getPlayers());

        User anotherPlayer = new User();
        assertEquals(3, lobby.addPlayer(anotherPlayer));
    }
}