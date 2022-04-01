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

public class VoiceChatCreator {
    private final UserManager userManager = new UserManager();
    private final RoomManager roomManager = new RoomManager();

    private final Logger log = LoggerFactory.getLogger(VoiceChatCreator.class);

    private static Map<String, VoiceRoom> rooms = new HashMap<>();

    ObjectMapper objectMapper = new ObjectMapper();

    public VoiceRoom createRoomWithPlayers(String gametoken)
    {
        VoiceRoom vr =  new VoiceRoom();
        try {
            //create room
            JsonNode jsonNode = objectMapper.readTree(roomManager.createRoom());
            String room_id = jsonNode.get("room").get("room_id").asText();
            vr.roomId = room_id;

            //create 4 players
            vr.player1 = createRandomUser();
            vr.player2 = createRandomUser();
            vr.player3 = createRandomUser();
            vr.player4 = createRandomUser();
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        rooms.put(gametoken, vr);
        return  vr;
    }

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

    private VoiceUser createRandomUser() throws JsonProcessingException {
        JsonNode player = null;
        player = objectMapper.readTree(userManager.createUser(UUID.randomUUID().toString(), "test"));
        VoiceUser user = new VoiceUser();
        user.id = player.get("user_id").asText();
        user.name = player.get("nickname").asText();
        user.accessToken = player.get("access_token").asText();
        return user;

    }

}
