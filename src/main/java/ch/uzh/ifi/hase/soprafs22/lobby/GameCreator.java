package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.voicechat.VoiceChatCreator;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceRoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameCreator {


    public GameCreatorData createGame(Lobby lobby) {
        GameManager manager= GameManager.getInstance();
        List<User> users =lobby.getPlayers();
        ArrayList<User> users1= new ArrayList<>();

        for (User user: users){
            users1.add(user);
        }
        Collections.shuffle(users1);
        Game newGame =  new Game(users1);
        manager.addGame(newGame);

        //create the voiceChat room
        //this only works if api.enabled is set to true in the environment
        VoiceRoom vr = VoiceChatCreator.getInstance().createRoomWithPlayers(newGame.getGameToken());

        return new GameCreatorData(vr, newGame.getGameToken());
    };
}
