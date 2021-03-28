package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BallScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

    public static BallScorerFragment newInstance(ScorerLocation scorerLocation) {
        BallScorerFragment fragment = new BallScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public BallScorerFragment() {
        tcpClient = TcpClient.getInstance();
    }

    public void assignScorerLocation(ScorerLocation scorerLocation) {
        this.scorerLocation = scorerLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        tcpClient.addDataListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tcpClient.removeDataListener(this);
    }

    private View.OnClickListener ballListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.red_balls_add: tcpClient.addRedBall(); break;
                case R.id.red_balls_remove: tcpClient.removeRedBall(); break;
                case R.id.blue_balls_add: tcpClient.addBlueBall(); break;
                case R.id.blue_balls_remove: tcpClient.removeBlueBall(); break;
            }

            updateUI();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ball_scorer, container, false);

        rootView.findViewById(R.id.red_balls_add).setOnClickListener(ballListener);
        rootView.findViewById(R.id.red_balls_remove).setOnClickListener(ballListener);

        rootView.findViewById(R.id.blue_balls_add).setOnClickListener(ballListener);
        rootView.findViewById(R.id.blue_balls_remove).setOnClickListener(ballListener);

        if(scorerLocation == ScorerLocation.BALLS_RED) {
            rootView.findViewById(R.id.blue_label).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.blue_balls_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_balls_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_balls).setVisibility(View.INVISIBLE);
        }
        else if(scorerLocation == ScorerLocation.BALLS_BLUE) {
            rootView.findViewById(R.id.red_label).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.red_balls_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_balls_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_balls).setVisibility(View.INVISIBLE);
        }

        updateUI();

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
//                tcpClient.setRedBalls(9);
//                tcpClient.setBlueBalls(9);

                tcpClient.setRedBalls(0);
                tcpClient.setBlueBalls(0);

                updateUI();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectionDropped() {

    }

    @Override
    public void updateUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)rootView.findViewById(R.id.red_balls)).setText("" + tcpClient.redBalls);
                ((TextView)rootView.findViewById(R.id.blue_balls)).setText("" + tcpClient.blueBalls);
            }
        });
    }
}
