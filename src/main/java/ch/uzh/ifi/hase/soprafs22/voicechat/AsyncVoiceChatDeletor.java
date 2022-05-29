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
