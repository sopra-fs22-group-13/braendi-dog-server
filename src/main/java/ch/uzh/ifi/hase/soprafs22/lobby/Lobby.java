package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lobby {
    private static int lobbyCount = 0;


    private int id;

    private final User owner;

    /**
     * decided to implement the list of users in a lobby as a single list with a fixed length instead of 4 separate entries.
     * owner still has its own entry, but will be added automatically to the list on creation.
     */
    private List<User> players = Arrays.asList(new User[4]);

    private List<User> pendingInvites = new ArrayList<>();


    public Lobby(User owner) {
        this.owner = owner;
        addPlayer(owner);

        lobbyCount++;
        this.id = lobbyCount;
    }

    /**
     * Adds a single player to the list of players.
     * New players will be added to the first empty slot
     * (in case there ever exists a way to remove a player from a lobby. Although, that would probably require other and better implementations)
     *
     * @return player number: int
     * returns the player number. the third player in a lobby is player Nr.3
     * @throws ResponseStatusException
     * throws an error if the lobby is already full
     */
    public int addPlayer(User newPlayer) {
        for (int i=0; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, newPlayer);
                newPlayer.setInLobby(true);
                return i+1;
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "This lobby is already full.");
    }

    public void addInvitee(User invitee) {
        pendingInvites.add(invitee);
    }

    public void deleteInvite(User player) {
        pendingInvites.remove(player);
    }

    /**
     * All getters and setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return this.owner;
    }

    public List<User> getPlayers() {
        List<User> playersCopy = new ArrayList<User>();
        for (User player: players) if (player != null) playersCopy.add(player);
        return playersCopy;
    }

    public List<User> getPendingInvites() {
        List<User> inviteesCopy = new ArrayList<User>();
        for (User invitee: pendingInvites) if (invitee != null) inviteesCopy.add(invitee);
        return inviteesCopy;
    }
}
