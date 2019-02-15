package me.nallen.fox.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class FlagScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

    public static FlagScorerFragment newInstance(ScorerLocation scorerLocation) {
        FlagScorerFragment fragment = new FlagScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public FlagScorerFragment() {
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

    private SeekBar.OnSeekBarChangeListener flagListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                int row = 0;
                int column = 0;
                switch(seekBar.getId()) {
                    case R.id.high_flag_1_1: row = 0; column = 0; break;
                    case R.id.high_flag_1_2: row = 0; column = 1; break;
                    case R.id.high_flag_1_3: row = 0; column = 2; break;
                    case R.id.high_flag_2_1: row = 1; column = 0; break;
                    case R.id.high_flag_2_2: row = 1; column = 1; break;
                    case R.id.high_flag_2_3: row = 1; column = 2; break;
                    case R.id.low_flag_1: row = 2; column = 0; break;
                    case R.id.low_flag_2: row = 2; column = 1; break;
                    case R.id.low_flag_3: row = 2; column = 2; break;
                }

                if(row < 2) {
                    tcpClient.setHighFlag(row, column, ToggleState.fromPosition(progress));
                }
                else {
                    tcpClient.setLowFlag(column, ToggleState.fromPosition(progress));
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_flag_scorer, container, false);

        ((SeekBar)rootView.findViewById(R.id.high_flag_1_1)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.high_flag_1_2)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.high_flag_1_3)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.high_flag_2_1)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.high_flag_2_2)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.high_flag_2_3)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.low_flag_1)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.low_flag_2)).setOnSeekBarChangeListener(flagListener);
        ((SeekBar)rootView.findViewById(R.id.low_flag_3)).setOnSeekBarChangeListener(flagListener);

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
                tcpClient.setHighFlag(0, 0, ToggleState.BLUE);
                tcpClient.setHighFlag(1, 0, ToggleState.BLUE);
                tcpClient.setLowFlag(0, ToggleState.BLUE);
                tcpClient.setHighFlag(0, 1, ToggleState.NONE);
                tcpClient.setHighFlag(1, 1, ToggleState.NONE);
                tcpClient.setLowFlag(1, ToggleState.NONE);
                tcpClient.setHighFlag(0, 2, ToggleState.RED);
                tcpClient.setHighFlag(1, 2, ToggleState.RED);
                tcpClient.setLowFlag(2, ToggleState.RED);

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
                ((SeekBar)rootView.findViewById(R.id.high_flag_1_1)).setProgress(tcpClient.highFlags[0][0].getPosition());
                ((SeekBar)rootView.findViewById(R.id.high_flag_1_2)).setProgress(tcpClient.highFlags[0][1].getPosition());
                ((SeekBar)rootView.findViewById(R.id.high_flag_1_3)).setProgress(tcpClient.highFlags[0][2].getPosition());
                ((SeekBar)rootView.findViewById(R.id.high_flag_2_1)).setProgress(tcpClient.highFlags[1][0].getPosition());
                ((SeekBar)rootView.findViewById(R.id.high_flag_2_2)).setProgress(tcpClient.highFlags[1][1].getPosition());
                ((SeekBar)rootView.findViewById(R.id.high_flag_2_3)).setProgress(tcpClient.highFlags[1][2].getPosition());
                ((SeekBar)rootView.findViewById(R.id.low_flag_1)).setProgress(tcpClient.lowFlags[0].getPosition());
                ((SeekBar)rootView.findViewById(R.id.low_flag_2)).setProgress(tcpClient.lowFlags[1].getPosition());
                ((SeekBar)rootView.findViewById(R.id.low_flag_3)).setProgress(tcpClient.lowFlags[2].getPosition());

                rootView.findViewById(R.id.high_flag_1_1_color).setBackgroundColor(getFlagColor(tcpClient.highFlags[0][0]));
                rootView.findViewById(R.id.high_flag_1_2_color).setBackgroundColor(getFlagColor(tcpClient.highFlags[0][1]));
                rootView.findViewById(R.id.high_flag_1_3_color).setBackgroundColor(getFlagColor(tcpClient.highFlags[0][2]));
                rootView.findViewById(R.id.high_flag_2_1_color).setBackgroundColor(getFlagColor(tcpClient.highFlags[1][0]));
                rootView.findViewById(R.id.high_flag_2_2_color).setBackgroundColor(getFlagColor(tcpClient.highFlags[1][1]));
                rootView.findViewById(R.id.high_flag_2_3_color).setBackgroundColor(getFlagColor(tcpClient.highFlags[1][2]));
                rootView.findViewById(R.id.low_flag_1_color).setBackgroundColor(getFlagColor(tcpClient.lowFlags[0]));
                rootView.findViewById(R.id.low_flag_2_color).setBackgroundColor(getFlagColor(tcpClient.lowFlags[1]));
                rootView.findViewById(R.id.low_flag_3_color).setBackgroundColor(getFlagColor(tcpClient.lowFlags[2]));
            }
        });
    }

    private int getFlagColor(ToggleState state) {
        switch(state) {
            case BLUE: return ResourcesCompat.getColor(getResources(), R.color.vexBlue, null);
            case RED: return ResourcesCompat.getColor(getResources(), R.color.vexRed, null);
        }

        return Color.GRAY;
    }
}
