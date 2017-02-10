package com.systek.guide.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.systek.guide.R;
import com.systek.guide.adapter.MuseumIconAdapter;
import com.systek.guide.bean.Museum;
import com.systek.guide.iView.IMuseumHomeView;
import com.systek.guide.presenter.MuseumHomePresenter;
import com.systek.guide.base.BaseFragment;
import com.systek.guide.ui.activity.GuideActivity;

import java.util.List;


public class MuseumHomeFragment extends BaseFragment implements IMuseumHomeView {

    private static final String ARG_PARAM1 = "param1";

    private Museum currentMuseum;

    private OnMuseumHomeFragmentInteractionListener mListener;
    private MuseumHomePresenter presenter;
    private ImageView ivPlayStateCtrl;
    private ProgressBar loadingProgress;
    private TextView tvMuseumIntroduce;
    private RelativeLayout rlGuideHome;
    private RelativeLayout rlMapHome;
    private RelativeLayout rlTopicHome;
    private RelativeLayout rlCollectionHome;
    private RelativeLayout rlNearlyHome;
    private MuseumIconAdapter iconAdapter;
    private List<String> iconUrls;
    private View onClickView;


    public MuseumHomeFragment() {
        // Required empty public constructor
    }

    public static MuseumHomeFragment newInstance(Museum museum) {
        MuseumHomeFragment fragment = new MuseumHomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, museum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MuseumHomePresenter(this);
        if (getArguments() != null) {
            currentMuseum = (Museum) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onViewCreated();
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        ivPlayStateCtrl = (ImageView) findViewById(R.id.ivPlayStateCtrl);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        //llMuseumLargestIcon = (LinearLayout) findViewById(R.id.llMuseumLargestIcon);
        tvMuseumIntroduce = (TextView) findViewById(R.id.tvMuseumIntroduce);
        rlGuideHome = (RelativeLayout) findViewById(R.id.rlGuideHome);
        rlMapHome = (RelativeLayout) findViewById(R.id.rlMapHome);
        rlTopicHome = (RelativeLayout) findViewById(R.id.rlTopicHome);
        rlCollectionHome = (RelativeLayout) findViewById(R.id.rlCollectionHome);
        rlNearlyHome = (RelativeLayout) findViewById(R.id.rlNearlyHome);

        RecyclerView recycleViewMuseumIcon = (RecyclerView) findViewById(R.id.recycleViewMuseumIcon);

         /*设置为横向*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getHoldingActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (recycleViewMuseumIcon != null) {
            recycleViewMuseumIcon.setLayoutManager(linearLayoutManager);
            iconAdapter = new MuseumIconAdapter(getHoldingActivity());
            recycleViewMuseumIcon.setAdapter(iconAdapter);
            recycleViewMuseumIcon.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        }

        rlGuideHome.setOnClickListener(onClickListener);
        rlMapHome.setOnClickListener(onClickListener);
        rlTopicHome.setOnClickListener(onClickListener);
        ivPlayStateCtrl.setOnClickListener(onClickListener);
        rlCollectionHome.setOnClickListener(onClickListener);
        //rlNearlyHome.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setOnClickView(v);
            presenter.onImageButtonClick();
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_museum_home;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMuseumHomeFragmentInteractionListener) {
            mListener = (OnMuseumHomeFragmentInteractionListener) context;
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
    public void refreshView() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toNextActivity(Intent intent) {

    }

    @Override
    public void setCurrentMuseum(Museum museum) {

    }

    @Override
    public Museum getCurrentMuseum() {
        return currentMuseum;
    }

    @Override
    public void setTitle(String title) {
        mListener.setTitle(title);
    }


    @Override
    public void refreshIntroduce(String introduce) {
        if(TextUtils.isEmpty(introduce)){return;}
        tvMuseumIntroduce.setText(introduce);
    }

    @Override
    public void setIconUrls(List<String> iconUrls) {
        this.iconUrls = iconUrls;
    }

    @Override
    public void refreshIcons() {
        if(iconUrls == null || iconAdapter == null){return;}
        iconAdapter.updateData(iconUrls);
    }

    @Override
    public void refreshMedia() {
        // TODO: 2016/12/2  
    }

    @Override
    public void showFailedError() {

    }

    @Override
    public void setAdapterMuseumId(String museumId) {
        if(iconAdapter!=null){
            iconAdapter.setMuseumId(museumId);
        }
    }

    @Override
    public void setMediaPath(String s) {

    }

    @Override
    public void setOnClickView(View view) {
        onClickView = view;
    }

    @Override
    public View getOnClickView() {
        return onClickView;
    }

    @Override
    public boolean isDrawerOpen() {
        return mListener.isDrawerOpen();
    }

    @Override
    public void closeDrawer() {
        mListener.closeDrawer();
    }

    @Override
    public void addShowFragment(BaseFragment fragment) {
        mListener.showFragment(fragment);
    }

    @Override
    public void addShowFragment(String tag) {
        mListener.showFragment(tag);
    }

    @Override
    public void showGuideActivity(String simpleName) {
        Intent intent = new Intent(getHoldingActivity(), GuideActivity.class);
        intent.putExtra(GuideActivity.INTENT_FRAGMENT_FLAG,simpleName);
        intent.putExtra(GuideActivity.INTENT_MUSEUM_ID,currentMuseum.getId());
        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
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
    public interface OnMuseumHomeFragmentInteractionListener {

        void setTitle(String title);

        void showFragment(BaseFragment fragment);

        void showFragment(String tag);

        void closeDrawer();

        boolean isDrawerOpen();
    }
}
