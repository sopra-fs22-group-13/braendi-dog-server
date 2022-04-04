package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;

public class Card {

    private CARDVALUE value;
    private CARDSUITE suite;
    private CARDTYPE type;

    public Card(CARDVALUE value, CARDTYPE type, CARDSUITE suite) {
        this.value = value;
        this.type = type;
        this.suite = suite;
    }

    // gets the move distance depending on the move
    public int getCorrespondingMoveDistance() {
        
        return 0;
    }

    public boolean isJoker() {
        return type == CARDTYPE.JOKER;
    }

    public boolean isAce() {
        return value == CARDVALUE.ACE;
    }

    public boolean isSeven() {
        return value == CARDVALUE.SEVEN;
    }

    public boolean isFour() {
        return value == CARDVALUE.FOUR;
    }

    public boolean canOpenMove() {
        if(value == CARDVALUE.ACE || value == CARDVALUE.KING || value == CARDVALUE.JOKER) {
            return true;
        }else {
            return false;
        }
    }
}
