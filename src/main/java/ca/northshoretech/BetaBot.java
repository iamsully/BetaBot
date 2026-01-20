package ca.northshoretech;

import ca.northshoretech.commands.DailyQuestionCommand;
import ca.northshoretech.commands.DailyRiddleCommand;
import ca.northshoretech.listeners.ReadyListener;
import ca.northshoretech.listeners.RiddleDirectMessageListener;
import ca.northshoretech.managers.RiddleManager;
import io.github.cdimascio.dotenv.Dotenv;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetaBot {

    private static final Logger logger = LoggerFactory.getLogger(BetaBot.class);
    private static final Dotenv config = Dotenv.configure().load();
    private static final RiddleManager riddleManager = new RiddleManager();

    /**
     * Loads environment variables and builds the bot shard manager
     *
     * @throws LoginException
     */
    private BetaBot() throws LoginException, InterruptedException {
        String token = config.get("TOKEN");

        // Check to make sure the token is valid from the dotenv file
        if (token == null) throw new LoginException(
            "There was no token in the dot env file"
        );

        JDA jda = JDABuilder.createDefault(token)
            .enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES
            )
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setActivity(Activity.watching("Beta Boys Streamers"))
            .addEventListeners(
                new ReadyListener(),
                new RiddleDirectMessageListener(),
                new DailyQuestionCommand(),
                new DailyRiddleCommand()
            )
            .build();
        jda.awaitReady();
    }

    /**
     * gets the static riddle manager for the bot
     *
     * @return returns the static riddle manager
     */
    public static RiddleManager getRiddleManager() {
        return riddleManager;
    }

    /**
     * Main entry point to the bot
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            BetaBot bot = new BetaBot();
        } catch (LoginException | InterruptedException e) {
            getLogger().error(
                "There was an error with the discord login token"
            );
            //            System.err.println("There was an error with the discord login token");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the dotenv config
     *
     * @return Dotenv config
     */
    public static Dotenv getConfig() {
        return config;
    }

    /**
     * Gets the static logger instance
     *
     * @return Logger for SLF4J
     */
    public static Logger getLogger() {
        return logger;
    }
}
