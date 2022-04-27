package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncVoiceChatDeletor extends Thread{
    private final Logger log = LoggerFactory.getLogger(AsyncVoiceChatDeletor.class);
    private String gameToken;
    private UpdateController updateController = SpringContext.getBean(UpdateController.class);

    public AsyncVoiceChatDeletor(String gameToken){
        this.gameToken = gameToken;
    }

    @Override
    public void run() {
        //delete a voiceChat with ID
        boolean success = VoiceChatCreator.getInstance().destroyRoomWithPlayers(gameToken);
        if(success)
        {
            log.info(String.format("Deleted Voice Room and Users from Chat with GameToken %s", gameToken));
        }else
        {
            log.info(String.format("Deletion Failed: Chat with GameToken %s could not be deleted completely", gameToken));
        }
    }
}
