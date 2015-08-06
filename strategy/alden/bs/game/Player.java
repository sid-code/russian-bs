package bs.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Representation of a player
 * @author Alden
 * @see KnownPlayer
 */
public class Player implements Iterable<Player> {
    private class Iter implements Iterator<Player> {
        private Player next;
        private boolean haventReturnedThis;

        public Iter() {
            next = Player.this;
            haventReturnedThis = true;
        }

        @Override
        public boolean hasNext() {
            return next != Player.this || haventReturnedThis;
        }

        @Override
        public Player next() {
            Player result = next;
            next = next.getSuccessor();
            haventReturnedThis = false;
            return result;
        }
    }

    private int handSize;
    private int number;
    private List<Move> moves;
    private Player successor;
    private boolean playing;

    /**
     * Construct a player with a given player number and a hand of a given size
     * @param number this player's position in the playing order
     * @param handSize the number of cards in the player's hand
     */
    public Player(int number, int handSize) {
        this.number = number;
        this.handSize = handSize;
        moves = new ArrayList<Move>();
        playing = true;
    }

    /**
     * @return the number of cards currently in the player's hand
     */
    public int getHandSize() {
        return handSize;
    }

    /**
     * @return an ordered list of moves this player has made
     */
    public List<Move> getMoves() {
        return new ArrayList<Move>(moves);
    }

    /**
     * @return this player's position in the playing order (player number)
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the next player in the (cyclic) sequence of players
     * @throws IllegalStateException if the successor has not been set
     * @see #nextToPlay()
     */
    public Player getSuccessor() {
        if (successor == null) {
            throw new IllegalStateException("Successor has not been set.");
        } else {
            return successor;
        }
    }

    /**
     * Returns an iterator over the players in the same game, starting with
     * {@code this} and ending with the player whose successor is {@code this}.
     * @return an iterator over one cycle of play
     */
    @Override
    public Iterator<Player> iterator() {
        return new Iter();
    }

    /**
     * This is called when this player makes a move. It adds {@code move} to
     * this player's list of moves, and decreases this player's hand size if
     * {@code move} is a {@link Play}. If {@code move} is a {@link Call}, it
     * updates the appropriate player's hand size depending on the outcome of
     * the call. Note that this means that a {@link Call} must have its
     * {@link Reaction} set before being passed to this method.
     * @param move a move made by this player
     * @see CallResult#CallResult(Play, Call)
     * @throws IllegalArgumentException if {@code move} is a play of more cards
     *             than are in this player's hand
     * @throws NullPointerException if {@code move} is a call with unknown
     *             result
     */
    public void move(Move move) {
        if (move instanceof Play) {
            handSize -= ((Play) move).getSize();
            if (handSize < 0) {
                throw new IllegalArgumentException("Play larger than hand.");
            }
        } else if (move instanceof Call) {
            CallResult result = ((Call) move).getReaction();
            if (result == null) {
                throw new NullPointerException("Result of call unknown.");
            } else {
                result.getRecipient().changeHandSize(result.getPileSize());
                result.getRecipient().processWinners();
            }
        }
        moves.add(move);
    }

    /**
     * @return the next player in order who has not yet won (is still playing),
     *         or {@code} null if no other players are still playing
     * @throws IllegalStateException if while traversing the player cycle, a
     *             player is encountered with no successor
     * @see #getSuccessor()
     */
    public Player nextToPlay() {
        Player candidate = getSuccessor();
        while (candidate != this) {
            if (candidate.playing) {
                return candidate;
            }
            candidate = candidate.getSuccessor();
        }
        return candidate;
    }

    /**
     * Sets which player follows this player in sequence, if it has not already
     * been set.
     * @param successor the next player in the playing order
     * @throws IllegalStateException if the successor has already been set
     */
    public void setSuccessor(Player successor) {
        if (this.successor == null) {
            this.successor = successor;
        } else {
            throw new IllegalStateException("Successor was already set");
        }
    }

    /**
     * @param difference the difference between the new and old hand sizes
     */
    protected void changeHandSize(int difference) {
        handSize += difference;
    }

    /**
     * Determine if any players have won (have empty hands), and update their
     * playing status accordingly. This should only be called at the end of a
     * given round.
     */
    protected void processWinners() {
        for (Player p : this) {
            if (p.getHandSize() == 0) {
                p.playing = false; // winner!
            }
        }
    }
}
