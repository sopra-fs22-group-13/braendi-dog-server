package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardStackTest {
    CardStack cardStack;

    @BeforeEach
    public void setup() {
        cardStack = new CardStack();
    }

    @Test
    public void creationTest() {
        assertEquals(56, cardStack.getDeck().size());
    }

    /**
     * draws a card, checks if the deck size shrunk and if the card is no longer found in the deck
     */
    @Test
    public void drawCardTest() {
        Card drawnCard = cardStack.getNextCard();
        assertEquals(55, cardStack.getDeck().size());
        for (Card card: cardStack.getDeck()) {
            if (card.isJoker()) continue;
            assertFalse(drawnCard.equalsContent(card));
        }
    }

    @Test
    public void restockTest() {
        List<Card> drawnCards = new ArrayList<>();

        do {
            drawnCards.add(cardStack.getNextCard());
        } while (cardStack.getDeck().size() < 56);

        assertEquals(56, drawnCards.size());
        assertEquals(56, cardStack.getDeck().size());
    }
}