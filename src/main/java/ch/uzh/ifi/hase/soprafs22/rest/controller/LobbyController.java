package ch.uzh.ifi.hase.soprafs22.rest.controller;

import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.LobbyGetDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {

    //private final LobbyManager lobbyManager = new LobbyManager();

    @PostMapping("/menu/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(@RequestBody String usertoken) {

        //int lobbyID = lobbyManager.openLobby(usertoken);

        return new LobbyGetDTO(17);
    }

}
