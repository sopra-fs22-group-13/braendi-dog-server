package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lobby {

    /**
     * Don't really understand how this works yet. Someone mind explaining it to me?
     * Signed, Anton
     */
    @Id
    @GeneratedValue
    private int id;

    private final User owner;

    /**
     * decided to implement the list of users in a lobby as a single list with a fixed length instead of 4 separate entries.
     * owner still has its own entry, but will be added automatically to the list on creation.
     * while this implementation definitely has its own flaws, I still think it's a bit cleaner, makes some methods a bit easier to implement and easier to maintain
     *
     * ideally we discuss this matter at our next meeting. blame me (Anton) if I forget about it
     */
    private List<User> players = Arrays.asList(new User[4]);

    /**
     * class diagram says this should be a list of ints. userIDs?
     */
    private List<Integer> pendingInvites = new ArrayList<>();


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



    /**
     * All getters and setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<User> getPlayers() {
        return players;
    }

    public List<Integer> getPendingInvites() {
        return pendingInvites;
    }
}
