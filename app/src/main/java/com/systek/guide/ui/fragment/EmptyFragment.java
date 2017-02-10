package com.systek.guide.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.systek.guide.R;
import com.systek.guide.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmptyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmptyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmptyFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EmptyFragment() {
        // Required empty public constructor
    }

    public static EmptyFragment newInstance() {
        EmptyFragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getTag(),"onAttach");
        if (getArguments() != null) {
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(getTag(),"onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getTag(),"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getTag(),"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(getTag(),"onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getTag(),"onDestroy");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(getTag(),"onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_empty;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
