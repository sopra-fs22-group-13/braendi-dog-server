package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

import java.util.List;

public class PossibleMovesGetDTO {

    //provided by the client
    Integer marblePosition;
    Boolean isInGoal;
    String card;

    //this is filled out by the server
    List<Integer> possiblePositions;
    List<Boolean> positionIsInGoal;


    /**
     * These in theory shouldn't ever be used
     */

    public Integer getMarblePosition() {
        return marblePosition;
    }

    public void setMarblePosition(Integer marblePosition) {
        this.marblePosition = marblePosition;
    }

    public Boolean getInGoal() {
        return isInGoal;
    }

    public void setInGoal(Boolean inGoal) {
        isInGoal = inGoal;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }


    /**
     * These are used by the server
     */

    public List<Integer> getPossiblePositions() {
        return possiblePositions;
    }

    public void setPossiblePositions(List<Integer> possiblePositions) {
        this.possiblePositions = possiblePositions;
    }

    public List<Boolean> getPositionIsInGoal() {
        return positionIsInGoal;
    }

    public void setPositionIsInGoal(List<Boolean> positionIsInGoal) {
        this.positionIsInGoal = positionIsInGoal;
    }
}
