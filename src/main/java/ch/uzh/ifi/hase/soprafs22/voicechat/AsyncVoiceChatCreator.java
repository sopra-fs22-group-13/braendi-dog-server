/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceRoom;
import ch.uzh.ifi.hase.soprafs22.voicechat.entities.VoiceUser;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a VoiceRoom and updates the Players in the lobby about it.
 */
public class AsyncVoiceChatCreator extends Thread{
    private Lobby lobby;
    private String gameToken;
    private UpdateController updateController = SpringContext.getBean(UpdateController.class);
    private final Logger log = LoggerFactory.getLogger(AsyncVoiceChatCreator.class);


    public AsyncVoiceChatCreator(Lobby lobby, String gameToken){
        this.lobby = lobby;
        this.gameToken = gameToken;
    }

    @Override
    public void run() {
        //create the voiceChat room
        //this only works if api.enabled is set to true in the environment
        VoiceRoom vr = VoiceChatCreator.getInstance().createRoomWithPlayers(gameToken);

        if(vr != null)
        {
            //send vc updates
            for (int i = 0; i < lobby.getPlayers().size(); i++) {
                User p = lobby.getPlayers().get(i);
                VoiceUser voiceUser;
                String userId;
                String userAuth;
                switch (i)
                {
                    case 0:
                        voiceUser = vr.player1;
                        break;
                    case 1:
                        voiceUser = vr.player2;
                        break;
                    case 2:
                        voiceUser = vr.player3;
                        break;
                    case 3:
                        voiceUser = vr.player4;
                        break;
                    default:
                        voiceUser = vr.player1;
                }

                String voiceJson = String.format("{\"app_id\": \"%s\", \"user_auth\": \"%s\", \"user_id\": \"%s\", \"room_id\": \"%s\"}", vr.appId, voiceUser.accessToken, voiceUser.id, vr.roomId);
                updateController.sendUpdateToUser(p.getToken(), new UpdateDTO(UpdateType.VOICE, voiceJson));
            }
            log.info(String.format("VoiceChat with GameToken %s was created", gameToken));
        }else
        {
            log.info(String.format("Deletion Failed: Chat with GameToken %s could not be deleted completely", gameToken));
        }
    }
}
