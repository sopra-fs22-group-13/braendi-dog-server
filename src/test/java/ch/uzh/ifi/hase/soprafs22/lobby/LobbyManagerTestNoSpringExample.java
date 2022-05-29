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

import ch.uzh.ifi.hase.soprafs22.mocks.MockSpringContext;
import ch.uzh.ifi.hase.soprafs22.mocks.MockUpdateController;
import ch.uzh.ifi.hase.soprafs22.mocks.MockUserRepo;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LobbyManagerTestNoSpringExample {

    @Test
    public void someTest()
    {

        //create some user
        User owner = new User();
        owner.setToken("1");

        //make it final (needed for abstract class implementation below)
        final User finalOwner = owner;

        // mock the user Repo, override any function you need here.
        // You can also implement further logic to return different users based on the token
        // by doing regular if statements in the function.
        MockUserRepo mockUserRepo = new MockUserRepo() {
            @Override
            public User findByToken(String token) {
                return finalOwner;
            }
        };

        //mock the update controller in a similar matter
        MockUpdateController mockUpdateController = new MockUpdateController() {
            @Override
            public void sendUpdateToUser(String usertoken, UpdateDTO updateMessage) {
                super.sendUpdateToUser(usertoken, updateMessage);
            }
        };

        //make a new mockSpringContext
        MockSpringContext mockSpringContext = new MockSpringContext();

        //map the mocked controllers to be returned upon asking for them.
        mockSpringContext.returnForClass(UserRepository.class, mockUserRepo);
        mockSpringContext.returnForClass(UpdateController.class, mockUpdateController);

        //set the spring context object to the mocked one (instead of the default runtime one)
        SpringContext.setSpringContextObject(mockSpringContext);

        //NOW create the lobby controller (not before, as it gets the userRepository on creation)
        LobbyManager lobbyController = new LobbyManager();

        //test as usual
        Lobby newLobby = lobbyController.getLobbyByID(lobbyController.openLobby(finalOwner.getToken()));
        assertThat(finalOwner).usingRecursiveComparison().isEqualTo(newLobby.getOwner());
        assertEquals(newLobby, lobbyController.getLobbyByID(newLobby.getId()));

        //IMPORTANT: RESET THE SPRING CONTEXT FOR THE NEXT TESTS (in different files)
        SpringContext.resetSpringContextObject();

    }
}
