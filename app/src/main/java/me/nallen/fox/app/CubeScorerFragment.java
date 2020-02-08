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

public class CubeScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

    public static CubeScorerFragment newInstance(ScorerLocation scorerLocation) {
        CubeScorerFragment fragment = new CubeScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public CubeScorerFragment() {
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

    private View.OnClickListener cubeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.red_orange_cubes_add: tcpClient.addRedOrangeCube(); break;
                case R.id.red_orange_cubes_remove: tcpClient.removeRedOrangeCube(); break;
                case R.id.red_green_cubes_add: tcpClient.addRedGreenCube(); break;
                case R.id.red_green_cubes_remove: tcpClient.removeRedGreenCube(); break;
                case R.id.red_purple_cubes_add: tcpClient.addRedPurpleCube(); break;
                case R.id.red_purple_cubes_remove: tcpClient.removeRedPurpleCube(); break;
                case R.id.blue_orange_cubes_add: tcpClient.addBlueOrangeCube(); break;
                case R.id.blue_orange_cubes_remove: tcpClient.removeBlueOrangeCube(); break;
                case R.id.blue_green_cubes_add: tcpClient.addBlueGreenCube(); break;
                case R.id.blue_green_cubes_remove: tcpClient.removeBlueGreenCube(); break;
                case R.id.blue_purple_cubes_add: tcpClient.addBluePurpleCube(); break;
                case R.id.blue_purple_cubes_remove: tcpClient.removeBluePurpleCube(); break;
            }

            updateUI();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cubes_scorer, container, false);

        rootView.findViewById(R.id.red_orange_cubes_add).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.red_orange_cubes_remove).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.red_green_cubes_add).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.red_green_cubes_remove).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.red_purple_cubes_add).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.red_purple_cubes_remove).setOnClickListener(cubeListener);

        rootView.findViewById(R.id.blue_orange_cubes_add).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.blue_orange_cubes_remove).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.blue_green_cubes_add).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.blue_green_cubes_remove).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.blue_purple_cubes_add).setOnClickListener(cubeListener);
        rootView.findViewById(R.id.blue_purple_cubes_remove).setOnClickListener(cubeListener);

        if(scorerLocation == ScorerLocation.CUBES_RED) {
            rootView.findViewById(R.id.blue_label).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.blue_orange_cubes_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_orange_cubes_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_orange_cubes).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.blue_green_cubes_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_green_cubes_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_green_cubes).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.blue_purple_cubes_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_purple_cubes_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.blue_purple_cubes).setVisibility(View.INVISIBLE);
        }
        else if(scorerLocation == ScorerLocation.CUBES_BLUE) {
            rootView.findViewById(R.id.red_label).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.red_orange_cubes_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_orange_cubes_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_orange_cubes).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.red_green_cubes_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_green_cubes_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_green_cubes).setVisibility(View.INVISIBLE);

            rootView.findViewById(R.id.red_purple_cubes_add).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_purple_cubes_remove).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.red_purple_cubes).setVisibility(View.INVISIBLE);
        }

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
                tcpClient.setRedOrangeCubes(0);
                tcpClient.setRedGreenCubes(0);
                tcpClient.setRedPurpleCubes(0);
                tcpClient.setBlueOrangeCubes(0);
                tcpClient.setBlueGreenCubes(0);
                tcpClient.setBluePurpleCubes(0);

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
                ((TextView)rootView.findViewById(R.id.red_orange_cubes)).setText("" + tcpClient.redOrangeCubes);
                ((TextView)rootView.findViewById(R.id.red_green_cubes)).setText("" + tcpClient.redGreenCubes);
                ((TextView)rootView.findViewById(R.id.red_purple_cubes)).setText("" + tcpClient.redPurpleCubes);
                ((TextView)rootView.findViewById(R.id.blue_orange_cubes)).setText("" + tcpClient.blueOrangeCubes);
                ((TextView)rootView.findViewById(R.id.blue_green_cubes)).setText("" + tcpClient.blueGreenCubes);
                ((TextView)rootView.findViewById(R.id.blue_purple_cubes)).setText("" + tcpClient.bluePurpleCubes);
            }
        });
    }
}
