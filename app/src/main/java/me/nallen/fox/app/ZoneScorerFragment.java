package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ZoneScorerFragment extends Fragment {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_zone_scorer, container, false);

        rootView.findViewById(R.id.button_far_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE)
                    tcpClient.addRedFarCube();
                else
                    tcpClient.addBlueFarCube();
            }
        });

        rootView.findViewById(R.id.button_far_near_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.removeRedFarStar();
                    tcpClient.addRedNearStar();
                }
                else {
                    tcpClient.removeBlueFarStar();
                    tcpClient.addBlueNearStar();
                }
            }
        });

        rootView.findViewById(R.id.button_far_near_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.removeRedFarCube();
                    tcpClient.addRedNearCube();
                }
                else {
                    tcpClient.removeBlueFarCube();
                    tcpClient.addBlueNearCube();
                }
            }
        });

        rootView.findViewById(R.id.button_near_far_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.removeRedNearStar();
                    tcpClient.addRedFarStar();
                }
                else {
                    tcpClient.removeBlueNearStar();
                    tcpClient.addBlueFarStar();
                }
            }
        });

        rootView.findViewById(R.id.button_near_far_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.removeRedNearCube();
                    tcpClient.addRedFarCube();
                }
                else {
                    tcpClient.removeBlueNearCube();
                    tcpClient.addBlueFarCube();
                }
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
            }
        });

        rootView.findViewById(R.id.button_near_fence_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.removeRedNearStar();
                }
                else {
                    tcpClient.removeBlueNearStar();
                }
            }
        });

        rootView.findViewById(R.id.button_near_fence_cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_ZONE) {
                    tcpClient.removeRedNearCube();
                }
                else {
                    tcpClient.removeBlueNearCube();
                }
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

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
