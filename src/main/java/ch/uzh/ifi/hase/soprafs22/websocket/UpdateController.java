package ch.uzh.ifi.hase.soprafs22.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.util.HtmlUtils;

@Controller
public class UpdateController {
    @MessageMapping("/update")
    @SendTo("/topic/update/test")
    public UpdateDTO websocketUpdate() throws Exception{
        Thread.sleep(1000); //for testing
        return new UpdateDTO("TEST", "A test message.");
    }
}
