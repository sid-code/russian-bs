package bs.strategy;

import java.util.ArrayList;
import java.util.List;

import bs.game.Call;
import bs.game.Card;
import bs.game.Card.Rank;
import bs.game.KnownPlayer;
import bs.game.Move;
import bs.game.Play;
import bs.game.Player;

/**
 * A very basic strategy that plays truthfully whenever possible and calls
 * belief otherwise.
 * @author Alden
 */
public class HonestChump implements Strategy {
    /**
     * Generate a play of as many cards as possible of a given rank.
     * @param self the player playing those cards
     * @param previous the previous play, or {@code null} if starting a round
     * @param rank the rank to play
     * @return a play of all cards in hand of that rank
     */
    private static Play play(KnownPlayer self, Play previous, Rank rank) {
        List<Card> toPlay = new ArrayList<Card>();
        for (Card c : self.getCards()) {
            if (c.getRank() == rank) {
                toPlay.add(c);
            }
        }
        return new Play(self.asPlayer(), previous, rank, toPlay);
    }

    @Override
    public Move makeMove(KnownPlayer self) {
        // The nearest player before self in the order who is still playing.
        Player previous = self.asPlayer();
        while (previous.nextToPlay() != self.asPlayer()) {
            previous = previous.nextToPlay();
        }
        List<Move> prevMoves = previous.getMoves();
        // The most recent move to be made
        Move lastMove = null;
        if (prevMoves.size() > 0) {
            // This will only not be the case when the game has just begun.
            lastMove = prevMoves.get(prevMoves.size() - 1);
        }
        if (lastMove != null && lastMove.getReaction() instanceof Move) {
            // lastMove was not actually last, because it had a reaction that
            // was another Move, made by self.
            lastMove = (Move) lastMove.getReaction();
        }
        if (lastMove instanceof Play) {
            Rank toMatch = ((Play) lastMove).getClaim();
            for (Card c : self.getCards()) {
                if (c.getRank() == toMatch) {
                    return play(self, (Play) lastMove, toMatch);
                }
            }
            return new Call(self.asPlayer(), Call.Type.BELIEVE);
        }
        else {
            // It's a new round; play the most frequent card in hand.
            int maxFreq = 0;
            Rank mode = null;
            // Quadratic time, but the list is tiny.
            for (Rank r : Rank.values()) {
                int freq = 0;
                for (Card c : self.getCards()) {
                    if (r.equals(c.getRank())) {
                        freq++;
                    }
                }
                if (freq > maxFreq) {
                    maxFreq = freq;
                    mode = r;
                }
            }
            return play(self, null, mode);
        }
    }
}
