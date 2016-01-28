package me.nallen.fox.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommentatorFragment extends Fragment {
    private ScorerLocation scorerLocation;
    private TcpClient tcpClient;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_commentator, container, false);

        if(scorerLocation != ScorerLocation.COMMENTATOR_AUTOMATION) {
            rootView.findViewById(R.id.automation_section).setVisibility(View.GONE);
        }

        rootView.findViewById(R.id.button_small_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setLargeHistory(false);
                tcpClient.setHistoryVisible(true);
            }
        });

        rootView.findViewById(R.id.button_large_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setLargeHistory(true);
                tcpClient.setHistoryVisible(true);
            }
        });

        rootView.findViewById(R.id.button_hide_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.setHistoryVisible(false);
            }
        });

        rootView.findViewById(R.id.button_clear_scores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient.clearAllScores();
            }
        });

        return rootView;
    }
}
