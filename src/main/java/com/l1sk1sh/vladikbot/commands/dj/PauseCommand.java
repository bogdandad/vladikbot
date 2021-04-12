package com.l1sk1sh.vladikbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.l1sk1sh.vladikbot.Bot;
import com.l1sk1sh.vladikbot.services.audio.AudioHandler;

import java.util.Objects;

/**
 * @author Oliver Johnson
 * Changes from original source:
 * - Reformating code
 * @author John Grosh
 */
public class PauseCommand extends DJCommand {
    public PauseCommand(Bot bot) {
        super(bot);
        this.name = "pause";
        this.help = "pauses the current song";
        this.bePlaying = true;
    }

    @Override
    public final void doCommand(CommandEvent event) {
        AudioHandler audioHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (Objects.requireNonNull(audioHandler).getPlayer().isPaused()) {
            event.replyWarning(String.format("The player is already paused! Use `%1$splay` to unpause!",
                    event.getClient().getPrefix()));
            return;
        }
        audioHandler.getPlayer().setPaused(true);
        event.replySuccess(String.format("Paused **%1$s**. Type `%2$splay` to unpause!",
                audioHandler.getPlayer().getPlayingTrack().getInfo().title,
                event.getClient().getPrefix()));
    }
}
