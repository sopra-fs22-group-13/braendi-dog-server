package ch.uzh.ifi.hase.soprafs22.rest.controller;


import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@RestController
public class GameController {

    private final UserService userService;
    private GameManager gameManager;

    GameController(UserService userService) {this.userService = userService;}

    @EventListener(ApplicationReadyEvent.class)
    public void start() { gameManager = GameManager.getInstance();
    }

    @GetMapping("/game/board/{gametoken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BoardData getGame( @PathVariable String gametoken, HttpServletRequest response) {
        User user = userService.checkIfLoggedIn(response);
        if (user== null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, user == null ? "bad token" : "something went wrong");
        }
        if (GameManager.getInstance().getGameByToken(gametoken)== null){
            return null;
        }
        Game game= GameManager.getInstance().getGameByToken(gametoken);
        String auth = response.getHeader("Authorization");
        String playerToken = auth.substring(6);
        if (game.getPlayerByToken(playerToken) == null){
            return null;
        }
        return game.gameState();

    }


}
