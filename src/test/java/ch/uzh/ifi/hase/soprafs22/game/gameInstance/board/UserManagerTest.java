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
            System.out.println("Token in setUp: " + i + user.getToken());

            Player player = new Player(COLOR.BLUE);

            players.add(player);
            users.add(user);
        }
        userManager = new UserManager(players, users);
    }
/*

    @Test
    void testGetPlayerFromUsertoken(){
        System.out.println("Token in Test: "+ users.get(0).getToken());
        assertEquals(players.get(0), userManager.getPlayerFromUserToken(users.get(0).getToken()));
        assertEquals(players.get(1), userManager.getPlayerFromUserToken(users.get(1).getToken()));
        assertEquals(players.get(2), userManager.getPlayerFromUserToken(users.get(2).getToken()));
        assertEquals(players.get(3), userManager.getPlayerFromUserToken(users.get(3).getToken()));
    }
*/

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
/*
    @Test
    void testGetUsersFromUsertoken(){
        assertEquals(users.get(0), userManager.getUserFromUsertoken("0"));
        assertEquals(users.get(1), userManager.getUserFromUsertoken("1"));
        assertEquals(users.get(2), userManager.getUserFromUsertoken("2"));
        assertEquals(users.get(3), userManager.getUserFromUsertoken("3"));
    }*/
}
