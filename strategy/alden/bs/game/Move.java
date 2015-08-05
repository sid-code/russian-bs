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
     * Sets the {@code Reaction} to this move, if it has not already been set
     * @param reaction the reaction to this move
     * @throws IllegalStateException if the reaction has already been set
     */
    public void setReaction(Reaction reaction) {
        if (this.reaction == null) {
            this.reaction = reaction;
        } else {
            throw new IllegalStateException("Reaction was already set.");
        }
    }
}
