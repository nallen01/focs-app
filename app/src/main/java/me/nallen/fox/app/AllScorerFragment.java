package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class AllScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

    public static AllScorerFragment newInstance(ScorerLocation scorerLocation) {
        AllScorerFragment fragment = new AllScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public AllScorerFragment() {
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

    private View.OnClickListener coneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.stationary_add_cone) {
                if(scorerLocation == ScorerLocation.RED_ALL) {
                    tcpClient.addRedStationaryCone();
                }
                else {
                    tcpClient.addBlueStationaryCone();
                }
            }
            else if(v.getId() == R.id.stationary_remove_cone) {
                if(scorerLocation == ScorerLocation.RED_ALL) {
                    tcpClient.removeRedStationaryCone();
                }
                else {
                    tcpClient.removeBlueStationaryCone();
                }
            }
            else if(v.getId() == R.id.mogo1_add_cone || v.getId() == R.id.mogo2_add_cone
                    || v.getId() == R.id.mogo3_add_cone || v.getId() == R.id.mogo4_add_cone) {
                int index = 0;
                switch(v.getId()) {
                    case R.id.mogo1_add_cone: index = 0; break;
                    case R.id.mogo2_add_cone: index = 1; break;
                    case R.id.mogo3_add_cone: index = 2; break;
                    case R.id.mogo4_add_cone: index = 3; break;
                }


                if(scorerLocation == ScorerLocation.RED_ALL) {
                    tcpClient.addRedBaseCone(index);
                }
                else {
                    tcpClient.addBlueBaseCone(index);
                }
            }
            else if(v.getId() == R.id.mogo1_remove_cone || v.getId() == R.id.mogo2_remove_cone
                    || v.getId() == R.id.mogo3_remove_cone || v.getId() == R.id.mogo4_remove_cone) {
                int index = 0;
                switch(v.getId()) {
                    case R.id.mogo1_remove_cone: index = 0; break;
                    case R.id.mogo2_remove_cone: index = 1; break;
                    case R.id.mogo3_remove_cone: index = 2; break;
                    case R.id.mogo4_remove_cone: index = 3; break;
                }


                if(scorerLocation == ScorerLocation.RED_ALL) {
                    tcpClient.removeRedBaseCone(index);
                }
                else {
                    tcpClient.removeBlueBaseCone(index);
                }
            }

            updateUI();
        }
    };

    private SeekBar.OnSeekBarChangeListener zoneListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                int index = 0;
                switch(seekBar.getId()) {
                    case R.id.mogo1_zone: index = 0; break;
                    case R.id.mogo2_zone: index = 1; break;
                    case R.id.mogo3_zone: index = 2; break;
                    case R.id.mogo4_zone: index = 3; break;
                }


                if(scorerLocation == ScorerLocation.RED_ALL) {
                    tcpClient.setRedBaseZone(index, ScoringZone.fromInt(progress));
                }
                else {
                    tcpClient.setBlueBaseZone(index, ScoringZone.fromInt(progress));
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
        rootView = inflater.inflate(R.layout.fragment_all_scorer, container, false);

        rootView.findViewById(R.id.mogo1_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.mogo1_remove_cone).setOnClickListener(coneListener);

        rootView.findViewById(R.id.mogo2_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.mogo2_remove_cone).setOnClickListener(coneListener);

        rootView.findViewById(R.id.mogo3_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.mogo3_remove_cone).setOnClickListener(coneListener);

        rootView.findViewById(R.id.mogo4_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.mogo4_remove_cone).setOnClickListener(coneListener);

        rootView.findViewById(R.id.stationary_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.stationary_remove_cone).setOnClickListener(coneListener);

        ((SeekBar)rootView.findViewById(R.id.mogo1_zone)).setOnSeekBarChangeListener(zoneListener);
        ((SeekBar)rootView.findViewById(R.id.mogo2_zone)).setOnSeekBarChangeListener(zoneListener);
        ((SeekBar)rootView.findViewById(R.id.mogo3_zone)).setOnSeekBarChangeListener(zoneListener);
        ((SeekBar)rootView.findViewById(R.id.mogo4_zone)).setOnSeekBarChangeListener(zoneListener);

        ((SeekBar)rootView.findViewById(R.id.parking)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(scorerLocation == ScorerLocation.RED_ALL)
                        tcpClient.setRedParking(progress);
                    else
                        tcpClient.setBlueParking(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                if(scorerLocation == ScorerLocation.RED_ALL) {
                    for(int i=0; i<4; i++) {
                        tcpClient.setRedBaseCones(i, 0);
                        tcpClient.setRedBaseZone(i, ScoringZone.NONE);
                    }
                    tcpClient.setRedStationaryCones(0);
                    tcpClient.setRedParking(0);
                }
                else {
                    for(int i=0; i<4; i++) {
                        tcpClient.setBlueBaseCones(i, 0);
                        tcpClient.setBlueBaseZone(i, ScoringZone.NONE);
                    }
                    tcpClient.setBlueStationaryCones(0);
                    tcpClient.setBlueParking(0);
                }

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
                if(scorerLocation == ScorerLocation.RED_ALL) {
                    ((SeekBar)rootView.findViewById(R.id.mogo1_zone)).setProgress(tcpClient.redBaseZones[0].getValue());
                    ((SeekBar)rootView.findViewById(R.id.mogo2_zone)).setProgress(tcpClient.redBaseZones[1].getValue());
                    ((SeekBar)rootView.findViewById(R.id.mogo3_zone)).setProgress(tcpClient.redBaseZones[2].getValue());
                    ((SeekBar)rootView.findViewById(R.id.mogo4_zone)).setProgress(tcpClient.redBaseZones[3].getValue());

                    ((SeekBar)rootView.findViewById(R.id.parking)).setProgress(tcpClient.redParking);

                    ((TextView)rootView.findViewById(R.id.mogo1_cones)).setText("" + tcpClient.redBaseCones[0]);
                    ((TextView)rootView.findViewById(R.id.mogo2_cones)).setText("" + tcpClient.redBaseCones[1]);
                    ((TextView)rootView.findViewById(R.id.mogo3_cones)).setText("" + tcpClient.redBaseCones[2]);
                    ((TextView)rootView.findViewById(R.id.mogo4_cones)).setText("" + tcpClient.redBaseCones[3]);
                    ((TextView)rootView.findViewById(R.id.stationary_cones)).setText("" + tcpClient.redStationaryCones);
                }
                else {
                    ((SeekBar)rootView.findViewById(R.id.mogo1_zone)).setProgress(tcpClient.blueBaseZones[0].getValue());
                    ((SeekBar)rootView.findViewById(R.id.mogo2_zone)).setProgress(tcpClient.blueBaseZones[1].getValue());
                    ((SeekBar)rootView.findViewById(R.id.mogo3_zone)).setProgress(tcpClient.blueBaseZones[2].getValue());
                    ((SeekBar)rootView.findViewById(R.id.mogo4_zone)).setProgress(tcpClient.blueBaseZones[3].getValue());

                    ((SeekBar)rootView.findViewById(R.id.parking)).setProgress(tcpClient.blueParking);

                    ((TextView)rootView.findViewById(R.id.mogo1_cones)).setText("" + tcpClient.blueBaseCones[0]);
                    ((TextView)rootView.findViewById(R.id.mogo2_cones)).setText("" + tcpClient.blueBaseCones[1]);
                    ((TextView)rootView.findViewById(R.id.mogo3_cones)).setText("" + tcpClient.blueBaseCones[2]);
                    ((TextView)rootView.findViewById(R.id.mogo4_cones)).setText("" + tcpClient.blueBaseCones[3]);
                    ((TextView)rootView.findViewById(R.id.stationary_cones)).setText("" + tcpClient.blueStationaryCones);
                }
            }
        });
    }
}
