package ch.uzh.ifi.hase.soprafs22.websocket;

import org.springframework.web.util.HtmlUtils;
import org.springframework.stereotype.Controller;

public class UpdateDTO {
    private String type;
    private String message;

    public UpdateDTO(){}

    public UpdateDTO(String type, String message)
    {
        this.type = type;
        this.message = message;
    }

    public String getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }
}
