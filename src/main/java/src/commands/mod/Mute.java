package src.commands.mod;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import src.DiscordBot;

import java.util.List;

public class Mute extends ListenerAdapter {

    Dotenv config = DiscordBot.getConfig();
    /**
     * When a slash command with the name mute is used this method is called
     * it mutes the user given for the amount of time given
     * @param event
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("mute")) {
            if(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)){
                return;
            }
            try {
                muteUser(event.getOption("user"), event.getOption("time"));
                logMute(event, event.getOption("user"), event.getOption("time"));
            } catch (HierarchyException e) {
                event.reply("I can't mute a user with higher or equal role than me").queue();
            }
        }
    }

    /**
     * private method to make the code cleaner
     * @param user
     * @param time
     */
    private void muteUser(OptionMapping user, OptionMapping time) throws HierarchyException {
        /*
         * sends a message to the user saying they have been muted
         * gives the user the muted role
         * removes the muted role after the time given
         */
        user.getAsUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage("You have been muted for " + time.getAsLong() + " seconds").queue();
        });
        //muted rol
        Role role = user.getAsMember().getGuild().getRolesByName("muted",true).get(0);
        //adds muted role
        user.getAsMember().getGuild()
            .addRoleToMember(user.getAsMember(), role).queue();
        List<Role> roles = user.getAsMember().getRoles();
        user.getAsMember().getRoles().forEach((r) -> {
            //removes all other roles
            user.getAsMember().getGuild()
                .removeRoleFromMember(user.getAsMember(), r).queue();
        });
        //removes muted role after time given
        user.getAsMember().getGuild()
            .removeRoleFromMember(user.getAsMember(), role)
                .queueAfter(time.getAsLong(), java.util.concurrent.TimeUnit.SECONDS);
        roles.forEach((r) -> {
            //removes all other roles
            user.getAsMember().getGuild()
                    .addRoleToMember(user.getAsMember(), r).queueAfter(time.getAsLong(), java.util.concurrent.TimeUnit.SECONDS);;
        });
    }

    private void logMute(SlashCommandInteractionEvent event, OptionMapping user, OptionMapping time) {
        /*
         * sends a message to the log channel saying who muted who for how long
         */
        event.getGuild().getTextChannelById(config.get("MUTEDLOGCHANNELID"))
            .sendMessage(user.getAsUser().getAsTag() +
                    " was muted by " + event.getUser().getAsTag() +
                    " for " + time.getAsLong() + " seconds with reason: " + event.getOption("reason").getAsString()).queue();
        event.reply("Muted " + user.getAsUser().getAsTag() + " for " + time.getAsLong() + " seconds").queue();
    }
}
