package bs.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Representation of a player for which the program knows private information
 * such as hand contents
 * @author Alden
 */
public class KnownPlayer {
    /**
     * Having {@link KnownPlayer} be a subclass of {@link Player} would create a
     * security risk, as private information could be accessed from
     * {@link Player} variables by casting to {@link KnownPlayer}. This inner
     * class exists to provide the interface of a {@link Player} that cannot be
     * casted to obtain additional information.
     * @author Alden
     */
    private class HiddenPlayer extends Player {
        /**
         * Used to more convincingly pretend to be a regular {@link Player}:
         * when the {@code KnownPlayer}'s state is consistent,
         * {@code expectedHandSize} should agree with {@code cards.size()}, but
         * it is not guaranteed that this will always be the case.
         */
        private int expectedHandSize;
        private List<Move> moves;

        public HiddenPlayer() {
            super(number, cards.size());
            expectedHandSize = cards.size();
            moves = new ArrayList<Move>();
        }

        @Override
        public int getHandSize() {
            if (expectedHandSize != cards.size()) {
                // Throwing an exception seems extreme
                System.err.println("Warning: expectedHandSize != cards.size()");
            }
            return expectedHandSize;
        }

        @Override
        public List<Move> getMoves() {
            return moves;
        }

        @Override
        public void move(Move move) {
            if (move instanceof Play) {
                Play play = (Play) move;
                expectedHandSize -= play.getSize();
                for (Card c : play.getCards()) {
                    if (!moves.remove(c)) {
                        throw new IllegalArgumentException(
                                "Play contains cards not in hand");
                    }
                }
            } else if (move instanceof Call) {
                CallResult result = ((Call) move).getReaction();
                if (result == null) {
                    throw new NullPointerException("result of call unknown.");
                } else {
                    result.getRecipient().changeHandSize(result.getPileSize());
                    // There's no sense in trying to add the whole pile here for
                    // a failed call; we only have access to the most recent
                    // play, so either the whole pile will be correctly added,
                    // or the game-running logic is flawed.
                }
            }
            moves.add(move);
        }

        @Override
        protected void changeHandSize(int difference) {
            expectedHandSize += difference;
        }
    }

    private HiddenPlayer hiddenPlayer;
    public final int number;
    private List<Card> cards;

    public KnownPlayer(int number) {
        this.number = number;
        cards = new ArrayList<Card>();
        hiddenPlayer = new HiddenPlayer();
    }

    public void addCards(Collection<Card> cards) {
        cards.addAll(cards);
        // No need to update HiddenPlayer's expectedHandSize; it should have
        // been or should soon be updated by changeHandSize logic.
    }

    public Player asPlayer() {
        return hiddenPlayer;
    }

    public List<Card> getCards() {
        return new ArrayList<Card>(cards);
    }
}
