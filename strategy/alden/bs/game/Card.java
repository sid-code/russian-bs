package bs.game;

/**
 * Representation of a single playing card
 * @author Alden
 */
public class Card {
    public enum Rank {
        ACE('A'), TWO('2'), THREE('3'), FOUR('4'), FIVE('5'),
        SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'), TEN('T'),
        JACK('J'), QUEEN('Q'), KING('K');

        public static Rank fromChar(char c) {
            for (Rank s : Rank.values()) {
                if (s.toChar() == c) {
                    return s;
                }
            }
            throw new IllegalArgumentException(c + " is not a rank.");
        }

        private char symbol;

        Rank(char symbol) {
            this.symbol = symbol;
        }

        public char toChar() {
            return symbol;
        }

        @Override
        public String toString() {
            return Character.toString(symbol);
        }
    }

    public enum Suit {
        CLUBS('C'), DIAMONDS('D'), HEARTS('H'), SPADES('S');

        public static Suit fromChar(char c) {
            for (Suit s : Suit.values()) {
                if (s.toChar() == c) {
                    return s;
                }
            }
            throw new IllegalArgumentException(c + " is not a suit.");
        }

        private char symbol;

        Suit(char symbol) {
            this.symbol = symbol;
        }

        public char toChar() {
            return symbol;
        }

        @Override
        public String toString() {
            return Character.toString(symbol);
        }
    }

    private static char rankFromString(String s) {
        if (s.length() != 2) {
            throw new IllegalArgumentException(s + " is not a card.");
        } else {
            return s.charAt(0);
        }
    }

    private static char suitFromString(String s) {
        if (s.length() != 2) {
            throw new IllegalArgumentException(s + " is not a card.");
        } else {
            return s.charAt(1);
        }
    }

    private Suit suit;

    private Rank rank;

    public Card(char rank, char suit) {
        this(Rank.fromChar(rank), Suit.fromChar(suit));
    }

    public Card(Rank rank, Suit suit) {
        this.suit = suit;
        this.rank = rank;
    }

    public Card(String s) {
        this(rankFromString(s), suitFromString(s));
    }

    @Override
    public String toString() {
        return rank.toString() + suit.toString();
    }
}
