/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.UserManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserManagerTest {

    ArrayList<Player> players;
    ArrayList<User> users;
    UserManager userManager;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp(){
        players = new ArrayList<>();
        users = new ArrayList<>();

        for (int i = 0; i<4; i++){

            User user = new User();
            user.setUsername(RandomString.make(6));
            user.setPassword(RandomString.make(10));
            user = userService.createUser(user);

            Player player = new Player(COLOR.BLUE);

            players.add(player);
            users.add(user);
        }
        userManager = new UserManager(players, users);
    }

    @Test
    void testGetPlayerFromUsertoken(){
        assertEquals(players.get(0), userManager.getPlayerFromUserToken(users.get(0).getToken()));
        assertEquals(players.get(1), userManager.getPlayerFromUserToken(users.get(1).getToken()));
        assertEquals(players.get(2), userManager.getPlayerFromUserToken(users.get(2).getToken()));
        assertEquals(players.get(3), userManager.getPlayerFromUserToken(users.get(3).getToken()));
    }

    @Test
    void testGetPlayerFromUser(){
        assertEquals(players.get(0), userManager.getPlayerFromUser(users.get(0)));
        assertEquals(players.get(1), userManager.getPlayerFromUser(users.get(1)));
        assertEquals(players.get(2), userManager.getPlayerFromUser(users.get(2)));
        assertEquals(players.get(3), userManager.getPlayerFromUser(users.get(3)));
    }

    @Test
    void testGetUserFromPlayer(){
        assertEquals(users.get(0), userManager.getUserFromPlayer(players.get(0)));
        assertEquals(users.get(1), userManager.getUserFromPlayer(players.get(1)));
        assertEquals(users.get(2), userManager.getUserFromPlayer(players.get(2)));
        assertEquals(users.get(3), userManager.getUserFromPlayer(players.get(3)));
    }
    @Test
    void testGetUserFromUsertoken(){
        assertEquals(users.get(0).getId(), userManager.getUserFromUsertoken(users.get(0).getToken()).getId());
        assertEquals(users.get(1).getId(), userManager.getUserFromUsertoken(users.get(1).getToken()).getId());
        assertEquals(users.get(2).getId(), userManager.getUserFromUsertoken(users.get(2).getToken()).getId());
        assertEquals(users.get(3).getId(), userManager.getUserFromUsertoken(users.get(3).getToken()).getId());
    }
}
