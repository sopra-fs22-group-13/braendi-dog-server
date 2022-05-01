package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.voicechat.VoiceChatCreator;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceRoom;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.hibernate.sql.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameCreator {

    private IUpdateController updateController = SpringContext.getBean(UpdateController.class);

    public String createGame(Lobby lobby) {
        GameManager manager= GameManager.getInstance();
        List<User> users =lobby.getPlayers();
        ArrayList<User> users1= new ArrayList<>();

        for (User user: users){
            users1.add(user);
        }
        //todo presentation: revert after
        //Collections.shuffle(users1);
        User u1 = users.get(2);
        users1.set(2, users1.get(0));
        users1.set(0, u1);

        Game newGame =  new Game(users1);
        manager.addGame(newGame);

        return newGame.getGameToken();
    };
}
