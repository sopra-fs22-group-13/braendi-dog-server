package ch.uzh.ifi.hase.soprafs22.websocket.dto;

import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import org.springframework.web.util.HtmlUtils;
import org.springframework.stereotype.Controller;

public class UpdateDTO {
    private UpdateType type;
    private String message;

    public UpdateDTO(){}

    public UpdateDTO(UpdateType type, String message)
    {
        this.type = type;
        this.message = message;
    }

    public UpdateType getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }
}
