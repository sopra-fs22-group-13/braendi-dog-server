package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Game _g;

    User owner;
    User user2;
    User user3;
    User user4;

    @BeforeEach
    void setupGame()
    {
        GameManager manager= new GameManager();
        owner = new User();
        user2 = new User();
        user3 = new User();
        user4 = new User();
        ArrayList<User> users =  new ArrayList<>();
        users.add(owner);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        _g = new Game(users,manager);
    }

/*
    @Test
    void getGameTokenTest(){
        assertNotNull(_g.getGameToken());
    }
    @Test
    void getUsersTokenTest(){
        assertEquals(owner.get_token(),_g.getUsersToken().get(0));
        assertEquals(player2.get_token(),_g.getUsersToken().get(1));
        assertEquals(player3.get_token(),_g.getUsersToken().get(2));
        assertEquals(player4.get_token(),_g.getUsersToken().get(3));
    }

    @Test
    void getCurrentTurnTest(){
        assertNotNull(_g.getCurrentTurn());
    }

    */
}