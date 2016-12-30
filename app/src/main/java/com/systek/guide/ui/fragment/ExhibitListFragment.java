package com.systek.guide.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.systek.guide.R;
import com.systek.guide.ui.adapter.ExhibitAdapter;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.ui.iView.IExhibitListView;
import com.systek.guide.presenter.NearExhibitPresenter;
import com.systek.guide.ui.BaseFragment;
import com.systek.guide.ui.widget.recyclerView.QRecyclerView;

import java.util.List;

public class ExhibitListFragment extends BaseFragment implements IExhibitListView {


    private static final String ARG_PARAM1 = "param1";

    private String museumId;
    private OnFragmentInteractionListener mListener;
    private QRecyclerView recyclerView;
    private ExhibitAdapter adapter;
    private List<Exhibit> nearExhibitList;
    private Exhibit chooseExhibit;
    private NearExhibitPresenter presenter;


    public ExhibitListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param museumId Parameter 1.
     * @return A new instance of fragment ExhibitListFragment.
     */
    public static ExhibitListFragment newInstance(String museumId) {
        ExhibitListFragment fragment = new ExhibitListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, museumId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            museumId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        presenter = new NearExhibitPresenter(this);

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
        return R.layout.fragment_exhibit_list;
    }



    @Override
    public void setNearExhibitTitle() {
        mListener.setNearExhibitTitle();
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
        void setNearExhibitTitle();
    }
}
