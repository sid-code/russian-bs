package bs.game;

/**
 * Representation of a player's move, either a call or a play
 * @author Alden
 * @see Call
 * @see Play
 */
public abstract class Move implements Reaction {
    private Player player;
    private Reaction reaction;

    protected Move(Player player) {
        this.player = player;
        reaction = null;
    }

    /**
     * @return who made this move
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the reaction to this move, or {@code null} if the reaction is
     *         unknown
     */
    public Reaction getReaction() {
        return reaction;
    }

    /**
     * Sets the {@link Reaction} to this move, if it has not already been set
     * @param move the move that was made after this one
     * @throws IllegalStateException if the reaction has already been set
     */
    public void setReaction(Move move) {
        if (reaction == null) {
            reaction = move;
        } else {
            throw new IllegalStateException("Reaction was already set.");
        }
    }
}
