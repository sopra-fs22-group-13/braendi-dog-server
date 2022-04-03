package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class LobbyController {

    private final GameCreator gameCreator = new GameCreator();
    private final UserRepository userRepository;

    /**
     * I'm not quite sure how things work with spring, e.g. how this class willbe created and how it gets access to the UserRepository. Without that many methods in this controller won't work properly.
     * Also, the UserRepository will need a method to get a User by its Usertoken. I didn't implement that yet, since it would need some proper testing (and again I don't really know how that Repo works either).
     * @param userRepository
     */
    public LobbyController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final List<Lobby> openLobbies = new ArrayList<>();



    /**
     * needs other implementations
     */
/*
    public boolean openLobby(String usertoken) {
        User owner = userRepository.findByToken(usertoken);
        if (owner == null) { throw an exception }

        Lobby newLobby = new Lobby(owner);
        openLobbies.add(newLobby);

        updatePlayers(lobby, UpdateType.LOBBY);

        return true;
    }
*/

    public void invitePlayer(Integer lobbyID, String ownerToken, String playertoken) throws ResponseStatusException{
        Lobby lobby = getLobbyByID(lobbyID);
        if (lobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby you want to join doesn't exist (anymore)");
        if (!Objects.equals(lobby.getOwner().getToken(), ownerToken)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to be the lobby owner in order to invite other players");

        /**
         * needs userRepo and findByToken to be implemented
         */
        User invitee = new User();
        //invitee = userRepository.findByToken(playertoken);
        if (invitee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The player you're trying to invite doesn't exist");

        lobby.addInvitee(invitee);

        updatePlayers(lobby, UpdateType.LOBBY);
    }

    public void inviteResponse(Integer lobbyID, String playertoken, boolean response) throws ResponseStatusException{
        Lobby lobby = getLobbyByID(lobbyID);
        if (lobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby you want to join doesn't exist (anymore)");

        for (User player: lobby.getPendingInvites()) {
            if (Objects.equals(player.getToken(), playertoken)) {
                //adds the player who responded to the invite to the lobby if they accepted, removes them from the invites-list either way
                if (response) {
                    lobby.addPlayer(player);
                }
                lobby.deleteInvite(player);

                updatePlayers(lobby, UpdateType.LOBBY);

                return;
            }
        }

        //only reachable if the player is not in the list of invites
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The player with this token has not been invited to this lobby");
    }

    /**
     * Not sure what getLobbyInfo() (see class diagram) was initially intended for.
     * I can't think of a case where it would be easier to get some formatted bundle of data instead of just the lobby object.
     * Feel free to adjust this if needed (I'm the one who writes the REST anyway, lol)
     */
    public Lobby getLobbyByID(Integer id) {
        for (Lobby lobby: openLobbies) {
            if (lobby.getId() == id) return lobby;
        }
        return null;
    }

    public boolean startGame(Integer lobbyID, String ownerToken) {
        Lobby lobby = getLobbyByID(lobbyID);
        if (!Objects.equals(lobby.getOwner().getToken(), ownerToken)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to be the owner of the lobby in order to start the game");

        updatePlayers(lobby, UpdateType.START);

        return gameCreator.CreateGame();
    }

    private void updatePlayers(Lobby lobby, UpdateType updateType) {
        /**
         * The way I understand how the Update stuff works, the message would need to be JSON-formatted.
         * Not sure if it would make sense to actually send a message here (given that an empty string works at all)
         */
        UpdateDTO updateDTO = new UpdateDTO(updateType, "");

        /**
         * Is there a universal UpdateController? How do I get it?
         */
        UpdateController updateController = new UpdateController();

        for (User player: lobby.getPlayers()) updateController.sendUpdateToUser(player.getToken(), updateDTO);
    }
}
