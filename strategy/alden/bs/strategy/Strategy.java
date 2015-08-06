package bs.strategy;

import bs.game.KnownPlayer;
import bs.game.Move;

/**
 * Represents a strategy for play
 * @author Alden
 */
public interface Strategy {
    /**
     * Decide what a given player should do in its present situation
     * @param self the player acting on this strategy
     * @return the move the player should make
     */
    public Move makeMove(KnownPlayer self);
}
