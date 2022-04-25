package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

public interface IBoard {
    BoardData getFormattedBoardState();
    void makeMove(Move move) throws InvalidMoveException;
    void makeStartingMove(COLOR color) throws InvalidMoveException;
    boolean checkWinningCondition(COLOR color);
    void makeSwitch(int start, int end) throws InvalidMoveException;
    boolean isAnyMovePossible(Card card, COLOR col);
    boolean isValidMove(Move move) throws InvalidMoveException;
}
