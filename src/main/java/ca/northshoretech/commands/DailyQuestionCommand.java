package ca.northshoretech.commands;

import ca.northshoretech.BetaBot;
import ca.northshoretech.helpers.EmbedHelper;
import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DailyQuestionCommand extends ListenerAdapter {

    private final String prefix = BetaBot.getConfig().get("PREFIX");
    private final String botCommandsChannelId = BetaBot.getConfig().get(
        "COMMANDS_CHANNEL"
    );
    private final String dailyQuestionChannelId = BetaBot.getConfig().get(
        "DAILY_QUESTION_CHANNEL"
    );
    private final String adminRoleId = BetaBot.getConfig().get("ADMIN_ROLE_ID");
    private final String commandPrefix = prefix + "qotd";

    /**
     * Handles incoming messages for the daily question command
     *
     * @param event the message received event
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // check if the bot is the sender of the message
        if (event.getAuthor().isBot()) return;

        // check if the message is being sent in the correct admin channel
        if (!event.getChannel().getId().equals(botCommandsChannelId)) return;

        // check if the message starts with the command prefix
        if (
            !event.getMessage().getContentRaw().startsWith(commandPrefix)
        ) return;

        // check if author is admin
        Role adminRole = event.getJDA().getRoleById(adminRoleId);
        if (adminRole == null) {
            BetaBot.getLogger().error(
                "No admin role found. Please make sure theres an admin role in the server"
            );
            return;
        }
        List<Role> authorRoles = Objects.requireNonNull(
            event.getMember()
        ).getRoles();
        if (!authorRoles.contains(adminRole)) {
            EmbedHelper.sendWarningEmbed(
                event.getChannel(),
                "You do not have the required admin permissions to use this command."
            );
            return;
        }

        // get the question from the message being sent
        String question = event
            .getMessage()
            .getContentRaw()
            .substring(commandPrefix.length());

        // create embed for the response
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Question of The Day!");
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.setDescription(question);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(
            "Powered By BetaBoys",
            "https://media.discordapp.net/attachments/1352001410069172387/1352464998886277161/image.jpg?ex=67dec56f&is=67dd73ef&hm=ef420dc5c3306558c8e6b4c732dccdda0356f1e8ccedaf3c6e5fdbaaa6314fb7&=&format=webp&width=960&height=960"
        );

        // get the daily question channel, if it's not found send an error embed
        TextChannel dailyQuestionChannel = event
            .getGuild()
            .getTextChannelById(dailyQuestionChannelId);
        if (dailyQuestionChannel == null) {
            BetaBot.getLogger().error(
                "There was an error finding the daily question channel by its ID"
            );
            EmbedHelper.sendErrorEmbed(
                event.getChannel(),
                "There was an error finding the daily question channel by its ID."
            );
            return;
        }
        // send message to the daily-question channel
        dailyQuestionChannel
            .sendMessage("@everyone")
            .setEmbeds(embedBuilder.build())
            .queue();
    }
}
