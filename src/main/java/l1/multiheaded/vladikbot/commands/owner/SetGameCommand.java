package l1.multiheaded.vladikbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import l1.multiheaded.vladikbot.settings.Constants;
import net.dv8tion.jda.core.entities.Game;

/**
 * @author Oliver Johnson
 * Changes from original source:
 * - Reformating code
 * @author John Grosh
 */
public class SetGameCommand extends OwnerCommand {
    public SetGameCommand() {
        this.name = "setgame";
        this.arguments = "<action> <game>";
        this.help = "sets the game the bot is playing";
        this.guildOnly = false;
        this.children = new OwnerCommand[]{
                new SetListenCommand(),
                new SetStreamCommand(),
                new SetWatchCommand()
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        String title = event.getArgs().toLowerCase().startsWith(Constants.ACTION_PLAYING)
                ? event.getArgs().substring(Constants.ACTION_PLAYING.length()).trim()
                : event.getArgs();
        try {
            event.getJDA().getPresence().setGame(title.isEmpty() ? null : Game.playing(title));
            event.replySuccess(String.format("**%1$s** is %2$s.",
                    event.getSelfUser().getName(),
                    (title.isEmpty())
                            ? "no longer playing anything."
                            : "now playing `" + title + "`")
            );
        } catch (Exception e) {
            event.replyError("The game could not be set!");
        }
    }

    private static class SetStreamCommand extends OwnerCommand {
        private SetStreamCommand() {
            this.name = "stream";
            this.aliases = new String[]{"twitch", Constants.ACTION_STREAMING};
            this.help = "sets the game the bot is playing to a stream";
            this.arguments = "<username> <game>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parts = event.getArgs().split("\\s+", 2);
            if (parts.length < 2) {
                event.replyError("Please include a twitch username and the name of the game to 'stream'.");
                return;
            }

            try {
                event.getJDA().getPresence().setGame(Game.streaming(parts[1], "https://twitch.tv/" + parts[0]));
                event.replySuccess(String.format("**%1$s** is now streaming `%2$s`.", event.getSelfUser().getName(), parts[1]));
            } catch (Exception e) {
                event.replyError("The game could not be set!");
            }
        }
    }

    private static class SetListenCommand extends OwnerCommand {
        private SetListenCommand() {
            this.name = "listen";
            this.aliases = new String[]{Constants.ACTION_LISTENING};
            this.help = "sets the game the bot is listening to";
            this.arguments = "<title>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.replyError("Please include a title to listen to!");
                return;
            }

            String title = event.getArgs().toLowerCase().startsWith("to")
                    ? event.getArgs().substring(2).trim()
                    : event.getArgs();
            try {
                event.getJDA().getPresence().setGame(Game.listening(title));
                event.replySuccess(String.format("**%1$s** is now listening to `%2$s`.", event.getSelfUser().getName(), title));
            } catch (Exception e) {
                event.replyError("The game could not be set!");
            }
        }
    }

    private static class SetWatchCommand extends OwnerCommand {
        private SetWatchCommand() {
            this.name = "watch";
            this.aliases = new String[]{Constants.ACTION_WATCHING};
            this.help = "sets the game the bot is watching";
            this.arguments = "<title>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.replyError("Please include a title to watch!");
                return;
            }

            String title = event.getArgs();
            try {
                event.getJDA().getPresence().setGame(Game.watching(title));
                event.replySuccess(String.format("**%1$s** is now watching to `%2$s`.", event.getSelfUser().getName(), title));
            } catch (Exception e) {
                event.replyError("The game could not be set!");
            }
        }
    }
}
