package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ZoneScorerFragment extends Fragment {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;

    public static ZoneScorerFragment newInstance(ScorerLocation scorerLocation) {
        ZoneScorerFragment fragment = new ZoneScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public ZoneScorerFragment() {
        tcpClient = TcpClient.getInstance();
    }

    public void assignScorerLocation(ScorerLocation scorerLocation) {
        this.scorerLocation = scorerLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_zone_scorer, container, false);

        rootView.findViewById(R.id.button_high_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(scorerLocation == ScorerLocation.RED_ZONE)
                    tcpClient.addRedHighBall();
                else
                    tcpClient.addBlueHighBall();*/
            }
        });

        rootView.findViewById(R.id.button_low_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(scorerLocation == ScorerLocation.RED_ZONE)
                    tcpClient.addRedLowBall();
                else
                    tcpClient.addBlueLowBall();*/
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.goal_scorer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                /*if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.setRedHighBalls(0);
                    tcpClient.setRedLowBalls(0);
                }
                else {
                    tcpClient.setBlueHighBalls(0);
                    tcpClient.setBlueLowBalls(0);
                }*/

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
