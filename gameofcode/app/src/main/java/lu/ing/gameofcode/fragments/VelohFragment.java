package lu.ing.gameofcode.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import lu.ing.gameofcode.R;

public class VelohFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_veloh, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
