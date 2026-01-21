package ca.sullyq.commands;

import ca.sullyq.HavenBot;
import ca.sullyq.Riddle;
import ca.sullyq.helpers.EmbedHelper;
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

public class DailyRiddleCommand extends ListenerAdapter {

    private final String prefix = HavenBot.getConfig().get("PREFIX");
    private final String botCommandsChannelId = HavenBot.getConfig().get(
        "COMMANDS_CHANNEL"
    );
    private final String dailyRiddleChannelId = HavenBot.getConfig().get(
        "DAILY_RIDDLE_CHANNEL"
    );
    private final String adminRoleId = HavenBot.getConfig().get("ADMIN_ROLE_ID");
    private final String commandPrefix = prefix + "rotd";

    /**
     * Handles incoming messages for the daily riddle command
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

        // make sure there is only one active riddle
        if (HavenBot.getRiddleManager().getActiveRiddle() != null) {
            EmbedHelper.sendWarningEmbed(
                event.getChannel(),
                "There is already a riddle created. There can only be one riddle created at a time"
            );
            return;
        }

        // check if author is admin
        Role adminRole = event.getJDA().getRoleById(adminRoleId);
        if (adminRole == null) {
            HavenBot.getLogger().error(
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

        // get the riddle from the message being sent
        String[] riddleMessage = event
            .getMessage()
            .getContentRaw()
            .substring(commandPrefix.length())
            .split("-");
        if (riddleMessage.length < 2) {
            HavenBot.getLogger().error(
                "The riddle message must have a riddle and an answer"
            );
            EmbedHelper.sendErrorEmbed(
                event.getChannel(),
                "The riddle message must have a riddle and an answer"
            );
            return;
        }
        // get the daily riddle channel, if it's not found send an error embed
        TextChannel dailyRiddleChannel = event
            .getGuild()
            .getTextChannelById(dailyRiddleChannelId);
        if (dailyRiddleChannel == null) {
            HavenBot.getLogger().error(
                "There was an error finding the daily riddle channel by its ID"
            );
            EmbedHelper.sendErrorEmbed(
                event.getChannel(),
                "There was an error finding the daily riddle channel by its ID."
            );
            return;
        }

        Riddle riddle = new Riddle(
            dailyRiddleChannel,
            riddleMessage[0],
            riddleMessage[1]
        );
        HavenBot.getRiddleManager().addRiddleToList(riddle);

        // create embed for the response
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Riddle of The Day!");
        embedBuilder.setColor(Color.MAGENTA);
        embedBuilder.setDescription(riddle.getRiddle());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(HavenBot.POWERED_BY, HavenBot.ICON_URL);


        // send message to the daily-riddle channel
        dailyRiddleChannel
            .sendMessage("@everyone")
            .setEmbeds(embedBuilder.build())
            .queue();
    }
}
