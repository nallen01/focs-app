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
        return inflater.inflate(R.layout.fragment_commentator, container, false);
    }
}
