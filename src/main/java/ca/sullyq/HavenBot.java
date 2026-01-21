package ca.sullyq;

import ca.sullyq.commands.DailyQuestionCommand;
import ca.sullyq.commands.DailyRiddleCommand;
import ca.sullyq.listeners.ReadyListener;
import ca.sullyq.listeners.RiddleDirectMessageListener;
import ca.sullyq.managers.RiddleManager;
import io.github.cdimascio.dotenv.Dotenv;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HavenBot {

    public static final String ICON_URL = "https://cdn.discordapp.com/avatars/1349532999309328455/54efad6155cc0872cc790801835308d2?size=256";
    public static final String POWERED_BY = "Powered By Starhaven";
    public static final String COLLABORATION = "In collaboration with BetaBoys";

    private static final Logger logger = LoggerFactory.getLogger(HavenBot.class);
    private static final Dotenv config = Dotenv.configure().load();
    private static final RiddleManager riddleManager = new RiddleManager();

    /**
     * Loads environment variables and builds the bot shard manager
     *
     * @throws LoginException
     */
    private HavenBot() throws LoginException, InterruptedException {
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
                .setActivity(Activity.watching("The Stars"))
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
            HavenBot bot = new HavenBot();
        } catch (LoginException | InterruptedException e) {
            getLogger().error(
                    "There was an error with the discord login token"
            );
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
