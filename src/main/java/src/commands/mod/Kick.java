package src.commands.mod;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Kick extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("kick")) {
            event.reply(event.getOption("user").getAsUser().getAsTag() + " has been kicked").queue(); // reply immediately
            User user = event.getOption("user").getAsUser();
            event.getGuild().kick(user).queue();
        }
    }
}