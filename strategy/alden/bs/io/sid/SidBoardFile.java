package bs.io.sid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import bs.game.Call;
import bs.game.CallResult;
import bs.game.Card;
import bs.game.Card.Rank;
import bs.game.KnownPlayer;
import bs.game.Play;
import bs.game.Player;
import bs.io.GameReader;
import bs.io.HandReader;

/**
 * @author Alden
 */
public class SidBoardFile implements GameReader {
    /**
     * @param anyPlayer any player in the same game as the desired player
     * @param playerNum the desired player's number
     * @return The player with number {@code playerNum}
     */
    private static Player findPlayer(Player anyPlayer, int playerNum) {
        Player player = null;
        for (Player p : anyPlayer) {
            if (p.getNumber() == playerNum) {
                player = p;
                break;
            }
        }
        if (player == null) {
            throw new RuntimeException("Play made by nonexistent player.");
        }
        return player;
    }

    private static Call parseCall(String line, Player anyPlayer, Play previous) {
        String[] tokens = line.split("\\|");
        int playerNum = Integer.valueOf(tokens[1].substring(1));
        Player player = findPlayer(anyPlayer, playerNum);
        Call.Type type = Call.Type.valueOf(tokens[2]);
        Call call = new Call(player, type);
        previous.setReaction(call);
        List<Card> revealed = new ArrayList<Card>();
        for (String card : tokens[3].split(",")) {
            revealed.add(new Card(card));
        }
        CallResult result = new CallResult(previous, call, revealed);
        if (result.wasCorrect() ^ "GOOD".equals(tokens[4])) {
            throw new RuntimeException("Call result does not agree");
        }
        int recipientNum = Integer.valueOf(tokens[5].substring(1));
        if (result.getRecipient() != null) {
            if (result.getRecipient().getNumber() != recipientNum) {
                throw new RuntimeException("Recipient does not agree");
            }
        }
        call.setReaction(result);
        player.move(call);
        return call;
    }

    private static Play parsePlay(String line, Player anyPlayer, Play previous) {
        String[] tokens = line.split("\\|");
        int playerNum = Integer.valueOf(tokens[1].substring(1));
        Player player = findPlayer(anyPlayer, playerNum);
        int e2len = tokens[2].length();
        Rank claim = Rank.fromChar(tokens[2].charAt(e2len - 1));
        int size = Integer.parseInt(tokens[2].substring(0, e2len - 1));
        int cardsLeft = Integer.parseInt(tokens[3]);
        Play play = new Play(player, previous, claim, size);
        player.move(play);
        if (player.getHandSize() != cardsLeft) {
            throw new RuntimeException("Hand size does not agree.");
        }
        return play;
    }

    private File boardFile;
    private static final Pattern PLAYER_PATTERN = Pattern.compile(
            "PLAYER\\|P\\d+\\|.*");
    private static final Pattern START_PATTERN = Pattern.compile(
            "START\\|P\\d+");

    private static final Pattern PLAY_PATTERN = Pattern.compile(
            "PLAY\\|P\\d+\\|\\d+[A[2-9]TJQK]\\|\\d+");

    private static final Pattern CALL_PATTERN = Pattern.compile(
            "CALL\\|P\\d+\\|(BS|BELIEVE)"
                    + "\\|[A[2-9]TJQK][DHCS](,[A[2-9]TJQK][DHCS])*"
                    + "\\|(GOOD|BAD)\\|P\\d+");

    public static final int DECK_SIZE = 52;

    public SidBoardFile(File file) {
        boardFile = file;
    }

    public SidBoardFile(String file) {
        this(new File(file));
    }

    @Override
    public KnownPlayer incorporatePrivate(HandReader reader) {
        KnownPlayer kp = new KnownPlayer(reader.playerNumber());
        kp.addCards(reader.initialHand());
        readFile(kp.asPlayer());
        kp.resetCards(reader.currentHand());
        if (!kp.isConsistent()) {
            throw new RuntimeException("Inconsistent hand and board files");
        }
        return kp;
    }

    @Override
    public Player initialPlayer() {
        return readFile(null);
    }

    /**
     * Read through a file, and return a reference to the entire game in the
     * form of one player. This player may be {@code null}, in which case a
     * player will be picked from the players in the game to be used as the
     * result.
     * @param resultPlayer the player to use as a reference to the result
     * @return {@code resultPlayer}, or a new player if the parameter was
     *         {@code null}, connected to the game information of the file
     */
    private Player readFile(Player resultPlayer) {
        try (Scanner in = new Scanner(boardFile)) {
            List<Integer> playerNumbers = new ArrayList<Integer>();
            String line = in.nextLine();
            while (PLAYER_PATTERN.matcher(line).matches()) {
                String trimLeft = line.substring(8);
                String trim = trimLeft.substring(0, trimLeft.indexOf('|'));
                playerNumbers.add(Integer.valueOf(trim));
                line = in.nextLine();
            }
            if (!START_PATTERN.matcher(line).matches()) {
                throw new RuntimeException("Missing START");
            }
            int kpNumber;
            if (resultPlayer == null) {
                kpNumber = playerNumbers.get(0);
                int hand = DECK_SIZE / playerNumbers.size();
                if (0 < DECK_SIZE % playerNumbers.size()) {
                    hand++;
                }
                resultPlayer = new Player(kpNumber, hand);
            } else {
                kpNumber = resultPlayer.getNumber();
            }
            int i = playerNumbers.indexOf(kpNumber) + 1;
            int ii = i;
            if (i == 0) {
                throw new RuntimeException("Indicated player not present.");
            }
            Player lastSuccessor = resultPlayer;
            for (; i < ii + playerNumbers.size() - 1; i++) {
                int hand = DECK_SIZE / playerNumbers.size();
                if (i < (DECK_SIZE % playerNumbers.size())) {
                    hand++;
                }
                Player p = new Player(
                        playerNumbers.get(i % playerNumbers.size()), hand);
                lastSuccessor.setSuccessor(p);
                lastSuccessor = p;
            }
            lastSuccessor.setSuccessor(resultPlayer);

            while (in.hasNextLine()) {
                line = in.nextLine();
                if (!PLAY_PATTERN.matcher(line).matches()) {
                    throw new RuntimeException("PLAY expected to start round.");
                }
                Play currentPlay = parsePlay(line, resultPlayer, null);
                while (in.hasNextLine()) {
                    line = in.nextLine();
                    if (PLAY_PATTERN.matcher(line).matches()) {
                        currentPlay =
                                parsePlay(line, resultPlayer, currentPlay);
                    } else if (CALL_PATTERN.matcher(line).matches()) {
                        parseCall(line, resultPlayer, currentPlay);
                        break;
                    } else {
                        throw new RuntimeException(
                                "PLAY or CALL expected to continue round.");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error reading " + boardFile.getAbsolutePath(),
                    e);
        }
        return resultPlayer;
    }
}
