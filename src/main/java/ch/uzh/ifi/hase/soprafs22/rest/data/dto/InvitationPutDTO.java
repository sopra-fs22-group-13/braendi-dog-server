package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvitationPutDTO {

    private Long inviteeID;

    public void setInviteeID(Long inviteeID) {
        this.inviteeID = inviteeID;
    }

    public Long getInviteeID() {
        return inviteeID;
    }
}
