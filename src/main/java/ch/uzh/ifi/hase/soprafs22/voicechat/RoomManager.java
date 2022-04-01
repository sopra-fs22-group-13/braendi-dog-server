package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.voicechat.requests.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomManager {
    private final RequestGenerator requestGenerator = new RequestGenerator();
    private final Logger log = LoggerFactory.getLogger(RoomManager.class);

    public String createRoom()
    {
        String response = requestGenerator.postRequest("rooms", "{\"type\":\"small_room_for_video\"}", true);
        log.info(response);
        return response;
    }

    public String getRoom(String room_id)
    {
        String response = requestGenerator.getRequest(String.format("rooms/%s", room_id), null, true);
        log.info(response);
        return response;
    }

    public boolean deleteRoom(String room_id)
    {
        boolean result = requestGenerator.deleteRequest(String.format("rooms/%s", room_id), true);
        return result;
    }

}
