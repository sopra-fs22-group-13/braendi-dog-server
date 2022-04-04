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

    //just for testing, This is not used
    //not removed yet as websocktes are weird and i do not want to read documentation again
    @MessageMapping("/update")
    @SendTo("/topic/update/test")
    public UpdateDTO websocketUpdate() throws Exception{
        messagingTemplate.convertAndSend("/topic/update/test", "LOL");
        Thread.sleep(1000); //for testing
        return new UpdateDTO(UpdateType.BOARD, "A test message.");
    }

    //for connecting to /gameupdates. the updates will be sent back to /user/usertoken/queue/specific-user
    // if the user sends a token in the header. The return here can be ignored by the client, as we expect the credentials to be correct.
    @MessageMapping("/gameupdates")
    public void sendSpecific(@Payload Message msg, Principal user, @Header("simpSessionId") String sessionId) throws Exception {
        log.warn("HELLO");
        String path = "/user" + "/queue/specific-user";
        messagingTemplate.convertAndSend(path, "Success");
    }

    /**
     * Sends an update to the user with a specific usertoken. This does not validate if the client actually listens to this,
     * it blindly sends data even if there is no receiver
     * @param usertoken the usertoken of the user to send to
     * @param updateMessage the content of the update
     */
    public void sendUpdateToUser(String usertoken, UpdateDTO updateMessage)
    {
        String path = "/user/" + usertoken + "/queue/specific-user";
        messagingTemplate.convertAndSend(path, updateMessage);
    }
}
