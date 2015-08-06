package bs.game;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import bs.game.Card.Rank;

/**
 * Representation of a play of some cards
 * @author Alden
 */
public class Play extends Move {
    private Rank claim;
    private int size;
    private int pileSize;
    private List<Card> cards;

    /**
     * Construct a {@code Play} of a given number of cards claiming a given rank
     * that continues an existing round. This should be used for situations
     * where the actual cards played are unknown (i.e. opponent plays).
     * @param player who played the cards
     * @param previous the previous play in the round
     * @param claim the claimed rank of the cards being played
     * @param size the number of cards being played
     */
    public Play(Player player, Play previous, Rank claim, int size) {
        super(player);
        cards = null;
        this.claim = claim;
        this.size = size;
        initPrevious(previous);
    }

    /**
     * Construct a {@code Play} of a given list of cards by a given player
     * claiming a given rank that continues an existing round.
     * @param player who played the cards
     * @param previous the previous play in the round
     * @param claim the claimed rank of the cards being played
     * @param cards a nonempty list of cards to play
     */
    public Play(Player player, Play previous, Rank claim, List<Card> cards) {
        super(player);
        this.claim = claim;
        this.cards = new ArrayList<Card>(cards);
        size = cards.size();
        initPrevious(previous);
    }

    /**
     * Construct a {@code Play} of a given number of cards claiming a given rank
     * that begins a new round. This should be used for situations where the
     * actual cards played are unknown (i.e. opponent plays).
     * @param player who played the cards
     * @param claim the claimed rank of the cards being played
     * @param size the number of cards being played
     */
    public Play(Player player, Rank claim, int size) {
        this(player, null, claim, size);
    }

    /**
     * Construct a {@code Play} of a given list of cards by a given player
     * claiming a given rank that begins a new round.
     * @param player who played the cards
     * @param claim the claimed rank of the cards being played
     * @param cards a nonempty list of cards to play
     */
    public Play(Player player, Rank claim, List<Card> cards) {
        this(player, null, claim, cards);
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
     * @return the total number of cards on the table after this play
     */
    public int getPileSize() {
        return pileSize;
    }

    @Override
    public Move getReaction() {
        return (Move) super.getReaction();
    }

    /**
     * @return the number of cards in this play
     */
    public int getSize() {
        return size;
    }

    /**
     * Called as part of constructors; sets up things involving the previous
     * play.
     * @param previous the previous play, or {@code null} if this is a new
     *            round.
     */
    private void initPrevious(Play previous) {
        if (previous == null) {
            pileSize = size;
        } else {
            pileSize = previous.getPileSize() + size;
            previous.setReaction(this);
        }
    }
}
