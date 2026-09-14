package eu.konggdev.strikemaps.util.icon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import eu.konggdev.strikemaps.util.file.FileTools;

public final class IconResolver {
    private static final String TAG = "IconResolver";

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
}
