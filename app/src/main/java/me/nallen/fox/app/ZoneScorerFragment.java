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

public class ZoneScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

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
        tcpClient.addDataListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tcpClient.removeDataListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_zone_scorer, container, false);

        if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE || scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
            rootView.findViewById(R.id.divider).setBackgroundResource(R.color.vexBlue);
            rootView.findViewById(R.id.divider_two).setBackgroundResource(R.color.vexBlue);
        }
        else {
            rootView.findViewById(R.id.divider).setBackgroundResource(R.color.vexRed);
            rootView.findViewById(R.id.divider_two).setBackgroundResource(R.color.vexRed);
        }

        if(scorerLocation == ScorerLocation.RED_NEAR_ZONE || scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
            rootView.findViewById(R.id.seekbar_elevation).setVisibility(View.INVISIBLE);
        }

        rootView.findViewById(R.id.button_add_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    tcpClient.addRedFarCube();
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    tcpClient.addBlueFarCube();
                }
                else if(scorerLocation == ScorerLocation.RED_NEAR_ZONE) {
                    tcpClient.addRedNearCube();
                }
                else if(scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
                    tcpClient.addBlueNearCube();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_add_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    tcpClient.addRedFarStar();
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    tcpClient.addBlueFarStar();
                }
                else if(scorerLocation == ScorerLocation.RED_NEAR_ZONE) {
                    tcpClient.addRedNearStar();
                }
                else if(scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
                    tcpClient.addBlueNearStar();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_remove_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    tcpClient.removeRedFarCube();
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    tcpClient.removeBlueFarCube();
                }
                else if(scorerLocation == ScorerLocation.RED_NEAR_ZONE) {
                    tcpClient.removeRedNearCube();
                }
                else if(scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
                    tcpClient.removeBlueNearCube();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_remove_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    tcpClient.removeRedFarStar();
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    tcpClient.removeBlueFarStar();
                }
                else if(scorerLocation == ScorerLocation.RED_NEAR_ZONE) {
                    tcpClient.removeRedNearStar();
                }
                else if(scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
                    tcpClient.removeBlueNearStar();
                }
                updateUI();
            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_elevation)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ElevatedState state = ElevatedState.fromInt(progress);

                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    tcpClient.setRedElevatedState(state);
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    tcpClient.setBlueElevatedState(state);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    tcpClient.setRedFarCubes(0);
                    tcpClient.setRedFarStars(0);
                    tcpClient.setRedElevatedState(ElevatedState.NONE);
                }
                else if(scorerLocation == ScorerLocation.RED_NEAR_ZONE) {
                    tcpClient.setRedNearCubes(0);
                    tcpClient.setRedNearStars(0);
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    tcpClient.setBlueFarCubes(0);
                    tcpClient.setBlueFarStars(0);
                    tcpClient.setBlueElevatedState(ElevatedState.NONE);
                }
                else if(scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
                    tcpClient.setBlueNearCubes(0);
                    tcpClient.setBlueNearStars(0);
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
                if(scorerLocation == ScorerLocation.RED_FAR_ZONE) {
                    ((TextView)rootView.findViewById(R.id.text_cubes)).setText("" + tcpClient.redFarCubes);
                    ((TextView)rootView.findViewById(R.id.text_stars)).setText("" + tcpClient.redFarStars);
                    ((SeekBar)rootView.findViewById(R.id.seekbar_elevation)).setProgress(tcpClient.redElevation.getValue());
                }
                else if(scorerLocation == ScorerLocation.BLUE_FAR_ZONE) {
                    ((TextView)rootView.findViewById(R.id.text_cubes)).setText("" + tcpClient.blueFarCubes);
                    ((TextView)rootView.findViewById(R.id.text_stars)).setText("" + tcpClient.blueFarStars);
                    ((SeekBar)rootView.findViewById(R.id.seekbar_elevation)).setProgress(tcpClient.blueElevation.getValue());
                }
                else if(scorerLocation == ScorerLocation.RED_NEAR_ZONE) {
                    ((TextView)rootView.findViewById(R.id.text_cubes)).setText("" + tcpClient.redNearCubes);
                    ((TextView)rootView.findViewById(R.id.text_stars)).setText("" + tcpClient.redNearStars);
                }
                else if(scorerLocation == ScorerLocation.BLUE_NEAR_ZONE) {
                    ((TextView)rootView.findViewById(R.id.text_cubes)).setText("" + tcpClient.blueNearCubes);
                    ((TextView)rootView.findViewById(R.id.text_stars)).setText("" + tcpClient.blueNearStars);
                }
            }
        });
    }
}
