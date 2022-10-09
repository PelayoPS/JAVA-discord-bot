package src.commands.audio.controls;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import src.commands.audio.lavaplayer.PlayerManager;
import src.util.commandPattern.Category;
import src.util.commandPattern.CommandInterface;

import java.net.URI;
import java.net.URISyntaxException;

public class Play implements CommandInterface {

    private static String name = "play";
    private Category category = Category.AUDIO;
    public Play() {
    }

    /**
     * When the command is excuted it plays a link from a song or playlist from youtube
     *
     * @param event The event
     */
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if(event.getMember().getVoiceState().getChannel() != null){
            event.getMember().getVoiceState().getChannel().getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
            play(event,event.getChannel().asTextChannel());

        } else {
            event.reply("You are not in a voice channel").queue();
        }
    }

    /**
     * returns the slash command data
     */
    @Override
    public CommandData getSlash() {
        CommandData commandData = Commands.slash(name, "Plays a song")
                .addOption(OptionType.STRING, "link", "Link to play", true);
        return commandData;
    }

    /**
     * returns the name of the command
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * returns the category of the command
     */
    @Override
    public Category getCategory() {
        return category;
    }

    /**
     * returns the name of the command for management
     */
    public static String getNameForManagement() {
        return name;
    }

    /**
     * Plays a song
     * if the link is from a song it adds ytsearch: to the link
     * @param event The event
     * @param channel The channel
     */
    private void play(SlashCommandInteractionEvent event, TextChannel channel) {
        String link = event.getOption("link").getAsString();
        if (link.contains("list")){
            //link doesnt need to be modified
        } else {
            if (!isUrl(link)) {
                link = "ytsearch:" + link;
            }
        }
        PlayerManager.getInstance().loadAndPlay(channel, link, event);
    }

    /**
     * Checks if the link is a valid url
     * @param url The url
     * @return true if it is a valid url
     */
    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
