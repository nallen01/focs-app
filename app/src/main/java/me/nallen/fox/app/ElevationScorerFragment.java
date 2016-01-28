package me.nallen.fox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ElevationScorerFragment extends Fragment {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;

    public static ElevationScorerFragment newInstance(ScorerLocation scorerLocation) {
        ElevationScorerFragment fragment = new ElevationScorerFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public ElevationScorerFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_elevation_scorer, container, false);

        rootView.findViewById(R.id.button_red_high_elevation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setRedElevatedState(ElevatedState.HIGH);
            }
        });

        rootView.findViewById(R.id.button_red_low_elevation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setRedElevatedState(ElevatedState.LOW);
            }
        });

        rootView.findViewById(R.id.button_red_no_elevation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setRedElevatedState(ElevatedState.NONE);
            }
        });

        rootView.findViewById(R.id.button_blue_high_elevation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setBlueElevatedState(ElevatedState.HIGH);
            }
        });

        rootView.findViewById(R.id.button_blue_low_elevation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setBlueElevatedState(ElevatedState.LOW);
            }
        });

        rootView.findViewById(R.id.button_blue_no_elevation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setBlueElevatedState(ElevatedState.NONE);
            }
        });

        return rootView;
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
                tcpClient.setRedElevatedState(ElevatedState.NONE);
                tcpClient.setBlueElevatedState(ElevatedState.NONE);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
