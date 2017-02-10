package com.systek.guide.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.systek.guide.R;
import com.systek.guide.base.BaseRecyclerAdapter;
import com.systek.guide.adapter.ExhibitAdapter;
import com.systek.guide.util.AndroidUtil;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.iView.ITopicView;
import com.systek.guide.presenter.TopicPresenter;
import com.systek.guide.util.MediaIDHelper;
import com.systek.guide.base.BaseFragment;
import com.systek.guide.widget.ColumnHorizontalScrollView;
import com.systek.guide.bean.ChannelItem;
import com.systek.guide.widget.recyclerView.QRecyclerView;

import java.util.List;

public class TopicFragment extends BaseFragment implements ITopicView {

    private static final String ARG_PARAM1 = "param1";
    private String museumId;
    private QRecyclerView qRecyclerView;
    private ExhibitAdapter exhibitAdapter;
    private TopicPresenter presenter;
    /** 自定义HorizontalScrollView */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    private LinearLayout mRadioGroup_content;
    private ImageView button_more_columns;
    private LinearLayout ll_more_columns;
    private RelativeLayout rl_column;
    /** 左阴影部分*/
    public ImageView shade_left;
    /** 右阴影部分 */
    public ImageView shade_right;
    /** 请求CODE */
    public final static int CHANNEL_REQUEST = 1;
    private int mScreenWidth;

    private int mItemWidth;
    private Exhibit chooseExhibit;
    private int columnSelectIndex;
    private ChannelItem chooseChannel;
    private List<Exhibit> allExhibitList;
    private List<ChannelItem> userChannelList;
    private OnFragmentInteractionListener mListener;

    public TopicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TopicFragment.
     */
    public static TopicFragment newInstance(String param1) {
        TopicFragment fragment = new TopicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new TopicPresenter(this);
        if (getArguments() != null) {
            museumId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onViewCreated();
        initTabColumn();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHANNEL_REQUEST){
            presenter.checkChannelList();
            initTabColumn();
        }
    }


    /**
     *  初始化Column栏目项
     * */
    private void initTabColumn() {

        mRadioGroup_content.removeAllViews();
        int count =  userChannelList.size();
        mColumnHorizontalScrollView.setParam(getHoldingActivity(), mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
        for(int i = 0; i< count; i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth , LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
//			TextView localTextView = (TextView) mInflater.inflate(R.layout.column_radio_item, null);
            TextView columnTextView = new TextView(getHoldingActivity());
            columnTextView.setTextAppearance(getHoldingActivity(), R.style.top_category_scroll_view_item_text);
//			localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
            columnTextView.setBackgroundResource(R.drawable.radio_button_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(userChannelList.get(i).getName());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
            if(columnSelectIndex == i){// TODO: 2016/8/12
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for(int i = 0;i < mRadioGroup_content.getChildCount();i++){
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else{
                            localView.setSelected(true);
                            //mViewPager.setCurrentItem(i);// TODO: 2016/8/12
                        }
                    }
                    setChooseChannel(userChannelList.get(v.getId()));
                    presenter.onChannelChoose();
                }
            });
            mRadioGroup_content.addView(columnTextView, i ,params);
        }
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
        mScreenWidth = AndroidUtil.getWindowsWidth(getHoldingActivity());
        mItemWidth = mScreenWidth / 7;// 一个Item宽度为屏幕的1/7
        mColumnHorizontalScrollView =  (ColumnHorizontalScrollView)findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);
        button_more_columns = (ImageView) findViewById(R.id.button_more_columns);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);

        qRecyclerView = (QRecyclerView) findViewById(R.id.qRecyclerView);
        //设置上拉刷新文字颜色
        assert qRecyclerView != null;
        qRecyclerView.setFooterViewTextColor(R.color.white_1000);
        //设置加载更多背景色
        qRecyclerView.setFooterViewBackgroundColor(R.color.colorAccent);
        qRecyclerView.setLinearLayout();
        qRecyclerView.setOnPullLoadMoreListener(pullLoadMoreListener);
        qRecyclerView.setEmptyView(LayoutInflater.from(getActivity()).inflate(R.layout.layout_recycler_empty_view, null));//setEmptyView
        qRecyclerView.setPullRefreshEnable(false);
        qRecyclerView.setSwipeRefreshEnable(false);
        qRecyclerView.setHasMore(false);
        exhibitAdapter = new ExhibitAdapter(getHoldingActivity());
        qRecyclerView.setAdapter(exhibitAdapter);


        exhibitAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Exhibit exhibit = exhibitAdapter.getExhibit(position);
                setChooseExhibit(exhibit);
                presenter.onExhibitChoose();
            }
        });


        button_more_columns.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ChannelFragment fragment = ChannelFragment.newInstance(museumId);
                mListener.showFragment(fragment);
                /*Intent intent_channel = new  Intent(getHoldingActivity(), TopicChooseActivity.class);
                intent_channel.putExtra(Constants.INTENT_MUSEUM_ID,museumId);
                startActivityForResult(intent_channel, CHANNEL_REQUEST);// TODO: 2016/12/2*/
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    private QRecyclerView.PullLoadMoreListener pullLoadMoreListener = new  QRecyclerView.PullLoadMoreListener(){
        @Override
        public void onRefresh() {}
        @Override
        public void onLoadMore() {}
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public String getMuseumId() {
        return museumId;
    }

    @Override
    public void refreshExhibitList() {
        exhibitAdapter.updateData(allExhibitList);
    }

    @Override
    public List<String> getChooseLabels() {
        return null;
    }

    @Override
    public void addChooseLabels(String label) {

    }

    @Override
    public void removeLabel() {

    }

    @Override
    public void removeLabels(List<String> labels) {

    }

    @Override
    public void toMapView(List<Exhibit> exhibits) {

    }

    @Override
    public void toNextActivity(Intent intent) {

    }

    @Override
    public void showFailedError() {

    }

    @Override
    public void hideErrorView() {

    }

    @Override
    public void showAllExhibits() {
        if(exhibitAdapter == null || allExhibitList == null ){return;}
        exhibitAdapter.updateData(allExhibitList);
    }

    @Override
    public void setAllExhibitList(List<Exhibit> exhibitList) {
        allExhibitList = exhibitList;
    }

    @Override
    public void setChooseExhibit(Exhibit exhibit) {
        this.chooseExhibit = exhibit;
    }

    @Override
    public Exhibit getChooseExhibit() {
        return chooseExhibit;
    }


    @Override
    public void toPlay() {
        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                getChooseExhibit().getId(),
                MediaIDHelper.MEDIA_ID_MUSEUM_ID,
                museumId);
        MediaControllerCompat.TransportControls controls = mListener.getControls();
        if(controls != null){
            controls.playFromMediaId(hierarchyAwareMediaID,null);
        }else{
            Toast.makeText(getHoldingActivity(),"MediaControllerCompat.TransportControls = null !",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNoData() {

    }

    @Override
    public void setUserChannelList(List<ChannelItem> channelItems) {
        this.userChannelList = channelItems;
    }

    @Override
    public List<ChannelItem> getUserChannelList() {
        return userChannelList;
    }

    @Override
    public void setChooseChannel(ChannelItem channel) {
        chooseChannel = channel;
    }

    @Override
    public ChannelItem getChooseChannel() {
        return chooseChannel;
    }

    @Override
    public void setTitle(String title) {
        mListener.setTitle(title);
    }

    @Override
    public List<Exhibit> getChooseAllExhibits() {
        return allExhibitList;
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
        void showFragment(BaseFragment fragment);
        MediaControllerCompat.TransportControls getControls();
        void setTitle(String title);
    }
}
