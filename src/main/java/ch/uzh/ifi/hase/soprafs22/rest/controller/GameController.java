package ch.uzh.ifi.hase.soprafs22.rest.controller;


import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.MovePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@RestController
public class GameController {

    private final UserService userService;
    private GameManager gameManager;

    GameController(UserService userService) {this.userService = userService;}

    @EventListener(ApplicationReadyEvent.class)
    public void start() { gameManager = GameManager.getInstance();
    }

    @GetMapping("/game/{gametoken}/board")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BoardData getGame( @PathVariable String gametoken, HttpServletRequest request) {
        User user = userService.checkIfLoggedIn(request);

        Game game = GameManager.getInstance().getGameByToken(gametoken);
        if (game==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A game with the provided token doesn't exist.");

        String auth = request.getHeader("Authorization");
        String playerToken = auth.substring(6);
        if (game.getPlayerByToken(playerToken) == null){
            return null;
        }
        return game.gameState();

    }

    @GetMapping("/game/{gametoken}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerData getPlayerHands(HttpServletRequest request, @PathVariable String gametoken) {
        User client = userService.checkIfLoggedIn(request);
        Game game = gameManager.getGameByToken(gametoken);

        return game.getPlayerStates(client.getToken());
    }

    /**TODO
     * authentication check pls
     * @param gametoken
     * @param movePutDTO
     */
    @PutMapping("/game/{gametoken}/board")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void playMove(@PathVariable String gametoken, @RequestBody MovePutDTO movePutDTO) {
        Move move = new Move(movePutDTO.get_fromPos(), movePutDTO.get_toPos(), movePutDTO.get_fromPosInGoal(), movePutDTO.get_toPosInGoal(), movePutDTO.getCard(), movePutDTO.getToken(), movePutDTO.getColor());

        Game game = gameManager.getGameByToken(gametoken);
        //if (game==null) then

        try {
            game.playerMove(move);
        } catch (InvalidMoveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid move request");
        }
    }
}
