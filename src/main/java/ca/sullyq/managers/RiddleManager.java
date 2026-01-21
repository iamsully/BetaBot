package ca.sullyq.managers;

import ca.sullyq.HavenBot;
import ca.sullyq.Riddle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class RiddleManager {

    private final ArrayList<Riddle> activeRiddles;
    private final ArrayList<String> membersWhoAnsweredRiddles;

    public RiddleManager() {
        this.membersWhoAnsweredRiddles = new ArrayList<>();
        this.activeRiddles = new ArrayList<>();
    }

    /**
     * sends the answer of the riddle to a specific channel
     *
     * @param channel the text channel to send the answer of the riddle in
     */
    public void sendRiddleAnswer(TextChannel channel) {
        final Riddle riddle = activeRiddles.getFirst();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("The Answer For The Riddle of The Day is!");
        embedBuilder.setColor(Color.MAGENTA);
        embedBuilder.setDescription(riddle.getAnswer());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(HavenBot.POWERED_BY, HavenBot.ICON_URL);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();

        // TODO: Im not sure this is needed
        riddle.setIsCompleted(true);
    }

    /**
     * gets the first active riddle ( there should only be one riddle at a time)
     *
     * @return Riddle
     */
    public Riddle getActiveRiddle() {
        if (activeRiddles.isEmpty()) {
            return null;
        }
        return this.activeRiddles.getFirst();
    }

    /**
     * add the riddle to the active riddle list
     *
     * @param riddle the current riddle to add to the list
     */
    public void addRiddleToList(Riddle riddle) {
        // TODO: Make sure there is only one riddle in the list, duplicates may cause issues with the thread scheduling of sending the response
        activeRiddles.add(riddle);
    }

    /**
     * remove the first and only riddle from the list
     */
    public void removeRiddleFromList() {
        activeRiddles.removeFirst();
    }

    /**
     * adds a specific member id to the list of members who have already answered the riddle correctly
     *
     * @param memberId the id of the member to be added to the answered riddle list
     */
    public void addMemberToWhoAnsweredRiddles(String memberId) {
        if (!membersWhoAnsweredRiddles.contains(memberId)) {
            membersWhoAnsweredRiddles.add(memberId);
        }
    }

    /**
     * check to see if the member has answered the riddle already
     *
     * @param memberId the id of the member to check
     * @return boolean
     */
    public boolean hasMemberAnsweredRiddle(String memberId) {
        return membersWhoAnsweredRiddles.contains(memberId);
    }

    /**
     * clear the list of members who have answered the riddle
     */
    public void clearMemberAnsweredRiddlesList() {
        membersWhoAnsweredRiddles.clear();
    }

}
