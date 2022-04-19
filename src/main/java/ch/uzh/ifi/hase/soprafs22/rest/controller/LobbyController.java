package ch.uzh.ifi.hase.soprafs22.rest.controller;

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
public class LobbyController {

    private final UserService userService;
    private LobbyManager lobbyManager;

    LobbyController(UserService userService) {
        this.userService = userService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        lobbyManager = new LobbyManager();
    }

    @PostMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(HttpServletRequest response) {
        User user = userService.checkIfLoggedIn(response);
        if (user==null) return null;

        int lobbyID = lobbyManager.openLobby(user.getToken());

        return new LobbyGetDTO(lobbyID);
    }

    /** TODO
     * still needs tests
     */
    @DeleteMapping("/lobby/{lobbyID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteLobby(@PathVariable Integer lobbyID, HttpServletRequest request) {
        User client = userService.checkIfLoggedIn(request);
        Lobby lobbyToBeDeleted = lobbyManager.getLobbyByID(lobbyID);
        if (lobbyToBeDeleted == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This lobby doesn't exist.");
        if (!Objects.equals(lobbyToBeDeleted.getOwner().getToken(), client.getToken())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be the owner to delete a lobby.");

        lobbyManager.closeLobby(lobbyID);
    }


    @PostMapping("/game")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createGame(HttpServletRequest response, Integer lobbyID) {
        User client = userService.checkIfLoggedIn(response);
        Lobby gameLobby = lobbyManager.getLobbyByID(lobbyID);
        if (gameLobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This lobby doesn't exist.");
        if (!Objects.equals(gameLobby.getOwner().getToken(), client.getToken())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be the owner to start the game.");

        lobbyManager.startGame(lobbyID, client.getToken());
    }
}
