package me.nallen.fox.app;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "me.nallen.fox.app";

    private SharedPreferences mPrefs;

    int scorer_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String ip = mPrefs.getString("ip", "");
        scorer_location = mPrefs.getInt("location", -1);
    }
}
