package com.l1sk1sh.vladikbot.services.meme;

import com.l1sk1sh.vladikbot.data.repository.SentMemeRepository;
import com.l1sk1sh.vladikbot.services.notification.MemeNotificationService;
import com.l1sk1sh.vladikbot.settings.BotSettingsManager;
import com.l1sk1sh.vladikbot.settings.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author l1sk1sh
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemeService {

    @Qualifier("frontThreadPool")
    private final ScheduledExecutorService frontThreadPool;
    private final BotSettingsManager settings;
    private final SentMemeRepository sentMemeRepository;
    private final MemeNotificationService memeNotificationService;
    private boolean initialized = false;
    private ScheduledFuture<?> scheduledMemeFeed;

    public void start() {
        if (!settings.get().isSendMemes()) {
            if (initialized) {
                stop();
            }
            return;
        }

        if (initialized) {
            log.info("Meme Service is already running.");
            return;
        }

        scheduledMemeFeed = frontThreadPool.scheduleWithFixedDelay(
                new MemeFeedTask(sentMemeRepository, memeNotificationService),
                10,
                Const.MEMES_UPDATE_FREQUENCY_IN_SECONDS,
                TimeUnit.SECONDS
        );

        log.info("Meme Service has been launched.");
        initialized = true;
    }

    public void stop() {
        if (!initialized) {
            log.info("Meme Service is already stopped.");
            return;
        }

        scheduledMemeFeed.cancel(false);

        log.info("Meme Service has been stopped.");
        initialized = false;
    }
}
