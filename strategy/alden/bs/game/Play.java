package bs.game;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import bs.game.Card.Rank;

/**
 * Representation of a play of some cards
 * @author Alden
 */
public class Play implements Move {
    private Rank claim;
    private int size;
    private List<Card> cards;

    /**
     * Given a list of some cards, construct a {@code Play} of them where the
     * claimed rank is the rank of the first card in the list.
     * @param cards a nonempty list of cards to play
     */
    public Play(List<Card> cards) {
        this(cards.get(0).getRank(), cards);
    }

    /**
     * Given a rank and a number, construct a {@code Play} of that many cards
     * claiming that rank. This should be used for situations where the actual
     * cards played are unknown (i.e. opponent plays).
     * @param claim the claimed rank of the cards being played
     * @param size the number of cards being played
     */
    public Play(Rank claim, int size) {
        cards = null;
        this.claim = claim;
        this.size = size;
    }

    /**
     * Given a rank and a list of some cards, construct a {@code Play} of those
     * cards claiming that rank.
     * @param claim the claimed rank of the cards being played
     * @param cards a nonempty list of cards to play
     */
    public Play(Rank claim, List<Card> cards) {
        this.claim = claim;
        this.cards = new ArrayList<Card>(cards);
        size = cards.size();
    }

    /**
     * @return the cards in this play
     * @throws NoSuchElementException if this is an opponent's unrevealed play
     */
    public List<Card> getCards() {
        if (cards != null) {
            return new ArrayList<Card>(cards);
        } else {
            throw new NoSuchElementException("Play contents unknown.");
        }
    }

    /**
     * @return the claimed rank of this play
     */
    public Rank getClaim() {
        return claim;
    }

    /**
     * @return the number of cards in this play
     */
    public int getSize() {
        return size;
    }
}
