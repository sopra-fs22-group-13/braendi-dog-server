/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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