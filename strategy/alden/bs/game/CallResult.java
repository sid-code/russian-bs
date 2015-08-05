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

    /**
     * Calculate the result of a given call made on a given play
     * @param play a play
     * @param call a call made in response to that play
     * @throws NoSuchElementException if the play's cards are unknown
     * @see Play#getCards()
     */
    public CallResult(Play play, Call call) {
        List<Card> revealed = play.getCards();
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
     * @return whether the call was correct
     */
    public boolean wasCorrect() {
        return correct;
    }
}
