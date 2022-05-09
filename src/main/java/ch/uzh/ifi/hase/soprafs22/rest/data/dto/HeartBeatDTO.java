package ch.uzh.ifi.hase.soprafs22.rest.data.dto;
import ch.uzh.ifi.hase.soprafs22.heartbeat.HeartBeatType;


public class HeartBeatDTO {
    private HeartBeatType type;

    public void setType(HeartBeatType type) {
        this.type = type;
    }

    public HeartBeatType getType() {
        return type;
    }
}
