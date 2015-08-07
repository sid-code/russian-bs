package bs.game;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Representation of the result of a call
 * @author Alden
 * @see Call
 */
public class CallResult implements Reaction {
    private boolean correct;
    private List<Card> revealed;
    private int pileSize;
    private Player recipient;
    private Play nextRound;

    /**
     * Calculate the result of a given call made on a given play
     * @param play a play
     * @param call a call made in response to that play
     * @throws NoSuchElementException if the play's cards are unknown
     * @see Play#getCards()
     */
    public CallResult(Play play, Call call) {
        this(play, call, play.getCards());
    }

    /**
     * Calculate the result of a given call made on a given play, where the play
     * object and the cards contained in the play are supplied separately.
     * @param play a play
     * @param call a call made in response to that play
     * @param revealed the cards revealed by that call
     */
    public CallResult(Play play, Call call, List<Card> revealed) {
        this.revealed = revealed;
        pileSize = play.getPileSize();
        boolean truthful = true;
        for (Card c : revealed) {
            if (c.getRank() != play.getClaim()) {
                truthful = false;
            }
        }
        correct = call.getType() == Call.Type.BS ^ truthful;
        if (correct) {
            switch (call.getType()) {
                case BS:
                    recipient = play.getPlayer();
                    break;
                case BELIEVE:
                    recipient = null;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid call");
            }
        } else {
            recipient = call.getPlayer();
        }
    }

    /**
     * @return the first play of the next round, or null if it has not been set
     */
    public Play getNextRound() {
        return nextRound;
    }

    /**
     * @return the total number of cards on the table at the time of the call
     */
    public int getPileSize() {
        return pileSize;
    }

    /**
     * @return the player who received the pile, or {@code null} if it was
     *         removed from play
     */
    public Player getRecipient() {
        return recipient;
    }

    /**
     * @return the cards revealed by the call
     */
    public List<Card> getRevealed() {
        return new ArrayList<Card>(revealed);
    }

    /**
     * Links this result, which ends a particular round, to the beginning of the
     * next round.
     * @param play the first play of the next round
     */
    public void setNextRound(Play play) {
        if (nextRound == null) {
            nextRound = play;
        } else {
            throw new IllegalStateException("Next round was already set.");
        }
    }

    /**
     * @return whether the call was correct
     */
    public boolean wasCorrect() {
        return correct;
    }
}
