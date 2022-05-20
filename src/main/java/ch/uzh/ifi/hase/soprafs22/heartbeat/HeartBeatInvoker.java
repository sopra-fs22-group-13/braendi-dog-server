package ch.uzh.ifi.hase.soprafs22.heartbeat;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.controller.LobbyController;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
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
                UserService userService = SpringContext.getBean(UserService.class);
                for (String token: playerTokens) {
                    Map<HeartBeatType, Boolean> heartBeats = hbm.getHeartBeatsOfPlayer(token);


                    if(heartBeats.get(HeartBeatType.LOBBY) == false)
                    {
                        //TODO
                        LobbyManager lm = SpringContext.getBean(LobbyController.class).getLobbyManagerInstance();
                        User user = userService.getUserByToken(token);
                        if(lm.getLobbyIdFromPlayer(token) != -1){
                            lm.closeLobby(lm.getLobbyIdFromPlayer(token), true, token);
                            userService.setUserOffline(user);
                        }
                    }
                    if(heartBeats.get(HeartBeatType.GAME) == false)
                    {
                        //TODO
                        GameManager gm = GameManager.getInstance();
                        User user = userService.getUserByToken(token);
                        if(gm.getGameByPlayer(token) != null){
                            gm.deleteGame(gm.getGameByPlayer(token).getGameToken(), true);
                            userService.setUserOffline(user);
                        }
                    }
                }
            }
        });
        timer.setRepeats(true); // execute repeatedly
        timer.start(); // Go go go!
    }
}
