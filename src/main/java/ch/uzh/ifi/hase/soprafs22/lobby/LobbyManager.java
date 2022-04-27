package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.voicechat.AsyncVoiceChatCreator;
import ch.uzh.ifi.hase.soprafs22.voicechat.VoiceChatCreator;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceRoom;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceUser;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LobbyManager {

    private final GameCreator gameCreator = new GameCreator();
    private final UserRepository userRepository = SpringContext.getBean(UserRepository.class);
    private final IUpdateController updateController = SpringContext.getBean(UpdateController.class);


    private final List<Lobby> openLobbies = new ArrayList<>();


    public Integer openLobby(String usertoken) {
        User owner = userRepository.findByToken(usertoken);
        if (owner == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't create lobby because couldn't find owner.");

        Lobby newLobby = new Lobby(owner);
        openLobbies.add(newLobby);

        updatePlayers(newLobby, UpdateType.LOBBY);

        return newLobby.getId();
    }

    public void closeLobby(Integer lobbyID) {
        Lobby lobbyToBeDeleted = getLobbyByID(lobbyID);
        List<User> players = lobbyToBeDeleted.getPlayers();

        openLobbies.remove(lobbyToBeDeleted);

        /** TODO
         * Not sure how this works in this case. Needs to be done before this method is actually usable.
         */
        /*
        UpdateDTO updateDTO = new UpdateDTO(updateType, "");
        for (User player: players) updateController.sendUpdateToUser(player.getToken(), updateDTO);
         */
    }


    public void invitePlayer(Integer lobbyID, String ownerToken, String playertoken) throws ResponseStatusException{
        Lobby lobby = getLobbyByID(lobbyID);

        if (lobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby you want to join doesn't exist (anymore)");
        if (!Objects.equals(lobby.getOwner().getToken(), ownerToken)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to be the lobby owner in order to invite other players");

        User invitee = userRepository.findByToken(playertoken);
        if (invitee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The player you're trying to invite doesn't exist");

        if (!lobby.getPendingInvites().contains(invitee)) lobby.addInvitee(invitee);

        UpdateDTO updateDTO = new UpdateDTO(UpdateType.INVITE, String.format("{\"lobbyId\": %s, \"ownerName\": \"%s\"}", lobbyID, lobby.getOwner().getUsername()));
        updateController.sendUpdateToUser(playertoken, updateDTO);
    }

    public void inviteResponse(Integer lobbyID, String playertoken, boolean response) throws ResponseStatusException{
        Lobby lobby = getLobbyByID(lobbyID);
        if (lobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby you want to join doesn't exist (anymore)");

        for (User player: lobby.getPendingInvites()) {
            if (Objects.equals(player.getToken(), playertoken)) {
                //adds the player who responded to the invite to the lobby if they accepted, removes them from the invites-list either way
                if (response) {
                    lobby.addPlayer(player);
                    updatePlayers(lobby, UpdateType.LOBBY);
                }
                lobby.deleteInvite(player);

                return;
            }
        }

        //only reachable if the player is not in the list of invites
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You have not been invited to this lobby");
    }

    public Lobby getLobbyByID(Integer id) {
        for (Lobby lobby: openLobbies) {
            if (lobby.getId() == id) return lobby;
        }
        return null;
    }

    public void startGame(Integer lobbyID, String ownerToken) {
        Lobby lobby = getLobbyByID(lobbyID);
        if (!Objects.equals(lobby.getOwner().getToken(), ownerToken)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to be the owner of the lobby in order to start the game");
        if (lobby.getPlayers().size() < 4) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The lobby needs to be full. You can't start the game right now.");

        //create Game
        String newGameToken = gameCreator.createGame(lobby);
        //create VC (if possible) ASYNC
        //this will also send the update
        Thread vcThread = new AsyncVoiceChatCreator(lobby, newGameToken);
        vcThread.start();

        String json = String.format("{\"gameToken\": \"%s\"}", newGameToken);

        updatePlayers(lobby, UpdateType.START, json);
    }


    private void updatePlayers(Lobby lobby, UpdateType updateType) {
        updatePlayers(lobby, updateType, "");
    }

    private void updatePlayers(Lobby lobby, UpdateType updateType, String message) {
        UpdateDTO updateDTO = new UpdateDTO(updateType, message);

        for (User player: lobby.getPlayers()) updateController.sendUpdateToUser(player.getToken(), updateDTO);
    }
}
