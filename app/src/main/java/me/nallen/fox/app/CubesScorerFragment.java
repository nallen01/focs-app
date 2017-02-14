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
import android.widget.Button;
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
        if(isFlipped) {
            rootView = inflater.inflate(R.layout.fragment_cubes_scorer_flip, container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_cubes_scorer, container, false);
        }

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_a)).setOnSeekBarChangeListener(new MySeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(0, progress);
            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_b)).setOnSeekBarChangeListener(new MySeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(1, progress);
            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_c)).setOnSeekBarChangeListener(new MySeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(2, progress);
            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_cube_d)).setOnSeekBarChangeListener(new MySeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    updateSlider(3, progress);
            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_blue_elevation)).setOnSeekBarChangeListener(new MySeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    tcpClient.setBlueElevatedState(ElevatedState.fromInt(progress));
            }
        });


        ((SeekBar)rootView.findViewById(R.id.seekbar_red_elevation)).setOnSeekBarChangeListener(new MySeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    tcpClient.setRedElevatedState(ElevatedState.fromInt(progress));
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

        updateUI();

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
        inflater.inflate(R.menu.goal_scorer, menu);
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
            case R.id.flip:
                this.isFlipped = !this.isFlipped;

                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.detach(this);
                fragTransaction.attach(this);
                fragTransaction.commit();
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

                if(tcpClient.redAuton) {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("[[Red]]");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("Blue");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("None");
                }
                else if(tcpClient.blueAuton) {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("Red");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("[[Blue]]");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("None");
                }
                else {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("Red");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("Blue");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("[[None]]");
                }

                ((SeekBar)rootView.findViewById(R.id.seekbar_blue_elevation)).setProgress(tcpClient.blueElevation.getValue());
                ((SeekBar)rootView.findViewById(R.id.seekbar_red_elevation)).setProgress(tcpClient.redElevation.getValue());
            }
        });
    }

    private abstract class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        public abstract void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) ;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
