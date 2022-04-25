package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;

import java.util.ArrayList;
import java.util.List;

public class GameCreator {


    public String createGame(Lobby lobby) {
        GameManager manager= GameManager.getInstance();
        List<User> users =lobby.getPlayers();
        ArrayList<User> users1= new ArrayList<>();

        for (User user: users){
            users1.add(user);
        }
        Game newGame =  new Game(users1);
        manager.addGame(newGame);

        //connect to voice chat still in process
        return newGame.getGameToken();
    };
}
