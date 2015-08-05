package bs.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a player
 * @author Alden
 * @see KnownPlayer
 */
public class Player {
    private int handSize;
    private int number;
    private List<Move> moves;

    /**
     * Construct a player with a given player number and a hand of a given size
     * @param number this player's position in the playing order
     * @param handSize the number of cards in the player's hand
     */
    public Player(int number, int handSize) {
        this.number = number;
        this.handSize = handSize;
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
            }
        }
        moves.add(move);
    }

    /**
     * @param difference the difference between the new and old hand sizes
     */
    protected void changeHandSize(int difference) {
        handSize += difference;
    }
}
