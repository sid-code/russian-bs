package bs.game;

/**
 * Representation of a call of either BS or belief
 * @author Alden
 */
public class Call extends Move {
    public enum Type {
        BS, BELIEVE
    };

    private Type type;

    public Call(Player player, Type type) {
        super(player);
        this.type = type;
    }

    @Override
    public CallResult getReaction() {
        return (CallResult) super.getReaction();
    }

    public Type getType() {
        return type;
    }
}
