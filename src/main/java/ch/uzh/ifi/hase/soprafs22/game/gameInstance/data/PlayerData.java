package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import java.util.ArrayList;

/**
 * contains the cards of a player, along with the 3 hidden card amounts of the remaining players in order.
 */
public class PlayerData {
    private ArrayList<String> visibleCards;
    private ArrayList<Integer> hiddenCardCount;

    public ArrayList<String> getVisibleCards() {
        return visibleCards;
    }

    public void setVisibleCards(ArrayList<String> visibleCards) {
        this.visibleCards = visibleCards;
    }

    public ArrayList<Integer> getHiddenCardCount() {
        return hiddenCardCount;
    }

    public void setHiddenCardCount(ArrayList<Integer> hiddenCardCount) {
        this.hiddenCardCount = hiddenCardCount;
    }
}
