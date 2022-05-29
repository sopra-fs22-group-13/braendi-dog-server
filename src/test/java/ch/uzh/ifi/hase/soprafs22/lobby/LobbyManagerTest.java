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
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LobbyManagerTest {
    LobbyManager lobbyManager;
    User owner;
    User invitee;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        lobbyManager = new LobbyManager();
        owner = new User();
        owner.setUsername(RandomString.make(6));
        owner.setPassword(RandomString.make(10));
        owner = userService.getUserByToken(userService.createUser(owner).getToken());

        invitee = new User();
        invitee.setUsername(RandomString.make(6));
        invitee.setPassword(RandomString.make(10));
        invitee = userService.getUserByToken(userService.createUser(invitee).getToken());
    }


    @Test
    public void creationTest() {
        Lobby newLobby = lobbyManager.getLobbyByID(lobbyManager.openLobby(owner.getToken()));

        assertThat(owner).usingRecursiveComparison().isEqualTo(newLobby.getOwner());
        assertEquals(newLobby, lobbyManager.getLobbyByID(newLobby.getId()));
        assertTrue(lobbyManager.isInLobby(owner));

        assertThrows(ResponseStatusException.class, () -> lobbyManager.openLobby(owner.getToken()));
    }

    @Test
    public void lobbyByIdTest() {
        Lobby newLobby = lobbyManager.getLobbyByID(lobbyManager.openLobby(owner.getToken()));
        assertNull(lobbyManager.getLobbyByID(0));
        assertEquals(newLobby, lobbyManager.getLobbyByID(newLobby.getId()));
        assertNull(lobbyManager.getLobbyByID(newLobby.getId()-1));
        assertNull(lobbyManager.getLobbyByID(newLobby.getId()+1));
    }

    @Test
    public void inviteTest() {
        int lobbyID = lobbyManager.openLobby(owner.getToken());
        Lobby newLobby = lobbyManager.getLobbyByID(lobbyID);
        lobbyManager.invitePlayer(lobbyID, owner.getToken(), invitee.getToken());

        assertSameUserList(List.of(invitee), newLobby.getPendingInvites());
    }

    @Test
    public void invResponseTest() {
        int lobbyID = lobbyManager.openLobby(owner.getToken());
        Lobby newLobby = lobbyManager.getLobbyByID(lobbyID);
        lobbyManager.invitePlayer(lobbyID, owner.getToken(), invitee.getToken());
        lobbyManager.inviteResponse(lobbyID, invitee.getToken(), true);

        assertEquals(2, newLobby.getPlayers().size());
        assertSameUserList(List.of(owner, invitee), newLobby.getPlayers());
        assertTrue(newLobby.getPendingInvites().isEmpty());
        assertTrue(lobbyManager.isInLobby(invitee));

        //negative response
        User anotherInvitee = new User();
        anotherInvitee.setUsername(RandomString.make(6));
        anotherInvitee.setPassword(RandomString.make(10));
        anotherInvitee = userService.getUserByToken(userService.createUser(anotherInvitee).getToken());
        lobbyManager.invitePlayer(lobbyID, owner.getToken(), anotherInvitee.getToken());
        lobbyManager.inviteResponse(lobbyID, anotherInvitee.getToken(), false);

        assertEquals(2, newLobby.getPlayers().size());
        assertSameUserList(List.of(owner, invitee), newLobby.getPlayers());
        assertTrue(newLobby.getPendingInvites().isEmpty());
        assertFalse(lobbyManager.isInLobby(anotherInvitee));

        //user that isn't invited wants to accept an invite
        User nonInvitee = new User();
        nonInvitee.setUsername(RandomString.make(6));
        nonInvitee.setPassword(RandomString.make(10));
        nonInvitee = userService.getUserByToken(userService.createUser(nonInvitee).getToken());
        String nonInvToken = nonInvitee.getToken();
        assertThrows(ResponseStatusException.class, () -> lobbyManager.inviteResponse(lobbyID, nonInvToken, true));
        assertFalse(lobbyManager.isInLobby(nonInvitee));
    }

    @Test
    public void closeLobbyTest() {
        Lobby newLobby = lobbyManager.getLobbyByID(lobbyManager.openLobby(owner.getToken()));
        Integer lobbyID = newLobby.getId();

        lobbyManager.closeLobby(lobbyID);
        assertNull(lobbyManager.getLobbyByID(lobbyID));
        assertFalse(lobbyManager.isInLobby(owner));
    }

    @Test
    public void startGameTest() {
        System.setProperty("api.enabled", "false");
        GameCreator gameCreator = mock(GameCreator.class);
        lobbyManager.gameCreator = gameCreator;
        IUpdateController updateController = mock(IUpdateController.class);
        lobbyManager.updateController = updateController;

        Lobby newLobby = lobbyManager.getLobbyByID(lobbyManager.openLobby(owner.getToken()));
        Integer lobbyID = newLobby.getId();

        when(gameCreator.createGame(newLobby)).thenReturn("ok");

        //lobby is not full
        assertThrows(ResponseStatusException.class, () -> lobbyManager.startGame(lobbyID, owner.getToken()));

        //fill the lobby
        User user2 = new User(); user2.setToken("user2"); newLobby.addPlayer(user2);
        User user3 = new User(); user3.setToken("user3"); newLobby.addPlayer(user3);
        User user4 = new User(); user4.setToken("user4"); newLobby.addPlayer(user4);

        //method not called from owner
        assertThrows(ResponseStatusException.class, () -> lobbyManager.startGame(lobbyID, user2.getToken()));

        //happy weather
        clearInvocations(updateController);
        lobbyManager.startGame(lobbyID, owner.getToken());
        assertNull(lobbyManager.getLobbyByID(lobbyID));

        //why not 8? because closing the lobby does not send an update if its called internally
        verify(updateController, times(4)).sendUpdateToUser(anyString(), any(UpdateDTO.class));
    }

    private void assertSameUserList(List<User> expected, List<User> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertThat(expected.get(i)).usingRecursiveComparison().isEqualTo(actual.get(i));
        }
    }
}