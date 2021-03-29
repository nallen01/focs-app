package me.nallen.fox.app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GoalScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

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
        setHasOptionsMenu(true);
        tcpClient.addDataListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tcpClient.removeDataListener(this);
    }

    private Button.OnClickListener goalListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            int x = 0, y = 0;
            BallType type = BallType.NONE;
            switch(view.getId()) {
                case R.id.goal_0_0_ball_none: x = 0; y = 0; type = BallType.NONE; break;
                case R.id.goal_0_0_ball_red: x = 0; y = 0; type = BallType.RED; break;
                case R.id.goal_0_0_ball_blue: x = 0; y = 0;type = BallType.BLUE; break;
                case R.id.goal_0_1_ball_none: x = 0; y = 1; type = BallType.NONE; break;
                case R.id.goal_0_1_ball_red: x = 0; y = 1; type = BallType.RED; break;
                case R.id.goal_0_1_ball_blue: x = 0; y = 1;type = BallType.BLUE; break;
                case R.id.goal_0_2_ball_none: x = 0; y = 2; type = BallType.NONE; break;
                case R.id.goal_0_2_ball_red: x = 0; y = 2; type = BallType.RED; break;
                case R.id.goal_0_2_ball_blue: x = 0; y = 2;type = BallType.BLUE; break;

                case R.id.goal_1_0_ball_none: x = 1; y = 0; type = BallType.NONE; break;
                case R.id.goal_1_0_ball_red: x = 1; y = 0; type = BallType.RED; break;
                case R.id.goal_1_0_ball_blue: x = 1; y = 0;type = BallType.BLUE; break;
                case R.id.goal_1_1_ball_none: x = 1; y = 1; type = BallType.NONE; break;
                case R.id.goal_1_1_ball_red: x = 1; y = 1; type = BallType.RED; break;
                case R.id.goal_1_1_ball_blue: x = 1; y = 1;type = BallType.BLUE; break;
                case R.id.goal_1_2_ball_none: x = 1; y = 2; type = BallType.NONE; break;
                case R.id.goal_1_2_ball_red: x = 1; y = 2; type = BallType.RED; break;
                case R.id.goal_1_2_ball_blue: x = 1; y = 2;type = BallType.BLUE; break;

                case R.id.goal_2_0_ball_none: x = 2; y = 0; type = BallType.NONE; break;
                case R.id.goal_2_0_ball_red: x = 2; y = 0; type = BallType.RED; break;
                case R.id.goal_2_0_ball_blue: x = 2; y = 0;type = BallType.BLUE; break;
                case R.id.goal_2_1_ball_none: x = 2; y = 1; type = BallType.NONE; break;
                case R.id.goal_2_1_ball_red: x = 2; y = 1; type = BallType.RED; break;
                case R.id.goal_2_1_ball_blue: x = 2; y = 1;type = BallType.BLUE; break;
                case R.id.goal_2_2_ball_none: x = 2; y = 2; type = BallType.NONE; break;
                case R.id.goal_2_2_ball_red: x = 2; y = 2; type = BallType.RED; break;
                case R.id.goal_2_2_ball_blue: x = 2; y = 2;type = BallType.BLUE; break;
            }

            tcpClient.setGoalOwnership(x, y, type);

            switch(x*3+y) {
                case 0: rootView.findViewById(R.id.goal_0_0_ball).setBackgroundColor(type.getColor()); break;
                case 1: rootView.findViewById(R.id.goal_0_1_ball).setBackgroundColor(type.getColor()); break;
                case 2: rootView.findViewById(R.id.goal_0_2_ball).setBackgroundColor(type.getColor()); break;
                case 3: rootView.findViewById(R.id.goal_1_0_ball).setBackgroundColor(type.getColor()); break;
                case 4: rootView.findViewById(R.id.goal_1_1_ball).setBackgroundColor(type.getColor()); break;
                case 5: rootView.findViewById(R.id.goal_1_2_ball).setBackgroundColor(type.getColor()); break;
                case 6: rootView.findViewById(R.id.goal_2_0_ball).setBackgroundColor(type.getColor()); break;
                case 7: rootView.findViewById(R.id.goal_2_1_ball).setBackgroundColor(type.getColor()); break;
                case 8: rootView.findViewById(R.id.goal_2_2_ball).setBackgroundColor(type.getColor()); break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goal_scorer, container, false);

        rootView.findViewById(R.id.goal_0_0_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_0_0_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_0_0_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_0_1_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_0_1_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_0_1_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_0_2_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_0_2_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_0_2_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_1_0_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_1_0_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_1_0_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_1_1_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_1_1_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_1_1_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_1_2_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_1_2_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_1_2_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_2_0_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_2_0_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_2_0_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_2_1_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_2_1_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_2_1_ball_blue).setOnClickListener(goalListener);

        rootView.findViewById(R.id.goal_2_2_ball_none).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_2_2_ball_red).setOnClickListener(goalListener);
        rootView.findViewById(R.id.goal_2_2_ball_blue).setOnClickListener(goalListener);

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
//                tcpClient.setGoalOwnership(0, 0, BallType.RED);
//                tcpClient.setGoalOwnership(0, 1, BallType.BLUE);
//                tcpClient.setGoalOwnership(0, 2, BallType.BLUE);
//                tcpClient.setGoalOwnership(1, 0, BallType.RED);
//                tcpClient.setGoalOwnership(1, 1, BallType.NONE);
//                tcpClient.setGoalOwnership(1, 2, BallType.BLUE);
//                tcpClient.setGoalOwnership(2, 0, BallType.RED);
//                tcpClient.setGoalOwnership(2, 1, BallType.RED);
//                tcpClient.setGoalOwnership(2, 2, BallType.BLUE);

                tcpClient.setGoalOwnership(0, 0, BallType.NONE);
                tcpClient.setGoalOwnership(0, 1, BallType.NONE);
                tcpClient.setGoalOwnership(0, 2, BallType.NONE);
                tcpClient.setGoalOwnership(1, 0, BallType.NONE);
                tcpClient.setGoalOwnership(1, 1, BallType.NONE);
                tcpClient.setGoalOwnership(1, 2, BallType.NONE);
                tcpClient.setGoalOwnership(2, 0, BallType.NONE);
                tcpClient.setGoalOwnership(2, 1, BallType.NONE);
                tcpClient.setGoalOwnership(2, 2, BallType.NONE);

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
                rootView.findViewById(R.id.goal_0_0_ball).setBackgroundColor(tcpClient.goalOwnership[0][0].getColor());
                rootView.findViewById(R.id.goal_0_1_ball).setBackgroundColor(tcpClient.goalOwnership[0][1].getColor());
                rootView.findViewById(R.id.goal_0_2_ball).setBackgroundColor(tcpClient.goalOwnership[0][2].getColor());
                rootView.findViewById(R.id.goal_1_0_ball).setBackgroundColor(tcpClient.goalOwnership[1][0].getColor());
                rootView.findViewById(R.id.goal_1_1_ball).setBackgroundColor(tcpClient.goalOwnership[1][1].getColor());
                rootView.findViewById(R.id.goal_1_2_ball).setBackgroundColor(tcpClient.goalOwnership[1][2].getColor());
                rootView.findViewById(R.id.goal_2_0_ball).setBackgroundColor(tcpClient.goalOwnership[2][0].getColor());
                rootView.findViewById(R.id.goal_2_1_ball).setBackgroundColor(tcpClient.goalOwnership[2][1].getColor());
                rootView.findViewById(R.id.goal_2_2_ball).setBackgroundColor(tcpClient.goalOwnership[2][2].getColor());
            }
        });
    }
}
