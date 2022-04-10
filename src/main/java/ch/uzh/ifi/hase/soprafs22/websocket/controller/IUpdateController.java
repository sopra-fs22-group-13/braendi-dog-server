package ch.uzh.ifi.hase.soprafs22.websocket.controller;

import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;

public interface IUpdateController {
    void sendUpdateToUser(String usertoken, UpdateDTO updateMessage);
}
