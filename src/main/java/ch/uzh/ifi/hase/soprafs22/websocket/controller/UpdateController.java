package ch.uzh.ifi.hase.soprafs22.websocket.controller;

import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import java.security.Principal;

@Component
public class UpdateController {

    private final Logger log = LoggerFactory.getLogger(UpdateController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/update")
    @SendTo("/topic/update/test")
    public UpdateDTO websocketUpdate() throws Exception{
        messagingTemplate.convertAndSend("/topic/update/test", "LOL");
        Thread.sleep(1000); //for testing
        return new UpdateDTO(UpdateType.BOARD, "A test message.");
    }

    @MessageMapping("/gameupdates")
    public void sendSpecific(@Payload Message msg, Principal user, @Header("simpSessionId") String sessionId) throws Exception {
        String path = "/user/" + sessionId + "/queue/specific-user";
        messagingTemplate.convertAndSend(path, "Success");
    }

    public void sendUpdateToUser(String usertoken, UpdateDTO updateMessage)
    {
        String path = "/user/" + usertoken + "/queue/specific-user";
        messagingTemplate.convertAndSend(path, updateMessage);
    }
}
