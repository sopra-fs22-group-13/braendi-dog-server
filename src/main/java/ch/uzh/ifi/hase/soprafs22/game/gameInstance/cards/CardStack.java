package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStack {

    private final List<Card> deck = new ArrayList<>();

    public CardStack() {
        for (CARDSUITE suite: CARDSUITE.values()) {
            for (CARDVALUE value: CARDVALUE.values()) {
                deck.add(new Card(value, CARDTYPE.DEFAULT, suite));
            }
        }

        shuffle();
    }

    public Card getNextCard() {
        Card c =  this.deck.remove(0);
        if(this.deck.size() == 0)
        {
            restock();
        }

        return c;
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    public List<Card> getDeck() {
        return new ArrayList<Card>(deck);
    }

    private void restock()
    {
        deck.clear();
        for (CARDSUITE suite: CARDSUITE.values()) {
            for (CARDVALUE value: CARDVALUE.values()) {
                deck.add(new Card(value, CARDTYPE.DEFAULT, suite));
            }
        }
        shuffle();
    }
}
