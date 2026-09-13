package eu.konggdev.strikemaps.util.file;

import android.content.res.AssetManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;

//FIXME: Ugly
public final class FileTools {
    private static final String TAG = "FileUtils";

    public static Bitmap getIcon(String iconLocator, AppCompatActivity activity) {
        switch (iconLocator.split("//")[0]) {
            //TODO: https
            case "assets:":
                return BitmapFactory.decodeStream(FileTools.openAssetStream("bundled/icon/" + iconLocator.split("//")[1], activity));
            default:
                Log.e(TAG, "Unimplemented icon locator space: " + iconLocator);
                return null;
        }
    }

    public static String loadStringFromAssetFile(String filePath, AppCompatActivity activity) {
        try (InputStream is = activity.getAssets().open(filePath)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String[] getAssetFiles(String path, String fileExt, AppCompatActivity activity) {
        AssetManager assetManager = activity.getAssets();
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

    public static InputStream openAssetStream(String path, AppCompatActivity activity) {
        try {
            return activity.getAssets().open(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
