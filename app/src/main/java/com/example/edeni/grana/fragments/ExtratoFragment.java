package com.example.edeni.grana.fragments;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.edeni.grana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExtratoFragment extends Fragment {

    private static String TITULO = "Extrato";

    public ExtratoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(TITULO);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extrato, container, false);
    }

}
