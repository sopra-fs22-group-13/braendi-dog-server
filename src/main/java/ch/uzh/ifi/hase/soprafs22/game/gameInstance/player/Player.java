package ch.uzh.ifi.hase.soprafs22.game.gameInstance.player;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import com.sun.istack.NotNull;

import java.util.ArrayList;

public class Player {

    private final ArrayList<Card> _hand = new ArrayList<Card>();
    private final COLOR _turn;

    public Player(COLOR playerColor) throws IllegalArgumentException{
        this._turn = playerColor;
    }

    public void removeCard(Card reqCard) {
        for (Card card: _hand) {
            if (reqCard.equalsContent(card)) {
                _hand.remove(card);
                return;
            }
        }
    }

    public boolean isCardAvailable(Card reqCard) {
        for (Card card: _hand) {
            if (reqCard.equalsContent(card)) return true;
        }

        return false;
    }

    public void addCard(Card card) {
        _hand.add(card);
    }

    public int getCardCount() {
        return _hand.size();
    }

    public ArrayList<String> getFormattedCards() {
        ArrayList<String> formattedCards = new ArrayList<>();
        for (Card card: _hand) formattedCards.add(card.getFormatted());
        return formattedCards;
    }

    public Card getCartValueInIndexHand(int i){
        return _hand.get(i);
    }

    public COLOR getTurn() {
        return this._turn;
    }

     public void removeAllCard(){
        _hand.clear();
     }

     public COLOR getColor(){
        return _turn;
     }
}
