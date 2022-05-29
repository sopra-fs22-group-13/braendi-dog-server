/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
                        if(lm.getLobbyIdFromPlayer(token) != -1){
                            lm.closeLobby(lm.getLobbyIdFromPlayer(token), true, token);
                        }
                    }
                    if(heartBeats.get(HeartBeatType.GAME) == false)
                    {
                        //TODO
                        GameManager gm = GameManager.getInstance();
                        if(gm.getGameByPlayer(token) != null){
                            gm.deleteGame(gm.getGameByPlayer(token).getGameToken(), true);
                        }
                    }
                }
            }
        });
        timer.setRepeats(true); // execute repeatedly
        timer.start(); // Go go go!
    }
}
