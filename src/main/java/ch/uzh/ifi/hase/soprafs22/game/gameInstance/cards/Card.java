package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;

public class Card {

    private final CARDVALUE value;
    private final CARDSUITE suite;
    private final CARDTYPE type;

    public Card(CARDVALUE value, CARDTYPE type, CARDSUITE suite) {
        this.value = value;
        this.type = type;
        this.suite = suite;
    }

    public Card(String card){
        if(card.equals("JOKER")){
            this.value = CARDVALUE.JOKER;
            this.type = CARDTYPE.JOKER;
            this.suite = null;
        }else{
            String[] splitString = card.split("");
            switch(splitString[0]){
                case "C":
                    this.suite = CARDSUITE.CLUBS;
                    break;
                case "D":
                    this.suite = CARDSUITE.DIAMOND;
                    break;
                case "H":
                    this.suite = CARDSUITE.HEARTS;
                    break;
                case "S":
                    this.suite = CARDSUITE.SPADES;
                    break;
                default:
                    this.suite = null;
                    break;
            }
            switch(splitString[1]){
                case "2":
                    this.value = CARDVALUE.TWO;
                    break;
                case "3":
                    this.value = CARDVALUE.THREE;
                    break;
                case "4":
                    this.value = CARDVALUE.FOUR;
                    break;
                case "5":
                    this.value = CARDVALUE.FIVE;
                    break;
                case "6":
                    this.value = CARDVALUE.SIX;
                    break;
                case "7":
                    this.value = CARDVALUE.SEVEN;
                    break;
                case "8":
                    this.value = CARDVALUE.EIGHT;
                    break;
                case "9":
                    this.value = CARDVALUE.NINE;
                    break;
                case "10":
                    this.value = CARDVALUE.TEN;
                    break;
                case "J":
                    this.value = CARDVALUE.JACK;
                    break;
                case "Q":
                    this.value = CARDVALUE.QUEEN;
                    break;
                case "K":
                    this.value = CARDVALUE.KING;
                    break;
                case "A":
                    this.value = CARDVALUE.ACE;
                    break;
                default:
                    this.value = null;
                    break;
            }
            this.type = CARDTYPE.DEFAULT;
        }

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
        return value == CARDVALUE.ACE || value == CARDVALUE.KING || value == CARDVALUE.JOKER;
    }

    public CARDSUITE getSuite() {
        return this.suite;
    }

    public CARDVALUE getValue() {
        return this.value;
    }

    public CARDTYPE getType() {
        return this.type;
    }

    /**
     * compares cards not by their reference, but by apparent equality
     * @return true if both cards are equal ignoring their references
     */
    public boolean equalsContent(Card card) {
        return this.suite == card.getSuite() && this.type == card.getType() && this.value == card.getValue();
    }

    public String getFormatted() {
        String formatted = "";
        switch (this.value) {
            case TWO -> formatted += "2";
            case THREE -> formatted += "3";
            case FOUR -> formatted += "4";
            case FIVE -> formatted += "5";
            case SIX -> formatted += "6";
            case SEVEN -> formatted += "7";
            case EIGHT -> formatted += "8";
            case NINE -> formatted += "9";
            case TEN -> formatted += "10";
            case JACK -> formatted += "J";
            case QUEEN -> formatted += "Q";
            case KING -> formatted += "K";
            case ACE -> formatted += "A";
            case JOKER -> { formatted += "Joker"; return formatted; }
        }
        switch (this.suite) {
            case CLUBS -> formatted += "C";
            case HEARTS -> formatted += "H";
            case SPADES -> formatted += "S";
            case DIAMOND -> formatted += "D";
        }
        return formatted;
    }
}
