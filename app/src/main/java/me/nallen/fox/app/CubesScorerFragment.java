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

public class CubesScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;
    private boolean isFlipped = false;

    private int[] prevSliderValues = {2, 2, 2, 2};

    public static CubesScorerFragment newInstance(ScorerLocation scorerLocation) {
        CubesScorerFragment fragment = new CubesScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public CubesScorerFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cubes_scorer, container, false);

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_a)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(0, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_b)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(1, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_c)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(2, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_d)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(3, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        rootView.findViewById(R.id.button_red_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setRedAuton(true);

                updateUI();
            }
        });

        rootView.findViewById(R.id.button_blue_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setBlueAuton(true);

                updateUI();
            }
        });

        rootView.findViewById(R.id.button_no_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setRedAuton(false);
                tcpClient.setBlueAuton(false);

                updateUI();
            }
        });

        return rootView;
    }

    private void updateSlider(int id, int newVal) {
        switch(prevSliderValues[id]) {
            case 0: tcpClient.removeRedFarCube(); break;
            case 1: tcpClient.removeRedNearCube(); break;
            case 3: tcpClient.removeBlueNearCube(); break;
            case 4: tcpClient.removeBlueFarCube(); break;
        }

        prevSliderValues[id] = newVal;

        switch(newVal) {
            case 0: tcpClient.addRedFarCube(); break;
            case 1: tcpClient.addRedNearCube(); break;
            case 3: tcpClient.addBlueNearCube(); break;
            case 4: tcpClient.addBlueFarCube(); break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.elevation_scorer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                tcpClient.setRedFarCubes(1);
                tcpClient.setRedNearCubes(0);
                tcpClient.setBlueNearCubes(0);
                tcpClient.setBlueFarCubes(1);

                tcpClient.setRedElevatedState(ElevatedState.NONE);
                tcpClient.setBlueElevatedState(ElevatedState.NONE);

                tcpClient.setRedAuton(false);
                tcpClient.setBlueAuton(false);

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
                int[] vals = {2, 2, 2, 2};

                int current = 0;

                for(int i=0; i<tcpClient.redFarCubes; i++) {
                    if(current >= 4)
                        break;
                    vals[current++] = 0;
                }
                for(int i=0; i<tcpClient.redNearCubes; i++) {
                    if(current >= 4)
                        break;
                    vals[current++] = 1;
                }
                for(int i=0; i<tcpClient.blueNearCubes; i++) {
                    if(current >= 4)
                        break;
                    vals[current++] = 3;
                }
                for(int i=0; i<tcpClient.blueFarCubes; i++) {
                    if(current >= 4)
                        break;
                    vals[current++] = 4;
                }

                ((SeekBar)rootView.findViewById(R.id.seekbar_cube_a)).setProgress(vals[0]);
                ((SeekBar)rootView.findViewById(R.id.seekbar_cube_b)).setProgress(vals[1]);
                ((SeekBar)rootView.findViewById(R.id.seekbar_cube_c)).setProgress(vals[2]);
                ((SeekBar)rootView.findViewById(R.id.seekbar_cube_d)).setProgress(vals[3]);

                prevSliderValues = vals;

                rootView.findViewById(R.id.button_red_auton).setPressed(tcpClient.redAuton);
                rootView.findViewById(R.id.button_blue_auton).setPressed(tcpClient.blueAuton);
                rootView.findViewById(R.id.button_no_auton).setPressed((!tcpClient.redAuton) && (!tcpClient.blueAuton));
            }
        });
    }
}
