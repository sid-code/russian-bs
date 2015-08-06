package bs.io.sid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import bs.game.KnownPlayer;
import bs.strategy.Strategy;

/**
 * @author Alden
 */
public class SidStrategyApplication {
    public static void main(String[] args) {
        Strategy strategy = loadStrategy();
        SidBoardFile board = new SidBoardFile(args[0]);
        SidHandFile hand = new SidHandFile(args[1]);
        KnownPlayer player = board.incorporatePrivate(hand);
        hand.write(strategy.makeMove(player));
    }

    private static Strategy loadStrategy() {
        try {
            // The choice of strategy is determined from a properties file
            // naming the class to use. The properties file could just be in the
            // working directory, but the intention is for the application and
            // the properties file to be packaged into a single .jar file.
            InputStream in = ClassLoader.getSystemResourceAsStream(PROPS);
            if (in == null) {
                throw new RuntimeException("Unable to find " + PROPS);
            }
            Properties props = new Properties();
            props.load(in);
            String strategyName = props.getProperty(STRATEGY_KEY);
            return (Strategy) Class.forName(strategyName).newInstance();
        } catch (IOException e) {
            throw new RuntimeException("Error while loading properties.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find indicated strategy.", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Strategy instantiation failed.", e);
        }
    }

    private static final String PROPS = "bs.properties";
    private static final String STRATEGY_KEY = "strategy";
}
