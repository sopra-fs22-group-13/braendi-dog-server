package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Game _g;

    Player owner;
    Player player2;
    Player player3;
    Player player4;

    @BeforeEach
    void setupGame()
    {
        GameManager manager= new GameManager();
        owner = new Player(COLOR.YELLOW/*token*/);
        player2 = new Player(COLOR.RED/*token*/);
        player3 = new Player(COLOR.GREEN/*token*/);
        player4 = new Player(COLOR.BLUE/*token*/);
        List<Player> players =  new ArrayList<>();
        players.add(owner);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        _g = new Game(players,manager);
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