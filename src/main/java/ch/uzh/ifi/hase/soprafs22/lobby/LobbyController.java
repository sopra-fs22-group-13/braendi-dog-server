package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class LobbyController {

    private final GameCreator gameCreator = new GameCreator();
    private final UserRepository userRepository;

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

        return true;
    }
*/

    public void inviteResponse(Integer lobbyID, String playertoken, boolean response) throws ResponseStatusException{
        Lobby lobby = getLobbyByID(lobbyID);
        if (lobby == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby you want to join doesn't exist (anymore)");

        for (User player: lobby.getPendingInvites()) {
            if (player.getToken() == playertoken) {
                lobby.addPlayer(player);
                lobby.deleteInvite(player);
                return;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The player with this token has not been invited to this lobby");
    }

    public Lobby getLobbyByID(Integer id) {
        for (Lobby lobby: openLobbies) {
            if (lobby.getId() == id) return lobby;
        }
        return null;
    }

    public boolean startGame(Integer lobbyID, String playertoken) {
        Lobby lobby = getLobbyByID(lobbyID);
        if (!Objects.equals(lobby.getOwner().getToken(), playertoken)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to be the owner of the lobby in order to start the game");

        return gameCreator.CreateGame();
    }
}
