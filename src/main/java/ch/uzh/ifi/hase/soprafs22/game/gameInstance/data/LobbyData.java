/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyData {
    private final ArrayList<Long> userIDs = new ArrayList<>();
    private final ArrayList<String> usernames = new ArrayList<>();
    private final ArrayList<Integer> avatars = new ArrayList<>();

    public LobbyData(List<User> lobby) {
        for (User user: lobby) {
            userIDs.add(user.getId());
            usernames.add(user.getUsername());
            avatars.add(user.getAvatar());
        }
    }

    public ArrayList<Long> getUserIDs() {
        return userIDs;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }
    public ArrayList<Integer> getAvatars() {
        return avatars;
    }
}
