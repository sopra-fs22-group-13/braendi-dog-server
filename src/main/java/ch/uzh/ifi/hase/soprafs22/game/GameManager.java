/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

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

package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.UserManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;

import java.util.ArrayList;
import java.util.Date;

public class GameManager {

    private ArrayList<Game> _games;
    private static GameManager gameManager = null;
    protected IUpdateController updateController = SpringContext.getBean(UpdateController.class);

    private GameManager(){
        this._games= new ArrayList<>();
    }

    public static GameManager getInstance(){
        if(gameManager==null){
            gameManager= new GameManager();
        }
        return gameManager;
    }

    public void addGame(Game newGame){
        _games.add(newGame);
    }

    public Game getGameByToken(String token){
        for (Game game:_games){
            if (game.getGameToken().equals(token)){
                return game;
            }
        }
        return null;
    }

    public Game getGameByPlayer(String token){
        for (Game game:_games){
            if (game.getPlayerByToken(token) != null){
                return game;
            }
        }
        return null;
    }

    synchronized public void deleteGame(String tokenToDelete, boolean fromHeartBeat){
        for (Game game:_games){
            String token = game.getGameToken();
            if(tokenToDelete.equals(token)){
                if(fromHeartBeat)
                {
                    if(game.getCreationTime() + 5000 > new Date().getTime()) return; //not enough time passed for heartbeat deletion, skip
                    game.cancelGame(); //track stats on deletion from heartbeat.
                }
                _games.remove(game);
                return;
            }
        }
    }

    synchronized public void deleteGame(String tokenToDelete){
        deleteGame(tokenToDelete, false);
    }
}
