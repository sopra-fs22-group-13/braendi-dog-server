package ch.uzh.ifi.hase.soprafs22.mocks;

import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;

/**
 * Can be used instead of the normal UpdateController in a MockSpringContext mapping.
 */
public abstract class MockUpdateController implements IUpdateController {

    @Override
    public void sendUpdateToUser(String usertoken, UpdateDTO updateMessage) {
        return;
    }
}
