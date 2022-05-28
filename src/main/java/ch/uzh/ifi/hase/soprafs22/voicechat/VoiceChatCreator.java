/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

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

package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceRoom;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class that has common functions for creating and deleting voice chats with 4 members.
 */
public class VoiceChatCreator {

    private static VoiceChatCreator instance = null;

    private final UserManager userManager = new UserManager();
    private final RoomManager roomManager = new RoomManager();

    private final Logger log = LoggerFactory.getLogger(VoiceChatCreator.class);

    private static Map<String, VoiceRoom> rooms = new HashMap<>();

    ObjectMapper objectMapper = new ObjectMapper();

    public synchronized static VoiceChatCreator getInstance()
    {
        if(instance == null)
        {
            instance = new VoiceChatCreator();
        }
        return instance;
    }

    private VoiceChatCreator(){}

    /**
     * Creats a room with 4 players
     * @param gametoken the gameToken to reference later for deletion (if the VoiceRoom object is lost)
     * @return the created VoiceRoom information
     */
    public VoiceRoom createRoomWithPlayers(String gametoken)
    {
        if(!new ApiAuth().vcEnabled())
        {
            return null;
        }

        VoiceRoom vr =  new VoiceRoom();
        try {
            //create room
            JsonNode jsonNode = objectMapper.readTree(roomManager.createRoom());
            String room_id = jsonNode.get("room").get("room_id").asText();
            vr.roomId = room_id;

            vr.appId = new ApiAuth().getId();

            //create 4 players
            vr.player1 = createRandomUser();
            vr.player2 = createRandomUser();
            vr.player3 = createRandomUser();
            vr.player4 = createRandomUser();
        }
        catch (JsonProcessingException e) {
            return null;
        }

        rooms.put(gametoken, vr);
        return  vr;
    }

    /**
     * Calls delete requests on the room and all 4 users using this room.
     * If any of those users are also in another room, they will be disconnected.
     * @param room
     * @return boolean if every deletion request succeeded
     */
    public boolean destroyRoomWithPlayers(VoiceRoom room)
    {
        boolean success = roomManager.deleteRoom(room.roomId);
        if(room.player1.id != null)
        {
            success = userManager.deleteUser(room.player1.id) ? success : false ;
        }
        if(room.player2.id != null)
        {
            success = userManager.deleteUser(room.player2.id) ? success : false ;
        }
        if(room.player3.id != null)
        {
            success = userManager.deleteUser(room.player3.id) ? success : false ;
        }
        if(room.player4.id != null)
        {
            success = userManager.deleteUser(room.player4.id) ? success : false ;
        }

        return success;
    }

    /**
     * OVERLOAD: Calls delete requests on the room and all 4 users using a room with this gametoken
     * @param gametoken the gametoken to search a room for
     * @return boolean if the room was found and deleted successfully.
     */
    public boolean destroyRoomWithPlayers(String gametoken)
    {
        VoiceRoom room = rooms.get(gametoken);
        if(room == null)
        {
            log.info("Failed early, could not get ", gametoken);
            return false;
        }

        return destroyRoomWithPlayers(room);
    }

    /**
     * Creates a random SendBird user with a random name
     * @return the created Users information
     * @throws JsonProcessingException if the user could not be created and therefore not parsed correctly.
     */
    private VoiceUser createRandomUser() throws JsonProcessingException {
        JsonNode player = null;
        player = objectMapper.readTree(userManager.createUser(UUID.randomUUID().toString(), "test"));
        VoiceUser user = new VoiceUser();
        user.id = player.get("user_id").asText();
        user.name = player.get("nickname").asText();
        user.accessToken = player.get("access_token").asText();
        user.appId = new ApiAuth().getId();
        return user;

    }

}
