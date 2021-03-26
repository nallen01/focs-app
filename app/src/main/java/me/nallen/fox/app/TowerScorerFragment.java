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
import android.widget.Button;
import android.widget.SeekBar;

public class TowerScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;

    public static TowerScorerFragment newInstance(ScorerLocation scorerLocation) {
        TowerScorerFragment fragment = new TowerScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public TowerScorerFragment() {
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

    private Button.OnClickListener towerListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = 0;
            CubeType type = CubeType.NONE;
            switch(view.getId()) {
                case R.id.tower_cube_1_none: pos = 0; type = CubeType.NONE; break;
                case R.id.tower_cube_1_orange: pos = 0; type = CubeType.ORANGE; break;
                case R.id.tower_cube_1_green: pos = 0; type = CubeType.GREEN; break;
                case R.id.tower_cube_1_purple: pos = 0; type = CubeType.PURPLE; break;
                case R.id.tower_cube_2_none: pos = 1; type = CubeType.NONE; break;
                case R.id.tower_cube_2_orange: pos = 1; type = CubeType.ORANGE; break;
                case R.id.tower_cube_2_green: pos = 1; type = CubeType.GREEN; break;
                case R.id.tower_cube_2_purple: pos = 1; type = CubeType.PURPLE; break;
                case R.id.tower_cube_3_none: pos = 2; type = CubeType.NONE; break;
                case R.id.tower_cube_3_orange: pos = 2; type = CubeType.ORANGE; break;
                case R.id.tower_cube_3_green: pos = 2; type = CubeType.GREEN; break;
                case R.id.tower_cube_3_purple: pos = 2; type = CubeType.PURPLE; break;
                case R.id.tower_cube_4_none: pos = 3; type = CubeType.NONE; break;
                case R.id.tower_cube_4_orange: pos = 3; type = CubeType.ORANGE; break;
                case R.id.tower_cube_4_green: pos = 3; type = CubeType.GREEN; break;
                case R.id.tower_cube_4_purple: pos = 3; type = CubeType.PURPLE; break;
                case R.id.tower_cube_5_none: pos = 4; type = CubeType.NONE; break;
                case R.id.tower_cube_5_orange: pos = 4; type = CubeType.ORANGE; break;
                case R.id.tower_cube_5_green: pos = 4; type = CubeType.GREEN; break;
                case R.id.tower_cube_5_purple: pos = 4; type = CubeType.PURPLE; break;
                case R.id.tower_cube_6_none: pos = 5; type = CubeType.NONE; break;
                case R.id.tower_cube_6_orange: pos = 5; type = CubeType.ORANGE; break;
                case R.id.tower_cube_6_green: pos = 5; type = CubeType.GREEN; break;
                case R.id.tower_cube_6_purple: pos = 5; type = CubeType.PURPLE; break;
                case R.id.tower_cube_7_none: pos = 6; type = CubeType.NONE; break;
                case R.id.tower_cube_7_orange: pos = 6; type = CubeType.ORANGE; break;
                case R.id.tower_cube_7_green: pos = 6; type = CubeType.GREEN; break;
                case R.id.tower_cube_7_purple: pos = 6; type = CubeType.PURPLE; break;
            }

            tcpClient.setTowerCube(pos, type);

            switch(pos) {
                case 0: rootView.findViewById(R.id.tower_cube_1).setBackgroundColor(type.getColor()); break;
                case 1: rootView.findViewById(R.id.tower_cube_2).setBackgroundColor(type.getColor()); break;
                case 2: rootView.findViewById(R.id.tower_cube_3).setBackgroundColor(type.getColor()); break;
                case 3: rootView.findViewById(R.id.tower_cube_4).setBackgroundColor(type.getColor()); break;
                case 4: rootView.findViewById(R.id.tower_cube_5).setBackgroundColor(type.getColor()); break;
                case 5: rootView.findViewById(R.id.tower_cube_6).setBackgroundColor(type.getColor()); break;
                case 6: rootView.findViewById(R.id.tower_cube_7).setBackgroundColor(type.getColor()); break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tower_scorer, container, false);

        rootView.findViewById(R.id.tower_cube_1_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_1_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_1_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_1_purple).setOnClickListener(towerListener);

        rootView.findViewById(R.id.tower_cube_2_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_2_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_2_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_2_purple).setOnClickListener(towerListener);

        rootView.findViewById(R.id.tower_cube_3_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_3_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_3_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_3_purple).setOnClickListener(towerListener);

        rootView.findViewById(R.id.tower_cube_4_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_4_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_4_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_4_purple).setOnClickListener(towerListener);

        rootView.findViewById(R.id.tower_cube_5_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_5_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_5_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_5_purple).setOnClickListener(towerListener);

        rootView.findViewById(R.id.tower_cube_6_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_6_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_6_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_6_purple).setOnClickListener(towerListener);

        rootView.findViewById(R.id.tower_cube_7_none).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_7_orange).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_7_green).setOnClickListener(towerListener);
        rootView.findViewById(R.id.tower_cube_7_purple).setOnClickListener(towerListener);

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
                tcpClient.setTowerCube(0, CubeType.NONE);
                tcpClient.setTowerCube(1, CubeType.NONE);
                tcpClient.setTowerCube(2, CubeType.NONE);
                tcpClient.setTowerCube(3, CubeType.NONE);
                tcpClient.setTowerCube(4, CubeType.NONE);
                tcpClient.setTowerCube(5, CubeType.NONE);
                tcpClient.setTowerCube(6, CubeType.NONE);

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
                rootView.findViewById(R.id.tower_cube_1).setBackgroundColor(tcpClient.towerCubes[0].getColor());
                rootView.findViewById(R.id.tower_cube_2).setBackgroundColor(tcpClient.towerCubes[1].getColor());
                rootView.findViewById(R.id.tower_cube_3).setBackgroundColor(tcpClient.towerCubes[2].getColor());
                rootView.findViewById(R.id.tower_cube_4).setBackgroundColor(tcpClient.towerCubes[3].getColor());
                rootView.findViewById(R.id.tower_cube_5).setBackgroundColor(tcpClient.towerCubes[4].getColor());
                rootView.findViewById(R.id.tower_cube_6).setBackgroundColor(tcpClient.towerCubes[5].getColor());
                rootView.findViewById(R.id.tower_cube_7).setBackgroundColor(tcpClient.towerCubes[6].getColor());
            }
        });
    }
}
