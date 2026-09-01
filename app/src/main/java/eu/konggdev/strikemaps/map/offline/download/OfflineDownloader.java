package eu.konggdev.strikemaps.map.offline.download;

import eu.konggdev.strikemaps.map.source.MapSource;

import java.net.URI;

public final class OfflineDownloader {

    public static String[] fetchAvailableExports(MapSource source) {
        //URI uri = URI.create(source.url);
        //String host = uri.getHost();
        return new String[0];
    }

    public static void download(MapSource source, String export) {
        //TODO
    }

    public static void download(MapSource source, int[] bounds) {
        //TODO
    }
}
