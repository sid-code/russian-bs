package bs.io;

import bs.game.KnownPlayer;
import bs.game.Player;

/**
 * Reads public information describing the history of the game.
 * @author Alden
 */
public interface GameReader {
    /**
     * Return a {@code KnownPlayer} corresponding to a given {@code HandReader}.
     * @param reader access to the private information of some player
     * @return a representation of that player with references to the entire
     *         game.
     */
    public KnownPlayer incorporatePrivate(HandReader reader);

    /**
     * Return the first player in the playing order. The structures of
     * {@link bs.game} are such that one player contains sufficient references
     * to access all of a game.
     * @return the first player to move
     */
    public Player initialPlayer();
}
