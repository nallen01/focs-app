package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GoalScorerFragment extends Fragment {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;

    public static GoalScorerFragment newInstance(ScorerLocation scorerLocation) {
        GoalScorerFragment fragment = new GoalScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public GoalScorerFragment() {
        tcpClient = TcpClient.getInstance();
    }

    public void assignScorerLocation(ScorerLocation scorerLocation) {
        this.scorerLocation = scorerLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_scorer, container, false);

        rootView.findViewById(R.id.button_high_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_GOAL)
                    tcpClient.addRedHighBall();
                else
                    tcpClient.addBlueHighBall();
            }
        });

        rootView.findViewById(R.id.button_low_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_GOAL)
                    tcpClient.addRedLowBall();
                else
                    tcpClient.addBlueLowBall();
            }
        });

        return rootView;
    }
}
