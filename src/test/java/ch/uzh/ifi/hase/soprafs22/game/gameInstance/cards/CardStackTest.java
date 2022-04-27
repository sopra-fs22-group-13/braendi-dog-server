package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardStackTest {
    CardStack cardStack;

    @BeforeEach
    public void setup() {
        cardStack = new CardStack();
    }

    //todo revert this to a valid test when we add all cards back
    //@Test
    public void creationTest() {
        assertEquals(56, cardStack.getDeck().size());
    }

    /**
     * draws a card, checks if the deck size shrunk and if the card is no longer found in the deck
     */
    //todo revert this to a valid test when we add all cards back
    //@Test
    public void drawCardTest() {
        Card drawnCard = cardStack.getNextCard();
        assertEquals(55, cardStack.getDeck().size());
        for (Card card: cardStack.getDeck()) {
            assertFalse(drawnCard.equalsContent(card));
        }
    }
}