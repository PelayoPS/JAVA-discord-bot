package src.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class StreamOnline extends ListenerAdapter {
    /**
     * When a slash command with the name streamonline is used this method is called
     * it sends a message to the channel with the users who are streaming and their links
     * @param event
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("streamonline")) {
            /*
             * if a user with the role Streamer is streaming it will send a message with their name and link
             */
            List<Member> streaming = event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Streamer", true).get(0))
                    .stream()
                    .filter(member -> member.getActivities().stream()
                            .anyMatch(activity -> activity.getType() == Activity.ActivityType.STREAMING)).collect(Collectors.toList());
            MessageEmbed messageEmbed = new EmbedBuilder()
                    .setTitle("Streamers")
                    .setDescription(streaming.stream().map(member -> member.getEffectiveName() + " - " + member.getActivities().get(0).getUrl()).collect(Collectors.joining("\n")))
                    .build();
            event.replyEmbeds(messageEmbed).queue();
        }
    }
}