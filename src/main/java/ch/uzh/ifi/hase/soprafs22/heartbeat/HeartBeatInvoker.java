package ch.uzh.ifi.hase.soprafs22.heartbeat;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class HeartBeatInvoker {
    public void startTesting()
    {
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                HeartBeatManager hbm = HeartBeatManager.getInstance();
                String[] playerTokens = hbm.getAllActivePlayerTokens();
                for (String token: playerTokens) {
                    Map<HeartBeatType, Boolean> heartBeats = hbm.getHeartBeatsOfPlayer(token);

                    // todo @Shitao
                    // do the cleanup in lobby and game
                    // careful, think about what must be synchronized and what does not

                    if(heartBeats.get(HeartBeatType.LOBBY) == false)
                    {
                        //idk
                    }
                    if(heartBeats.get(HeartBeatType.GAME) == false)
                    {
                        //idk
                        GameManager gm = GameManager.getInstance();
                    }
                }
            }
        });
        timer.setRepeats(true); // execute repeatedly
        timer.start(); // Go go go!
    }
}
