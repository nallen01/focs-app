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
    private boolean isFlipped = false;

    public static ZoneScorerFragment newInstance(ScorerLocation scorerLocation, boolean flip) {
        ZoneScorerFragment fragment = new ZoneScorerFragment();
        fragment.assignFlipped(flip);
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public ZoneScorerFragment() {
        tcpClient = TcpClient.getInstance();
    }

    public void assignScorerLocation(ScorerLocation scorerLocation) {
        this.scorerLocation = scorerLocation;
    }

    public void assignFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
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
            rootView = inflater.inflate(R.layout.fragment_zone_scorer_flip, container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_zone_scorer, container, false);
        }

        if(scorerLocation == ScorerLocation.BLUE_ZONE) {
            rootView.findViewById(R.id.divider).setBackgroundResource(R.color.vexBlue);
            rootView.findViewById(R.id.divider_two).setBackgroundResource(R.color.vexBlue);
        }
        else {
            rootView.findViewById(R.id.divider).setBackgroundResource(R.color.vexRed);
            rootView.findViewById(R.id.divider_two).setBackgroundResource(R.color.vexRed);
        }

        rootView.findViewById(R.id.button_far_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.addRedFarCube();
                }
                else {
                    tcpClient.addBlueFarCube();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_far_near_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    if(tcpClient.redFarStars > 0) {
                        tcpClient.removeRedFarStar();
                        tcpClient.addRedNearStar();
                    }
                }
                else {
                    if(tcpClient.blueFarStars > 0) {
                        tcpClient.removeBlueFarStar();
                        tcpClient.addBlueNearStar();
                    }
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_far_near_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    if(tcpClient.redFarCubes > 0) {
                        tcpClient.removeRedFarCube();
                        tcpClient.addRedNearCube();
                    }
                }
                else {
                    if(tcpClient.blueFarCubes > 0) {
                        tcpClient.removeBlueFarCube();
                        tcpClient.addBlueNearCube();
                    }
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_near_far_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    if(tcpClient.redNearStars > 0) {
                        tcpClient.removeRedNearStar();
                        tcpClient.addRedFarStar();
                    }
                }
                else {
                    if(tcpClient.blueNearStars > 0) {
                        tcpClient.removeBlueNearStar();
                        tcpClient.addBlueFarStar();
                    }
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_near_far_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    if(tcpClient.redNearCubes > 0) {
                        tcpClient.removeRedNearCube();
                        tcpClient.addRedFarCube();
                    }
                }
                else {
                    if(tcpClient.blueNearCubes > 0) {
                        tcpClient.removeBlueNearCube();
                        tcpClient.addBlueFarCube();
                    }
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_fence_near_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.addRedNearStar();
                }
                else {
                    tcpClient.addBlueNearStar();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_fence_near_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.addRedNearCube();
                }
                else {
                    tcpClient.addBlueNearCube();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_near_fence_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    if(tcpClient.redNearStars > 0) {
                        tcpClient.removeRedNearStar();
                    }
                }
                else {
                    if(tcpClient.blueNearStars > 0) {
                        tcpClient.removeBlueNearStar();
                    }
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_near_fence_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    if(tcpClient.redNearCubes > 0) {
                        tcpClient.removeRedNearCube();
                    }
                }
                else {
                    if(tcpClient.blueNearCubes > 0) {
                        tcpClient.removeBlueNearCube();
                    }
                }
                updateUI();
            }
        });

        ((SeekBar)rootView.findViewById(R.id.seekbar_elevation)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ElevatedState state = ElevatedState.fromInt(progress);

                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.setRedElevatedState(state);
                }
                else {
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
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.setRedFarCubes(0);
                    tcpClient.setRedFarStars(0);
                    tcpClient.setRedNearCubes(0);
                    tcpClient.setRedNearStars(0);
                    tcpClient.setRedElevatedState(ElevatedState.NONE);
                }
                else {
                    tcpClient.setBlueFarCubes(0);
                    tcpClient.setBlueFarStars(0);
                    tcpClient.setBlueNearCubes(0);
                    tcpClient.setBlueNearStars(0);
                    tcpClient.setBlueElevatedState(ElevatedState.NONE);
                }

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
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    ((TextView)rootView.findViewById(R.id.text_far_cubes)).setText("" + tcpClient.redFarCubes);
                    ((TextView)rootView.findViewById(R.id.text_far_stars)).setText("" + tcpClient.redFarStars);
                    ((TextView)rootView.findViewById(R.id.text_near_cubes)).setText("" + tcpClient.redNearCubes);
                    ((TextView)rootView.findViewById(R.id.text_near_stars)).setText("" + tcpClient.redNearStars);
                    ((SeekBar)rootView.findViewById(R.id.seekbar_elevation)).setProgress(tcpClient.redElevation.getValue());
                }
                else {
                    ((TextView)rootView.findViewById(R.id.text_far_cubes)).setText("" + tcpClient.blueFarCubes);
                    ((TextView)rootView.findViewById(R.id.text_far_stars)).setText("" + tcpClient.blueFarStars);
                    ((TextView)rootView.findViewById(R.id.text_near_cubes)).setText("" + tcpClient.blueNearCubes);
                    ((TextView)rootView.findViewById(R.id.text_near_stars)).setText("" + tcpClient.blueNearStars);
                    ((SeekBar)rootView.findViewById(R.id.seekbar_elevation)).setProgress(tcpClient.blueElevation.getValue());
                }
            }
        });
    }
}
