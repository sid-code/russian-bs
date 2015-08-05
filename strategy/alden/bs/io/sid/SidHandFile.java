package bs.io.sid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import bs.game.Call;
import bs.game.Card;
import bs.game.Move;
import bs.game.Play;
import bs.io.HandReader;
import bs.io.MoveWriter;

/**
 * Reads from and writes to Sid's .hand file format
 * @author Alden
 */
public class SidHandFile implements HandReader, MoveWriter {
    private static final Pattern PLAYER_PATTERN = Pattern.compile(
            "PLAYER\\|P\\d+");
    private static final Pattern HAND_PATTERN = Pattern.compile(
            "HAND\\|[A[2-9]TJQK][DHCS](,[A[2-9]TJQK][DHCS])*");

    private File handFile;

    public SidHandFile(File file) {
        handFile = file;
    }

    public SidHandFile(String file) {
        this(new File(file));
    }

    @Override
    public List<Card> currentHand() {
        try (Scanner in = new Scanner(handFile)) {
            String hand = null;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (HAND_PATTERN.matcher(line).matches()) {
                    hand = line.substring(5);
                }
            }
            if (hand == null) {
                throw new RuntimeException(handFile.getAbsolutePath() +
                        " contains no valid hand information");
            } else {
                String[] cards = hand.split(",");
                ArrayList<Card> result = new ArrayList<Card>(cards.length);
                for (String card : cards) {
                    result.add(new Card(card));
                }
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error reading " + handFile.getAbsolutePath(),
                    e);
        }
    }

    @Override
    public int playerNumber() {
        try (Scanner in = new Scanner(handFile)) {
            String line = in.nextLine();
            if (PLAYER_PATTERN.matcher(line).matches()) {
                return Integer.parseInt(line.substring(8));
            } else {
                throw new RuntimeException(handFile.getAbsolutePath() +
                        " start with " + line + " instead of player number");
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error reading " + handFile.getAbsolutePath(),
                    e);
        }
    }

    @Override
    public void write(Move move) {
        String output;
        if (move instanceof Call) {
            switch (((Call) move).getType()) {
                case BELIEVE:
                    output = "BELIEVE";
                    break;
                case BS:
                    output = "BS";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid call");
            }
        } else if (move instanceof Play) {
            Play play = (Play) move;
            if (play.getSize() > 9) {
                throw new UnsupportedOperationException(
                        "Plays are limited to single-digit numbers of cards.");
            }
            List<Card> cards = play.getCards();
            List<String> cardStrings = new ArrayList<String>(cards.size());
            for (Card c : cards) {
                cardStrings.add(c.toString());
            }
            output = String.format("%d%c;%s%n",
                    play.getSize(),
                    play.getClaim().toChar(),
                    String.join(",", cardStrings));
        } else {
            throw new IllegalArgumentException("Move is neither Play nor Call");
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(handFile, true))) {
            // The two %n ensure that output is and will be on its own line
            out.printf("%n%s%n", output);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error writing " + handFile.getAbsolutePath(),
                    e);
        }
    }
}
