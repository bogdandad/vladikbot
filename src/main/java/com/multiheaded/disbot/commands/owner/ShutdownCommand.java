package com.multiheaded.disbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.multiheaded.disbot.VladikBot;

/**
 * @author Oliver Johnson
 * Changes from original source:
 * - Reformating code
 * - Added permissions
 * @author John Grosh
 */
public class ShutdownCommand extends OwnerCommand {
    private final VladikBot bot;

    public ShutdownCommand(VladikBot bot) {
        this.bot = bot;
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyWarning("Shutting down...");
        bot.shutdown();
    }
}
