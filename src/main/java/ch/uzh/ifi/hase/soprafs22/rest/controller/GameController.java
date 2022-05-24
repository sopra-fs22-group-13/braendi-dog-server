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
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.MovePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.PossibleMovesGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment environment;

    GameController(UserService userService) {this.userService = userService;}

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        if(Arrays.stream(environment.getActiveProfiles()).noneMatch(
                env -> (env.equalsIgnoreCase("test")) ))
        {
            gameManager = GameManager.getInstance();
        }
    }

    @GetMapping("/game/{gametoken}/board")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BoardData getGame( @PathVariable String gametoken, HttpServletRequest request) {
        User user = userService.checkIfLoggedIn(request);

        Game game = gameManager.getGameByToken(gametoken);
        if (game==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A game with the provided token doesn't exist.");

        String auth = request.getHeader("Authorization");
        String playerToken = auth.substring(6);
        if (game.getPlayerByToken(playerToken) == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a player in this game.");
        }
        return game.gameState();

    }

    @GetMapping("/game/{gametoken}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerData getPlayerHands(HttpServletRequest request, @PathVariable String gametoken) {
        User client = userService.checkIfLoggedIn(request);
        Game game = gameManager.getGameByToken(gametoken);
        if (game==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A game with the provided token doesn't exist.");

        return game.getPlayerStates(client.getToken());
    }

    @PutMapping("/game/{gametoken}/board")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void playMove(@PathVariable String gametoken, @RequestBody MovePutDTO movePutDTO) {

        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        if(movePutDTO.get_fromPos().size() != movePutDTO.get_fromPosInGoal().size() || movePutDTO.get_toPos().size() != movePutDTO.get_toPosInGoal().size()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid move request: Malformed move");
        }

        for (int i = 0; i < movePutDTO.get_fromPos().size(); i++) {
            from.add(new BoardPosition(movePutDTO.get_fromPos().get(i), movePutDTO.get_fromPosInGoal().get(i)));
        }
        for (int i = 0; i < movePutDTO.get_toPos().size(); i++) {
            to.add(new BoardPosition(movePutDTO.get_toPos().get(i), movePutDTO.get_toPosInGoal().get(i)));
        }
        
        Move move = new Move(from, to, new Card(movePutDTO.getCard()), movePutDTO.getToken(), movePutDTO.getColor(), movePutDTO.getCardIsPartOfJoker());

        Game game = gameManager.getGameByToken(gametoken);
        if (game==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A game with the provided token doesn't exist.");

        try {
            game.playerMove(move);
        } catch (InvalidMoveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid move request: " + e.getMessage());
        }
    }

    @GetMapping("/game/{gametoken}/moves")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<BoardPosition> getPossibleMoves(HttpServletRequest request, @PathVariable String gametoken, @RequestBody PossibleMovesGetDTO possibleMovesGetDTO) {
        User user = userService.checkIfLoggedIn(request);

        Game game = GameManager.getInstance().getGameByToken(gametoken);
        if (game==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A game with the provided token doesn't exist.");

        String auth = request.getHeader("Authorization");
        String playerToken = auth.substring(6);
        Player player = game.getPlayerByToken(playerToken);
        if (player == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a player in this game.");
        }


        List<BoardPosition> possibleMoves = game.getPossibleMoves(
                new BoardPosition(possibleMovesGetDTO.getIndex(), possibleMovesGetDTO.getInGoal()),
                new Card(possibleMovesGetDTO.getCard()),
                player.getColor()
                );
        //List<PossibleMovesGetDTO> possibleMoveDTOs = (List<PossibleMovesGetDTO>) possibleMoves.stream().map(boardPosition -> new PossibleMovesGetDTO(boardPosition.getIndex(), boardPosition.isInGoal()));

        return possibleMoves;
    }


    protected void insertGameManager(GameManager gameManager) {
        if(Arrays.stream(environment.getActiveProfiles()).anyMatch(
                env -> (env.equalsIgnoreCase("test")) ))
        {
            this.gameManager = gameManager;
        }
    }
}
