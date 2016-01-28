package me.nallen.fox.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "me.nallen.fox.app";

    private SharedPreferences mPrefs;
    private TcpClient tcpClient;

    ScorerLocation scorer_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tcpClient = TcpClient.getInstance();
        if(!tcpClient.isConnected()) {
            // We need to try connect
            mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            String fox_ip = mPrefs.getString("fox_ip", "");
            String automation_ip = mPrefs.getString("automation_ip", "");
            int scorer_location = mPrefs.getInt("scorer_location", -1);

            Intent localIntent = new Intent(this, ConnectActivity.class);
            localIntent.putExtra("fox_ip", fox_ip);
            localIntent.putExtra("automation_ip", automation_ip);
            localIntent.putExtra("scorer_location", scorer_location);

            startActivityForResult(localIntent, 1);
        }
    }
}
