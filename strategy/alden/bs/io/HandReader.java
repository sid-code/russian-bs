package bs.io;

import java.util.List;

import bs.game.Card;

/**
 * Reads player-specific information, such as cards in the hand
 * @author Alden
 */
public interface HandReader {
    public List<Card> currentHand();

    public int playerNumber();
}
