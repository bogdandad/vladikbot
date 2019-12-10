package com.l1sk1sh.vladikbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.l1sk1sh.vladikbot.commands.admin.*;
import com.l1sk1sh.vladikbot.commands.dj.*;
import com.l1sk1sh.vladikbot.commands.everyone.SettingsCommand;
import com.l1sk1sh.vladikbot.commands.everyone.StatusCommand;
import com.l1sk1sh.vladikbot.commands.music.*;
import com.l1sk1sh.vladikbot.commands.owner.*;
import com.l1sk1sh.vladikbot.settings.BotSettings;
import com.l1sk1sh.vladikbot.settings.BotSettingsManager;
import com.l1sk1sh.vladikbot.settings.GuildSpecificSettingsManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * @author Oliver Johnson
 */
// TODO Rewrite all processes
final class VladikBot {
    private VladikBot() {}

    private static final Logger log = LoggerFactory.getLogger(VladikBot.class);

    public static void main(String[] args) {
        try {
            EventWaiter waiter = new EventWaiter();
            BotSettingsManager botSettingsManager = new BotSettingsManager();
            GuildSpecificSettingsManager guildSpecificSettingsManager = new GuildSpecificSettingsManager();
            BotSettings botSettings = botSettingsManager.getSettings();

            Bot bot = new Bot(waiter, botSettingsManager, guildSpecificSettingsManager);

            CommandClientBuilder commandClientBuilder = new CommandClientBuilder()
                    .setPrefix(botSettings.getPrefix())
                    .setOwnerId(Long.toString(botSettings.getOwnerId()))
                    .setEmojis(botSettings.getSuccessEmoji(), botSettings.getWarningEmoji(), botSettings.getErrorEmoji())
                    .setHelpWord(botSettings.getHelpWord())
                    .setStatus((botSettings.getOnlineStatus() != OnlineStatus.UNKNOWN)
                            ? botSettings.getOnlineStatus() : OnlineStatus.DO_NOT_DISTURB)
                    .setGame((botSettings.getGame() != null)
                            ? botSettings.getGame() : Game.playing("your dad"))
                    .setLinkedCacheSize(200)
                    .addCommands(
                            new PingCommand(),
                            new SettingsCommand(botSettings, guildSpecificSettingsManager),
                            new StatusCommand(botSettings),
                            new DebugCommand(bot),

                            new SetNotificationChannelCommand(bot),
                            new SetDjCommand(bot),
                            new SetTextChannelCommand(bot),
                            new SetVoiceChannelCommand(bot),

                            new PermissionsCommand(),
                            new BackupMediaCommand(bot),
                            new BackupTextChannelCommand(bot),
                            new EmojiStatsCommand(waiter, bot),
                            new AutoReplyCommand(bot),
                            new GameAndActionSimulationCommand(bot),

                            new ForceSkipCommand(bot),
                            new PauseCommand(bot),
                            new PlayNextCommand(bot),
                            new RepeatCommand(bot),
                            new SkipToCommand(bot),
                            new StopCommand(bot),
                            new VolumeCommand(bot),
                            new MoveTrackCommand(bot),

                            new AutoPlaylistCommand(bot),
                            new PlaylistCommand(bot),
                            new SetAvatarCommand(),
                            new SetGameCommand(),
                            new SetNameCommand(),
                            new SetStatusCommand(),
                            new ClearTmpCommand(bot),
                            new AutoBackupCommand(bot),

                            new LyricsCommand(bot),
                            new NowPlayingCommand(bot),
                            new PlayCommand(bot),
                            new PlaylistsCommand(bot),
                            new QueueCommand(bot),
                            new RemoveCommand(bot),
                            new SearchCommand(bot),
                            new ShuffleCommand(bot),
                            new SkipCommand(bot),
                            new SoundCloudSearchCommand(bot),

                            new ShutdownCommand(bot)
                    );

            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(botSettings.getToken())
                    .setAudioEnabled(true)
                    .addEventListener(
                            waiter,
                            commandClientBuilder.build(),
                            new Listener(bot)
                    )
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        } catch (ExceptionInInitializerError e) {
            log.error("Problematic botSettings input.");
            System.exit(1);
        } catch (LoginException le) {
            log.error("Invalid username and/or password.");
            System.exit(1);
        }
    }
}
