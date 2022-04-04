package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.voicechat.requests.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that provides functions for certain room-specific API calls to SendBird.
 */
public class RoomManager {
    private final RequestGenerator requestGenerator = new RequestGenerator();
    private final Logger log = LoggerFactory.getLogger(RoomManager.class);

    /**
     * Sends an API request to create a room
     * @return the response text as a String
     */
    public String createRoom()
    {
        String response = requestGenerator.postRequest("rooms", "{\"type\":\"large_room_for_audio_only\"}", true);
        log.info(response);
        return response;
    }

    /**
     * Gets a SendBird Room by its Id
     * @param room_id
     * @return the response text as a String
     */
    public String getRoom(String room_id)
    {
        String response = requestGenerator.getRequest(String.format("rooms/%s", room_id), null, true);
        log.info(response);
        return response;
    }

    /**
     * Sends a DELETE API request to SendBird.
     * @param room_id
     * @return a boolean if the the request returned 204 (success)
     */
    public boolean deleteRoom(String room_id)
    {
        boolean result = requestGenerator.deleteRequest(String.format("rooms/%s", room_id), true);
        return result;
    }

}
