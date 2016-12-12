package com.systek.guide.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.systek.guide.R;
import com.systek.guide.adapter.BaseRecyclerAdapter;
import com.systek.guide.adapter.ExhibitAdapter;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.iView.ICollectionView;
import com.systek.guide.presenter.CollectionPresenter;
import com.systek.guide.service.MediaIDHelper;
import com.systek.guide.ui.BaseFragment;
import com.systek.guide.ui.widget.recyclerView.QRecyclerView;

import java.util.List;

public class CollectionFragment extends BaseFragment implements ICollectionView{

    private static final String ARG_PARAM1 = "param1";
    private String museumId;
    private QRecyclerView qRecyclerView;
    private ExhibitAdapter exhibitAdapter;
    private CollectionPresenter presenter;
    private List<Exhibit> favoriteExhibitList;
    private OnFragmentInteractionListener mListener;
    private Exhibit chooseExhibit;
    private String mMediaId;

    public CollectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CollectionFragment.
     */
    public static CollectionFragment newInstance(String param1) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CollectionPresenter(this);

        if (getArguments() != null) {
            museumId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onViewCreated();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        qRecyclerView  = (QRecyclerView) findViewById(R.id.qRecyclerView);
        //设置上拉刷新文字颜色
        assert qRecyclerView != null;
        qRecyclerView.setFooterViewTextColor(R.color.white_1000);
        //设置加载更多背景色
        qRecyclerView.setFooterViewBackgroundColor(R.color.colorAccent);
        qRecyclerView.setLinearLayout();
        qRecyclerView.setOnPullLoadMoreListener(pullLoadMoreListener);
        qRecyclerView.setEmptyView(LayoutInflater.from(getActivity()).inflate(R.layout.layout_recycler_empty_view, null));//setEmptyView
        exhibitAdapter = new ExhibitAdapter(getHoldingActivity());
        qRecyclerView.setAdapter(exhibitAdapter);
        exhibitAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                presenter.onExhibitChoose(exhibitAdapter.getExhibit(position));
            }
        });

    }


    private QRecyclerView.PullLoadMoreListener pullLoadMoreListener  = new QRecyclerView.PullLoadMoreListener() {
        @Override
        public void onRefresh() {

        }

        @Override
        public void onLoadMore() {

        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection;
    }

    @Override
    public void refreshView() {
        exhibitAdapter.updateData(favoriteExhibitList);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public Exhibit getChooseExhibit() {
        return chooseExhibit;
    }

    @Override
    public void setChooseExhibit(Exhibit exhibit) {
        this.chooseExhibit = exhibit;
    }

    @Override
    public void toNextActivity(Intent intent) {

    }

    @Override
    public void showFailedError() {

    }

    @Override
    public void onNoData() {

    }

    @Override
    public void hideErrorView() {

    }

    @Override
    public void showFavoriteExhibits() {
        if(exhibitAdapter !=null){
            exhibitAdapter.updateData(favoriteExhibitList);
        }
    }

    @Override
    public String getMuseumId() {
        return museumId;
    }

    @Override
    public void setFavoriteExhibitList(List<Exhibit> exhibitList) {
        this.favoriteExhibitList = exhibitList;
    }

    @Override
    public void toPlay() {
        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                getChooseExhibit().getId(),
                MediaIDHelper.MEDIA_ID_MUSEUM_ID,
                museumId);
        MediaControllerCompat.TransportControls controls =  getSupportMediaController().getTransportControls();
        controls.playFromMediaId(hierarchyAwareMediaID,null);
    }

    @Override
    public void setTitle(String title) {
        mListener.setTitle(title);
    }

    /**
     * 获得 媒体播放控制器
     * @return 媒体播放控制器对象
     */
    private MediaControllerCompat getSupportMediaController() {
        return mListener.getSupportMediaController();
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

        /**
         * 获得 媒体播放控制器
         * @return 媒体播放控制器对象
         */
        MediaControllerCompat getSupportMediaController();

        /**
         * 设置标题
         * @param title 标题
         */
        void setTitle(String title);
    }
}
