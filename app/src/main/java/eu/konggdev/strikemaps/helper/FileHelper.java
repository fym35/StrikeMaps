package eu.konggdev.strikemaps.helper;

import android.content.res.AssetManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import eu.konggdev.strikemaps.app.AppController;

//FIXME: Ugly
public final class FileHelper {
    public static Bitmap getIcon(String iconLocator, AppController app) {
        switch (iconLocator.split("//")[0]) {
            //TODO: https
            case "assets:":
                return BitmapFactory.decodeStream(FileHelper.openAssetStream("bundled/icon/" + iconLocator.split("//")[1], app));
            default:
                app.logcat("Unimplemented icon locator space: " + iconLocator);
                return null;
        }
    }

    public static String loadStringFromAssetFile(String filePath, AppController app) {
        try (InputStream is = app.getActivity().getAssets().open(filePath)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void writeUserFile(String path, String fileName, String content, AppController app) throws IOException {
        try {
            File userDirectory = new File(app.getActivity().getExternalFilesDir(null), path);

            if (!userDirectory.exists() && !userDirectory.mkdirs()) {
                app.logcat("Failed to create directory: " + userDirectory.getAbsolutePath());
                return;
            }

            File file = new File(userDirectory, fileName);

            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

                writer.write(content);
                writer.flush();
            }

        } catch (IOException e) {
            throw e;
        }
    }

    public static String[] getAssetFiles(String path, String fileExt, AppController app) {
        AssetManager assetManager = app.getActivity().getAssets();
        try {
            if (path != null && path.startsWith("/")) {
                path = path.substring(1);
            }

            String fullPath = (path == null || path.isEmpty()) ? "" : path;

            String[] files = assetManager.list(fullPath);

            if (files == null) return new String[0];

            if (fileExt == null || fileExt.isEmpty())
                return files;

            List<String> filtered = new ArrayList<>();
            for (String file : files) {
                if (file.toLowerCase().endsWith(fileExt.toLowerCase())) {
                    filtered.add((fullPath.isEmpty() ? "" : fullPath + "/") + file);
                }
            }

            return filtered.toArray(new String[0]);

        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static InputStream openAssetStream(String path, AppController app) {
        try {
            return app.getActivity().getAssets().open(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
