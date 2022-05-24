package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.LobbyData;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.InvitationPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.InvitationResponsePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@RestController
public class LobbyController {

    private UserService userService;
    private LobbyManager lobbyManager;

    @Autowired
    private Environment environment;

    LobbyController(UserService userService) {
        this.userService = userService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        if(Arrays.stream(environment.getActiveProfiles()).noneMatch(
                env -> (env.equalsIgnoreCase("test")) ))
        {
            lobbyManager = new LobbyManager();
        }
    }

    @GetMapping("/lobby/{lobbyID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyData getLobby(HttpServletRequest request, @PathVariable Integer lobbyID) {
        User client = userService.checkIfLoggedIn(request);
        Lobby lobby = lobbyManager.getLobbyByID(lobbyID);

        if(lobby == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby with this id cannot be found");
        }

        boolean inLobby = false;
        for (User user: lobby.getPlayers()) {
            if (Objects.equals(user.getToken(), client.getToken())) {
                inLobby = true;
                break;
            }
        }
        if (!inLobby) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not in this lobby.");

        LobbyData lobbyData = new LobbyData(lobby.getPlayers());
        if (lobbyData.getUserIDs().size() != lobbyData.getUsernames().size()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while fetching the lobby data.");
        }

        return lobbyData;
    }

    @PostMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(HttpServletRequest request) {
        User user = userService.checkIfLoggedIn(request);

        int lobbyID = lobbyManager.openLobby(user.getToken());

        return new LobbyGetDTO(lobbyID);
    }


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
    public void createGame(HttpServletRequest request, @RequestBody GamePostDTO gamePostDTO) {
        User client = userService.checkIfLoggedIn(request);
        Lobby gameLobby = lobbyManager.getLobbyByID(gamePostDTO.getLobbyID());
        if (gameLobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This lobby doesn't exist.");
        if (!Objects.equals(gameLobby.getOwner().getToken(), client.getToken())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be the owner to start the game.");

        lobbyManager.startGame(gamePostDTO.getLobbyID(), client.getToken());
    }

    @PutMapping("/lobby/{lobbyID}/invitations")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void invitePlayer(HttpServletRequest request, @PathVariable Integer lobbyID, @RequestBody InvitationPutDTO invitationPutDTO) {
        User client = userService.checkIfLoggedIn(request);
        User invitee = userService.getUserById(invitationPutDTO.getInviteeID());
        if (invitee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The player you're trying to invite doesn't exist");

        lobbyManager.invitePlayer(lobbyID, client.getToken(), invitee.getToken());
    }

    @PutMapping("/invitations")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void respondToInvitation(HttpServletRequest request, @RequestBody InvitationResponsePutDTO invitationResponsePutDTO) {
        User client = userService.checkIfLoggedIn(request);

        lobbyManager.inviteResponse(invitationResponsePutDTO.getLobbyID(), client.getToken(), invitationResponsePutDTO.getResponse());
    }

    
    public LobbyManager getLobbyManagerInstance(){
        return lobbyManager;
    }

    protected void insertLobbyManager(LobbyManager lobbyManager) {
        if(Arrays.stream(environment.getActiveProfiles()).anyMatch(
                env -> (env.equalsIgnoreCase("test")) ))
        {
            this.lobbyManager = lobbyManager;
        }
    }
}
