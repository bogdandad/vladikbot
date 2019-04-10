package com.multiheaded.vladikbot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.multiheaded.vladikbot.VladikBot;
import com.multiheaded.vladikbot.services.audio.AudioHandler;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 * @author Oliver Johnson
 * Changes from original source:
 * - Reformating code
 * @author John Grosh
 */
public abstract class MusicCommand extends Command {
    protected final VladikBot bot;
    protected boolean bePlaying;
    protected boolean beListening;

    protected MusicCommand(VladikBot bot) {
        this.bot = bot;
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        TextChannel tchannel = bot.getSettings().getTextChannel(event.getGuild());

        if (tchannel != null && !event.getTextChannel().equals(tchannel)) {
            try {
                event.getMessage().delete().queue();
            } catch (PermissionException ignore) { /* Ignoring */ }
            event.replyInDm(String.format("%1$s You can only use that command in %2$s!",
                    event.getClient().getError(), tchannel.getAsMention()));
            return;
        }

        bot.getPlayerManager().setUpHandler(event.getGuild()); /* No point constantly checking for this later */
        if (bePlaying && !((AudioHandler) event.getGuild().getAudioManager().getSendingHandler())
                .isMusicPlaying(event.getJDA())) {
            event.replyError("There must be music playing to use that!");
            return;
        }

        if (beListening) {
            VoiceChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();
            if (current == null) {
                current = bot.getSettings().getVoiceChannel(event.getGuild());
            }

            GuildVoiceState userState = event.getMember().getVoiceState();
            if (!userState.inVoiceChannel()
                    || userState.isDeafened()
                    || (current != null && !userState.getChannel().equals(current))) {
                event.replyError(String.format("You must be listening in *$1%s* to use that!",
                        (current == null ? "a voice channel" : current.getName())));
                return;
            }
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
                try {
                    event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                } catch (PermissionException ex) {
                    event.replyError(String.format("I am unable to connect to **%1$s**!", userState.getChannel().getName()));
                    return;
                }
            }
        }

        doCommand(event);
    }

    protected abstract void doCommand(CommandEvent event);
}
