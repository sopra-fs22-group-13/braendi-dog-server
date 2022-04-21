package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

public class InvitationPutDTO {

    private final Long inviteeID;

    public InvitationPutDTO(Long inviteeID) {
        this.inviteeID = inviteeID;
    }

    public Long getInviteeID() {
        return inviteeID;
    }
}
