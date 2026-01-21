package ca.sullyq.listeners;

import ca.sullyq.HavenBot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

    /**
     * Handles the on ready event and prints that the bot as started
     *
     * @param event the ready event when the bot is successfully started
     */
    @Override
    public void onReady(ReadyEvent event) {
        HavenBot.getLogger().info("HavenBot is now online");
    }
}
