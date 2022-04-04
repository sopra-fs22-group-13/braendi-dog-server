package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Lobby {

    @Id
    @GeneratedValue
    private int id;

    private final User owner;

    /**
     * decided to implement the list of users in a lobby as a single list with a fixed length instead of 4 separate entries.
     * owner still has its own entry, but will be added automatically to the list on creation.
     * while this implementation definitely has its own flaws, I still think it's a bit cleaner, makes some methods a bit easier to implement and easier to maintain
     */
    private List<User> players = Arrays.asList(new User[4]);

    private List<User> pendingInvites = new ArrayList<>();


    public Lobby(User owner) {
        this.owner = owner;
        addPlayer(owner);
    }

    /**
     * Adds a single player to the list of players.
     * New players will be added to the first empty slot
     * (in case there ever exists a way to remove a player from a lobby. Although, that would probably require other and better implementations)
     *
     * @return player number: int
     * returns the player number. the third player in a lobby is player Nr.3
     * @throws null
     * currently doesn't throw anything at all. definitely should though
     */
    public int addPlayer(User newPlayer) {
        for (int i=0; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, newPlayer);
                return i+1;
            }
        }
        return -1;
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

    /** TODO
     *  make this return a copy pls
     * @return
     */
    public List<User> getPlayers() {
        return players;
    }

    /** TODO
     *  make this return a copy pls
     * @return
     */
    public List<User> getPendingInvites() {
        return pendingInvites;
    }
}
