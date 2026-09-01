package eu.konggdev.strikemaps;

import eu.konggdev.strikemaps.app.AppController;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    AppController app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = new AppController(this);
        setContentView(R.layout.view_main);
        app.init();
    }

    public void logcat(String tag, String log) {
        Log.i(tag, log);
    }

    public void logcat(String log) {
        Log.i("LogcatGeneric", log);
    }

    @Override
    public void onBackPressed() {
        if (!app.getUi().back())
            super.onBackPressed();
    }
}
