package vola.systers.com.volunteers_android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vola.systers.com.volunteers_android.R;

public class ScheduleFragment extends Fragment {

    public ScheduleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO : SCHEDULE FRAGMENT

        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        return rootView;
    }

}
