package bs.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import bs.game.Call;
import bs.game.Card;
import bs.game.Card.Rank;
import bs.game.KnownPlayer;
import bs.game.Move;
import bs.game.Play;

/**
 * A simple strategy that picks randomly from a variety of possible moves using
 * a distribution with random weights that is fixed at initialization. This
 * could be rather similar to Victor's idea with training of the probabilities,
 * but in its current state is not intelligent.
 * @author Alden
 */
public class Randomized implements Strategy {
    private Random rand;
    /**
     * Probability of calling instead of playing (if possible)
     */
    private double callP;
    /**
     * Probability of calling believe instead of BS
     */
    private double believeP;
    /**
     * Probability of playing truthfully if possible
     */
    private double truthP;
    /**
     * Probability of playing one card more than already decided to play
     */
    private double extraCardP;

    public Randomized() {
        // Of course, these probabilities will be reset every turn anyway,
        // because the program will terminate and be relaunched.
        rand = new Random();
        callP = rand.nextDouble();
        believeP = rand.nextDouble();
        truthP = rand.nextDouble();
        extraCardP = rand.nextDouble();
    }

    @Override
    public Move makeMove(KnownPlayer self) {
        Move lastMove = self.asPlayer().lastMove();
        if (lastMove instanceof Play && rand.nextDouble() < callP) {
            if (rand.nextDouble() < believeP) {
                return new Call(self.asPlayer(), Call.Type.BELIEVE);
            } else {
                return new Call(self.asPlayer(), Call.Type.BS);
            }
        } else {
            int maxCardsToPlay = 1;
            while (rand.nextDouble() < extraCardP && maxCardsToPlay < 4) {
                maxCardsToPlay++;
            }
            Rank claim = null;
            if (lastMove instanceof Play) {
                claim = ((Play) lastMove).getClaim();
            } else {
                // To avoid a bias towards earlier elements in values in
                // case of ties, iterate over a randomly-ordered copy.
                List<Rank> ranks = Arrays.asList(Rank.values());
                Collections.shuffle(ranks, rand);
                int maxSoFar = 0;
                for (Rank r : ranks) {
                    int count = 0;
                    for (Card c : self.getCards()) {
                        if (c.getRank().equals(r)) {
                            count++;
                        }
                    }
                    if (count >= maxCardsToPlay) {
                        claim = r;
                        break;
                    } else if (count > maxSoFar) {
                        // Keeping track of this ensures that claim will be
                        // set to the most populous rank if no rank has
                        // enough to meet maxCardsToPlay.
                        maxSoFar = count;
                        claim = r;
                    }
                }
            }
            List<Card> cards = self.getCards();
            if (rand.nextDouble() < truthP) {
                List<Card> toPlay = new ArrayList<Card>();
                for (Card c : cards) {
                    if (c.getRank().equals(claim)) {
                        toPlay.add(c);
                    }
                    if (toPlay.size() >= maxCardsToPlay) {
                        break;
                    }
                }
                if (toPlay.size() == 0) {
                    // Darn, unable to make truthful play
                    toPlay.add(cards.get(rand.nextInt(cards.size())));
                }
                return new Play(self.asPlayer(), claim, toPlay);
            } else {
                Collections.shuffle(cards, rand);
                List<Card> toPlay = new ArrayList<Card>();
                for (Card c : cards) {
                    if (!c.getRank().equals(claim)) {
                        toPlay.add(c);
                    }
                    if (toPlay.size() >= maxCardsToPlay) {
                        break;
                    }
                }
                // In case there weren't enough non-claim cards, add some cards
                // of the claimed rank
                for (Card c : cards) {
                    if (toPlay.size() >= maxCardsToPlay) {
                        break;
                    }
                    if (c.getRank().equals(claim)) {
                        toPlay.add(c);
                    }
                }
                return new Play(self.asPlayer(), claim, toPlay);
            }
        }
    }
}
