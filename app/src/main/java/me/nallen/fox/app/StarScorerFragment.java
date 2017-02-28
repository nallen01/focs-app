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

public class StarScorerFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;
    private boolean isFlipped = false;

    public static StarScorerFragment newInstance(ScorerLocation scorerLocation, boolean flip) {
        StarScorerFragment fragment = new StarScorerFragment();
        fragment.assignFlipped(flip);
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public StarScorerFragment() {
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
            rootView = inflater.inflate(R.layout.fragment_star_scorer_flip, container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_star_scorer, container, false);
        }

        rootView.findViewById(R.id.button_add_near_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_STARS) {
                    tcpClient.addRedNearStar();
                }
                else {
                    tcpClient.addBlueNearStar();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_add_far_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_STARS) {
                    tcpClient.addRedFarStar();
                }
                else {
                    tcpClient.addBlueFarStar();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_remove_near_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_STARS) {
                    if(tcpClient.redNearStars > 0)
                        tcpClient.removeRedNearStar();
                }
                else {
                    if(tcpClient.blueNearStars > 0)
                        tcpClient.removeBlueNearStar();
                }
                updateUI();
            }
        });

        rootView.findViewById(R.id.button_remove_far_star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scorerLocation == ScorerLocation.RED_STARS) {
                    if(tcpClient.redFarStars > 0)
                        tcpClient.removeRedFarStar();
                }
                else {
                    if(tcpClient.blueFarStars > 0)
                        tcpClient.removeBlueFarStar();
                }
                updateUI();
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
                if(scorerLocation == ScorerLocation.RED_STARS) {
                    tcpClient.setRedFarStars(7);
                    tcpClient.setRedNearStars(0);
                }
                else {
                    tcpClient.setBlueFarStars(7);
                    tcpClient.setBlueNearStars(0);
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
                if(scorerLocation == ScorerLocation.RED_STARS) {
                    ((TextView)rootView.findViewById(R.id.text_far_stars)).setText("" + tcpClient.redFarStars);
                    ((TextView)rootView.findViewById(R.id.text_near_stars)).setText("" + tcpClient.redNearStars);
                }
                else {
                    ((TextView)rootView.findViewById(R.id.text_far_stars)).setText("" + tcpClient.blueFarStars);
                    ((TextView)rootView.findViewById(R.id.text_near_stars)).setText("" + tcpClient.blueNearStars);
                }
            }
        });
    }
}
