package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

/**
 * returned object on lobby creation
 */
public class LobbyGetDTO {
    private final int lobbyID;

    public LobbyGetDTO(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    public int getLobbyID() {
        return this.lobbyID;
    }
}
