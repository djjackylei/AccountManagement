package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swjtu.huxin.accountmanagement.R;

public class TuBiaoFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null)
            mArgument = bundle.getString(ARGUMENT);

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tubiao,container,false);
        return view;
    }

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static TuBiaoFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        TuBiaoFragment contentFragment = new TuBiaoFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }
}