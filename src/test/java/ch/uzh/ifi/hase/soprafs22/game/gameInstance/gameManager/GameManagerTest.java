package ch.uzh.ifi.hase.soprafs22.game.gameInstance.gameManager;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GameManagerTest {

    @Autowired
    private UserService userService;

    @Test
    public void gameManagerSingletonTest(){
        GameManager gameManager1= GameManager.getInstance();
        GameManager gameManager2 = GameManager.getInstance();
        assertEquals(gameManager1,gameManager2);
    }
    //@Test
    public void addGameTest(){
        GameManager gameManager= GameManager.getInstance();


        ArrayList<User> users= new ArrayList<>();
        for (int i = 0; i<4; i++){

            User user = new User();
            user.setUsername(RandomString.make(6));
            user.setPassword(RandomString.make(10));
            user = userService.createUser(user);


            users.add(user);
        }

        Game game = new Game(users);

        gameManager.addGame(game);

        assertEquals(game,gameManager.getGameByToken(game.getGameToken()));
    }

}
