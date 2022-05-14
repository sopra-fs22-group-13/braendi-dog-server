package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

import java.util.List;

public class PossibleMovesGetDTO {

    //provided by the client
    Integer index;
    Boolean inGoal;
    String card;

    public PossibleMovesGetDTO(int index, boolean inGoal) {
        this.index = index;
        this.inGoal = inGoal;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer marblePosition) {
        this.index = marblePosition;
    }

    public Boolean getInGoal() {
        return inGoal;
    }

    public void setInGoal(Boolean inGoal) {
        inGoal = inGoal;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
