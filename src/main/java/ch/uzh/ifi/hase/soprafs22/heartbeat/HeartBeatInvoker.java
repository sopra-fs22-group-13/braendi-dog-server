package ch.uzh.ifi.hase.soprafs22.heartbeat;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.controller.LobbyController;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;

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

                    //TODO
                    // do the cleanup in lobby and game
                    // careful, think about what must be synchronized and what does not

                    if(heartBeats.get(HeartBeatType.LOBBY) == false)
                    {
                        //TODO
                        LobbyManager lm = SpringContext.getBean(LobbyController.class).getLobbyManagerInstance();
                        lm.closeLobby(lm.getLobbyIdFromPlayer(token));
                        hbm.updateOnlineStatus(token, false);
                    }
                    if(heartBeats.get(HeartBeatType.GAME) == false)
                    {
                        //TODO
                        GameManager gm = GameManager.getInstance();
                        gm.deleteGame(gm.getGameByPlayer(token).getGameToken());
                        hbm.updateOnlineStatus(token, false);
                    }
                }
            }
        });
        timer.setRepeats(true); // execute repeatedly
        timer.start(); // Go go go!
    }
}
