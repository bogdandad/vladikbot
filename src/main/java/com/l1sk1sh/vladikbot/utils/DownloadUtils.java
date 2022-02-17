package com.l1sk1sh.vladikbot.utils;

import com.l1sk1sh.vladikbot.settings.Const;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author l1sk1sh
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DownloadUtils {

    public static String getFilenameFromUrl(String url) {
        String tempUrl = StringUtils.replaceLast(url, "/", "_"); /* Replacing last '/' */
        Matcher urlNameMatcher = Pattern.compile("[^/]+$").matcher(tempUrl); /* Getting exact file name */
        if (urlNameMatcher.find()) {
            return urlNameMatcher.group();
        }

        return "";
    }

    public static boolean downloadAndSaveToFolder(URL url, String pathToSave) throws IOException {
        String fileName = getFilenameFromUrl(url.toString());
        return downloadAndSaveToFile(url, (fileName.isEmpty()) ? null : (pathToSave + fileName));
    }

    public static boolean downloadAndSaveToFile(URL url, String localFileNamePath) throws IOException {
        if (!FileUtils.fileOrFolderIsAbsent(localFileNamePath)) {
            return true;
        }

        if (url == null) {
            return false;
        }

        // copyURLToFile() from Commons library won't work without user agent due to 403

        log.debug("Downloading file [{}].", localFileNamePath);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", Const.USER_AGENT);

        try (ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(localFileNamePath);
             FileChannel writeChannel = fileOutputStream.getChannel()) {

            writeChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        return true;
    }
}
