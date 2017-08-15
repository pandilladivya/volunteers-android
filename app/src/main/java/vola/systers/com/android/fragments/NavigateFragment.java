package vola.systers.com.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vola.systers.com.android.R;

public class NavigateFragment extends Fragment {

    public NavigateFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO : NAVIGATE FRAGMENT

        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        return rootView;
    }


}
