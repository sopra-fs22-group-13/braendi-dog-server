package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.LobbyGetDTO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {

    private LobbyManager lobbyManager;

    @EventListener(ApplicationReadyEvent.class)
    public void schlau() {
        lobbyManager = new LobbyManager();
    }

    @PostMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(@RequestBody String usertoken) {
        /**
         * check if usertoken makes sense
         * e.g. CheckIfLoggedIn
         */

        int lobbyID = lobbyManager.openLobby(usertoken);

        return new LobbyGetDTO(lobbyID);
    }

}
