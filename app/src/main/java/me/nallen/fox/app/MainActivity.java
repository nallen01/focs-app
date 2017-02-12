package me.nallen.fox.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements DataListener {
    private static final String PREFS_NAME = "me.nallen.fox.app";

    private SharedPreferences mPrefs;
    private TcpClient tcpClient;

    ScorerLocation scorer_location;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ConnectActivity.ACTIVITY_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                String fox_ip = data.getStringExtra("fox_ip");
                String automation_ip = data.getStringExtra("automation_ip");
                scorer_location = ScorerLocation.fromInt(data.getIntExtra("scorer_location", -1));

                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putString("fox_ip", fox_ip);
                ed.putString("automation_ip", automation_ip);
                ed.putInt("scorer_location", scorer_location.getValue());
                ed.commit();

                Toaster.doToast(getApplicationContext(), "Successfully Connected as " + scorer_location.getName());

                showScorer();
            }
            else {
                finish();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tcpClient = TcpClient.getInstance();
        tcpClient.addDataListener(this);
        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(!tcpClient.isConnected()) {
            // We need to try connect

            showConnectPage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        tcpClient.removeDataListener(this);
    }

    private void showScorer() {
        Fragment fragment = null;

        setTitle(scorer_location.getName());

        if(scorer_location == ScorerLocation.RED_FAR_ZONE || scorer_location == ScorerLocation.BLUE_FAR_ZONE) {
            fragment = ZoneScorerFragment.newInstance(scorer_location);
        }
        else if(scorer_location == ScorerLocation.RED_NEAR_ZONE || scorer_location == ScorerLocation.BLUE_NEAR_ZONE) {
            fragment = ZoneScorerFragment.newInstance(scorer_location);
        }
        else if(scorer_location == ScorerLocation.RED_ZONE || scorer_location == ScorerLocation.BLUE_ZONE) {
            fragment = AllZoneScorerFragment.newInstance(scorer_location, scorer_location == ScorerLocation.BLUE_ZONE);
        }
        else if(scorer_location == ScorerLocation.RED_STARS || scorer_location == ScorerLocation.BLUE_STARS) {
            Toaster.doToast(getApplicationContext(), "Not yet implemented");
            logout(false);
            return;
        }
        else if(scorer_location == ScorerLocation.CUBES) {
            Toaster.doToast(getApplicationContext(), "Not yet implemented");
            logout(false);
            return;
        }
        else if(scorer_location == ScorerLocation.COMMENTATOR || scorer_location == ScorerLocation.COMMENTATOR_AUTOMATION) {
            fragment = CommentatorFragment.newInstance(scorer_location);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
    }

    private void showConnectPage() {
        String fox_ip = mPrefs.getString("fox_ip", "");
        String automation_ip = mPrefs.getString("automation_ip", "");
        int scorer_location = mPrefs.getInt("scorer_location", -1);

        Intent localIntent = new Intent(this, ConnectActivity.class);
        localIntent.putExtra("fox_ip", fox_ip);
        localIntent.putExtra("automation_ip", automation_ip);
        localIntent.putExtra("scorer_location", scorer_location);

        startActivityForResult(localIntent, ConnectActivity.ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void connectionDropped() {
        tcpClient.logout();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toaster.doToast(getApplicationContext(), "Connection dropped");

                showConnectPage();
            }
        });


    }

    @Override
    public void updateUI() {

    }

    private void logout() {
        logout(true);
    }
    private void logout(boolean showMessage) {
        SharedPreferences.Editor ed = mPrefs.edit();
        //ed.remove("fox_ip");
        //ed.remove("automation_ip");
        ed.remove("scorer_location");
        ed.commit();

        tcpClient.logout();

        if(showMessage)
            Toaster.doToast(getApplicationContext(), "Logged Out");

        showConnectPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
