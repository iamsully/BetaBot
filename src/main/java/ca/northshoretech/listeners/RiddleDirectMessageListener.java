package ca.northshoretech.listeners;

import ca.northshoretech.BetaBot;
import ca.northshoretech.Riddle;
import ca.northshoretech.helpers.EmbedHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RiddleDirectMessageListener extends ListenerAdapter {

    private final String dailyRiddleChannelId = BetaBot.getConfig().get(
        "DAILY_RIDDLE_CHANNEL"
    );
    private final String guildId = BetaBot.getConfig().get("GUILD_ID");

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getType().isGuild()) return;
        if (event.getAuthor().isBot()) return;

        Riddle activeRiddle = BetaBot.getRiddleManager().getActiveRiddle();
        if (activeRiddle == null) {
            EmbedHelper.sendWarningEmbed(
                event.getChannel(),
                "There is no active riddle."
            );
            return;
        }

        String message = event.getMessage().getContentRaw();
        String riddleAnswer = BetaBot.getRiddleManager()
            .getActiveRiddle()
            .getAnswer()
            .trim();

        if (
            BetaBot.getRiddleManager().hasMemberAnsweredRiddle(
                event.getAuthor().getId()
            )
        ) {
            EmbedHelper.sendWarningEmbed(
                event.getChannel(),
                "You have already answered this riddle"
            );
            return;
        }

        // check if the message the user is sending contains the correct riddle answer
        if (
            message.contains(riddleAnswer.toLowerCase()) ||
            message.equalsIgnoreCase(riddleAnswer.toLowerCase())
        ) {
            Guild guild = event.getJDA().getGuildById(guildId);
            if (guild == null) {
                return;
            }
            // get the daily riddle channel, if it's not found send an error embed
            TextChannel dailyRiddleChannel = event
                .getJDA()
                .getTextChannelById(dailyRiddleChannelId);
            if (dailyRiddleChannel == null) {
                BetaBot.getLogger().error(
                    "There was an error finding the daily riddle channel by its ID {}",
                    dailyRiddleChannelId
                );
                EmbedHelper.sendErrorEmbed(
                    event.getChannel(),
                    "There was an error finding the daily riddle channel by its ID."
                );
                return;
            }
            BetaBot.getRiddleManager().addMemberToWhoAnsweredRiddles(
                event.getAuthor().getId()
            );
            EmbedHelper.sendRiddleAnswerResponseToDM(event.getChannel(), true);
            //send embed to the daily riddle channel letting members know, a member got the answer correct
            EmbedHelper.sendCorrectRiddleAnswerToChannel(
                dailyRiddleChannel,
                event.getAuthor()
            );
        } else {
            EmbedHelper.sendRiddleAnswerResponseToDM(event.getChannel(), false);
        }
    }
}
