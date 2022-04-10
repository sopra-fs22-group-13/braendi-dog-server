package ch.uzh.ifi.hase.soprafs22.game.gameInstance.player;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;

import java.util.ArrayList;

public class Player {

    private ArrayList<Card> _hand = new ArrayList<Card>();
    private COLOR _turn;
    private String _token;

    public Player(String token){
        this._token= null;
    }

    public String get_token() {
        return _token;
    }

    public ArrayList<String> getFormattedCards()
    {
        return null;
    }

    public int getCardCount()
    {
        return 0;
    }
}
