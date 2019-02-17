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

public class CapsAndParkingScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

    public static CapsAndParkingScorerFragment newInstance(ScorerLocation scorerLocation) {
        CapsAndParkingScorerFragment fragment = new CapsAndParkingScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public CapsAndParkingScorerFragment() {
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

    private View.OnClickListener capListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.red_high_caps_add: tcpClient.addRedHighCap(); break;
                case R.id.red_high_caps_remove: tcpClient.removeRedHighCap(); break;
                case R.id.red_low_caps_add: tcpClient.addRedLowCap(); break;
                case R.id.red_low_caps_remove: tcpClient.removeRedLowCap(); break;
                case R.id.blue_high_caps_add: tcpClient.addBlueHighCap(); break;
                case R.id.blue_high_caps_remove: tcpClient.removeBlueHighCap(); break;
                case R.id.blue_low_caps_add: tcpClient.addBlueLowCap(); break;
                case R.id.blue_low_caps_remove: tcpClient.removeBlueLowCap(); break;
            }

            updateUI();
        }
    };

    private SeekBar.OnSeekBarChangeListener parkingListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                switch(seekBar.getId()) {
                    case R.id.red_parking_1: tcpClient.setRedParking(0, ParkingState.fromInt(progress)); break;
                    case R.id.red_parking_2: tcpClient.setRedParking(1, ParkingState.fromInt(progress)); break;
                    case R.id.blue_parking_1: tcpClient.setBlueParking(0, ParkingState.fromInt(progress)); break;
                    case R.id.blue_parking_2: tcpClient.setBlueParking(1, ParkingState.fromInt(progress)); break;
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
        rootView = inflater.inflate(R.layout.fragment_caps_parking_scorer, container, false);

        rootView.findViewById(R.id.red_high_caps_add).setOnClickListener(capListener);
        rootView.findViewById(R.id.red_high_caps_remove).setOnClickListener(capListener);

        rootView.findViewById(R.id.red_low_caps_add).setOnClickListener(capListener);
        rootView.findViewById(R.id.red_low_caps_remove).setOnClickListener(capListener);

        rootView.findViewById(R.id.blue_high_caps_add).setOnClickListener(capListener);
        rootView.findViewById(R.id.blue_high_caps_remove).setOnClickListener(capListener);

        rootView.findViewById(R.id.blue_low_caps_add).setOnClickListener(capListener);
        rootView.findViewById(R.id.blue_low_caps_remove).setOnClickListener(capListener);

        ((SeekBar)rootView.findViewById(R.id.red_parking_1)).setOnSeekBarChangeListener(parkingListener);
        ((SeekBar)rootView.findViewById(R.id.red_parking_2)).setOnSeekBarChangeListener(parkingListener);
        ((SeekBar)rootView.findViewById(R.id.blue_parking_1)).setOnSeekBarChangeListener(parkingListener);
        ((SeekBar)rootView.findViewById(R.id.blue_parking_2)).setOnSeekBarChangeListener(parkingListener);

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
                tcpClient.setRedHighCaps(0);
                tcpClient.setRedLowCaps(2);
                tcpClient.setBlueHighCaps(0);
                tcpClient.setBlueLowCaps(2);

                tcpClient.setRedParking(0, ParkingState.NONE);
                tcpClient.setRedParking(1, ParkingState.NONE);
                tcpClient.setBlueParking(0, ParkingState.NONE);
                tcpClient.setBlueParking(1, ParkingState.NONE);

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
                ((SeekBar)rootView.findViewById(R.id.red_parking_1)).setProgress(tcpClient.redParking[0].getValue());
                ((SeekBar)rootView.findViewById(R.id.red_parking_2)).setProgress(tcpClient.redParking[1].getValue());
                ((SeekBar)rootView.findViewById(R.id.blue_parking_1)).setProgress(tcpClient.blueParking[0].getValue());
                ((SeekBar)rootView.findViewById(R.id.blue_parking_2)).setProgress(tcpClient.blueParking[1].getValue());

                ((TextView)rootView.findViewById(R.id.red_high_caps)).setText("" + tcpClient.redHighCaps);
                ((TextView)rootView.findViewById(R.id.red_low_caps)).setText("" + tcpClient.redLowCaps);
                ((TextView)rootView.findViewById(R.id.blue_high_caps)).setText("" + tcpClient.blueHighCaps);
                ((TextView)rootView.findViewById(R.id.blue_low_caps)).setText("" + tcpClient.blueLowCaps);
            }
        });
    }
}
