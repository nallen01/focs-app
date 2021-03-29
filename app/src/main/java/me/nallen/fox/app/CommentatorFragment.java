package me.nallen.fox.app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CommentatorFragment extends Fragment implements DataListener {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;
    private View rootView;
    private boolean showAuton = false;

    public static CommentatorFragment newInstance(ScorerLocation scorerLocation) {
        CommentatorFragment fragment = new CommentatorFragment();
        fragment.assignScorerLocation(scorerLocation);
        return fragment;
    }

    public CommentatorFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_commentator, container, false);

        if(scorerLocation != ScorerLocation.COMMENTATOR_AUTOMATION) {
            rootView.findViewById(R.id.automation_section).setVisibility(View.GONE);
        }

        rootView.findViewById(R.id.button_red_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setAutonWinner(AutonWinner.RED);

                updateUI();
            }
        });

        rootView.findViewById(R.id.button_blue_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setAutonWinner(AutonWinner.BLUE);

                updateUI();
            }
        });

        rootView.findViewById(R.id.button_tie_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setAutonWinner(AutonWinner.TIE);

                updateUI();
            }
        });

        rootView.findViewById(R.id.button_no_auton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setAutonWinner(AutonWinner.NONE);

                updateUI();
            }
        });

        rootView.findViewById(R.id.button_corner_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHistoryMethod(HistoryMethod.CORNER);
            }
        });

        rootView.findViewById(R.id.button_side_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHistoryMethod(HistoryMethod.SIDE);
            }
        });

        rootView.findViewById(R.id.button_full_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHistoryMethod(HistoryMethod.FULL);
            }
        });

        rootView.findViewById(R.id.button_hide_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHistoryMethod(HistoryMethod.NONE);
            }
        });

        rootView.findViewById(R.id.button_hide_focs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHideFox(true);
            }
        });

        rootView.findViewById(R.id.button_show_focs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHideFox(false);
            }
        });

        rootView.findViewById(R.id.button_clear_scores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.clearAllScores();
            }
        });

        rootView.findViewById(R.id.button_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setPaused(true);
            }
        });

        rootView.findViewById(R.id.button_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setPaused(false);
            }
        });

        rootView.findViewById(R.id.button_division_science).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setFoxDisplay(FoxDisplay.SCIENCE);
            }
        });

        rootView.findViewById(R.id.button_division_tech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setFoxDisplay(FoxDisplay.TECHNOLOGY);
            }
        });

        rootView.findViewById(R.id.button_division_none).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setFoxDisplay(FoxDisplay.NONE);
            }
        });

        updateUI();

        updateAutonDisplay();

        return rootView;
    }

    private void updateAutonDisplay() {
        View autonView = rootView.findViewById(R.id.auton_section);

        if(showAuton) {
            autonView.setVisibility(View.VISIBLE);
        }
        else {
            autonView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.commentator, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.incl_auton:
                if(item.isChecked()) {
                    item.setChecked(false);
                    showAuton = false;
                }
                else {
                    item.setChecked(true);
                    showAuton = true;
                }

                updateAutonDisplay();

                return true;
            case R.id.three_team:
                if(item.isChecked()) {
                    item.setChecked(false);
                    tcpClient.setThreeTeam(false);
                }
                else {
                    item.setChecked(true);
                    tcpClient.setThreeTeam(true);
                }
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
                if(tcpClient.autonWinner == AutonWinner.RED) {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("[[Red]]");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("Blue");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("None");
                    ((Button)rootView.findViewById(R.id.button_tie_auton)).setText("Tied");
                }
                else if(tcpClient.autonWinner == AutonWinner.BLUE) {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("Red");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("[[Blue]]");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("None");
                    ((Button)rootView.findViewById(R.id.button_tie_auton)).setText("Tied");
                }
                else if(tcpClient.autonWinner == AutonWinner.NONE) {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("Red");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("Blue");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("[[None]]");
                    ((Button)rootView.findViewById(R.id.button_tie_auton)).setText("Tied");
                }
                else {
                    ((Button)rootView.findViewById(R.id.button_red_auton)).setText("Red");
                    ((Button)rootView.findViewById(R.id.button_blue_auton)).setText("Blue");
                    ((Button)rootView.findViewById(R.id.button_no_auton)).setText("None");
                    ((Button)rootView.findViewById(R.id.button_tie_auton)).setText("[[Tied]]");
                }
            }
        });
    }
}
