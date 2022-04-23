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
public class UpdateController implements IUpdateController {

    private final Logger log = LoggerFactory.getLogger(UpdateController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //for connecting to /gameupdates. the updates will be sent back to /user/usertoken/queue/specific-user
    @MessageMapping("/gameupdates")
    public void sendSpecific() throws Exception {
        log.info("Websocket: New Incoming Connection");
    }

    /**
     * Sends an update to the user with a specific usertoken. This does not validate if the client actually listens to this,
     * it blindly sends data even if there is no receiver
     * @param usertoken the usertoken of the user to send to
     * @param updateMessage the content of the update
     */
    public void sendUpdateToUser(String usertoken, UpdateDTO updateMessage)
    {
        log.info(String.format("updating user %s with updateType %s and message: %s", usertoken, updateMessage.getType().toString(), updateMessage.getMessage()));
        String path = "/user/" + usertoken + "/queue/specific-user";
        messagingTemplate.convertAndSend(path, updateMessage);
    }
}
