package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyControllerTest {
    LobbyController lobbyController;

    @BeforeEach
    public void setup() {
        lobbyController = new LobbyController();
    }

    /**
     * Yeah, actually forget about this one. SpringContext throws an error and I can't really test anything without it.
     */
}