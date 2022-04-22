package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

public class InvitationResponsePutDTO {

    private final Integer lobbyID;
    private final boolean response;

    public InvitationResponsePutDTO(Integer lobbyID, boolean response) {
        this.lobbyID = lobbyID;
        this.response = response;
    }

    public Integer getLobbyID() {
        return lobbyID;
    }

    public boolean getResponse() {
        return response;
    }
}
