package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyData {
    private final ArrayList<Long> userIDs = new ArrayList<>();
    private final ArrayList<String> usernames = new ArrayList<>();

    public LobbyData(List<User> lobby) {
        for (User user: lobby) {
            userIDs.add(user.getId());
            usernames.add(user.getUsername());
        }
    }

    public ArrayList<Long> getUserIDs() {
        return userIDs;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }
}
