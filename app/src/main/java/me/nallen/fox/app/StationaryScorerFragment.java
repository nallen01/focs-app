package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class StationaryScorerFragment extends Fragment implements DataListener {
    private TcpClient tcpClient;
    private View rootView;

    public static StationaryScorerFragment newInstance() {
        StationaryScorerFragment fragment = new StationaryScorerFragment();
        return fragment;
    }

    public StationaryScorerFragment() {
        tcpClient = TcpClient.getInstance();
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
            if(v.getId() == R.id.red_stationary_add_cone) {
                tcpClient.addRedStationaryCone();
            }
            else if(v.getId() == R.id.red_stationary_remove_cone) {
                tcpClient.removeRedStationaryCone();
            }
            else if(v.getId() == R.id.blue_stationary_add_cone) {
                tcpClient.addBlueStationaryCone();
            }
            else if(v.getId() == R.id.blue_stationary_remove_cone) {
                tcpClient.removeBlueStationaryCone();
            }

            updateUI();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stationary_scorer, container, false);

        rootView.findViewById(R.id.red_stationary_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.red_stationary_remove_cone).setOnClickListener(coneListener);

        rootView.findViewById(R.id.blue_stationary_add_cone).setOnClickListener(coneListener);
        rootView.findViewById(R.id.blue_stationary_remove_cone).setOnClickListener(coneListener);

        ((SeekBar)rootView.findViewById(R.id.red_parking)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    tcpClient.setRedParking(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        ((SeekBar)rootView.findViewById(R.id.blue_parking)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
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
                tcpClient.setRedStationaryCones(0);
                tcpClient.setRedParking(0);
                tcpClient.setBlueStationaryCones(0);
                tcpClient.setBlueParking(0);

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
                ((SeekBar)rootView.findViewById(R.id.red_parking)).setProgress(tcpClient.redParking);

                ((TextView)rootView.findViewById(R.id.red_stationary_cones)).setText("" + tcpClient.redStationaryCones);

                ((SeekBar)rootView.findViewById(R.id.blue_parking)).setProgress(tcpClient.blueParking);

                ((TextView)rootView.findViewById(R.id.blue_stationary_cones)).setText("" + tcpClient.blueStationaryCones);
            }
        });
    }
}
