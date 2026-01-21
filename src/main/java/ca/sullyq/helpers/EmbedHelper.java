package ca.sullyq.helpers;

import ca.sullyq.HavenBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.*;
import java.time.Instant;

public class EmbedHelper {

    public static void sendErrorEmbed(MessageChannel channel, String errorMessage) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("An error has occurred");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription(errorMessage);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(HavenBot.POWERED_BY, HavenBot.ICON_URL);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void sendWarningEmbed(MessageChannel channel, String warningMessage) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Warning!");
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setDescription(warningMessage);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(HavenBot.POWERED_BY, HavenBot.ICON_URL);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void sendCorrectRiddleAnswerToChannel(MessageChannel channel, User user) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Winner Winner, Chicken Dinner!");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription(user.getAsMention() + " has guessed the correct answer!");
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(HavenBot.POWERED_BY, HavenBot.ICON_URL);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void sendRiddleAnswerResponseToDM(MessageChannel channel, boolean correctAnswer) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (correctAnswer) {
            embedBuilder.setTitle("Answer Correct!");
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setDescription("Good job! You got the answer correct!!");
        } else {
            embedBuilder.setTitle("Wrong Answer!");
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Sorry, wrong answer! Try again.");
        }
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(HavenBot.POWERED_BY, HavenBot.ICON_URL);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
