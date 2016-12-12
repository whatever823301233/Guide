package com.systek.guide.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.systek.guide.R;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.iView.IChannelChooseView;
import com.systek.guide.presenter.ChannelPresenter;
import com.systek.guide.ui.BaseFragment;
import com.systek.guide.ui.widget.channel.ChannelItem;
import com.systek.guide.ui.widget.channel.DragAdapter;
import com.systek.guide.ui.widget.channel.DragGrid;
import com.systek.guide.ui.widget.channel.OtherAdapter;
import com.systek.guide.ui.widget.channel.OtherGridView;

import java.util.ArrayList;
import java.util.List;

public class ChannelFragment extends BaseFragment implements IChannelChooseView {
    private static final String ARG_PARAM1 = "param1";

    private String museumId;

    private OnFragmentInteractionListener mListener;

    /** 用户栏目的 GRID VIEW */
    private DragGrid userGridView;
    /** 其它栏目的 GRID VIEW */
    private OtherGridView otherGridView;
    /** 用户栏目对应的适配器，可以拖动 */
    private DragAdapter userAdapter;
    /** 其它栏目对应的适配器 */
    private OtherAdapter otherAdapter;
    /** 其它栏目列表 */
    private ArrayList<ChannelItem> otherChannelList ;
    /** 用户栏目列表 */
    private ArrayList<ChannelItem> userChannelList;
    /** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
    boolean isMove = false;
    private ChannelPresenter presenter;



    public ChannelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChannelFragment.
     */
    public static ChannelFragment newInstance(String param1) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChannelPresenter(this);
        if (getArguments() != null) {
            museumId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.initChannels();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.saveLabels();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        userGridView = (DragGrid) findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
        userAdapter = new DragAdapter(getHoldingActivity());
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(getHoldingActivity());
        otherGridView.setAdapter(otherAdapter);

        //设置GRID_VIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(itemClickListener);
        userGridView.setOnItemClickListener(itemClickListener);

    }


    private AdapterView.OnItemClickListener itemClickListener =  new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            presenter.onItemClick(parent,view,position,id);
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_channel;
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
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toNextActivity(Intent intent) {

    }

    @Override
    public boolean isMove() {
        return isMove;
    }

    @Override
    public void hideUserAddOtherItem(AdapterView<?> parent, View view,final int position, long id) {

        final ImageView moveImageView = getView(view);
        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
        final int[] startLocation = new int[2];
        newTextView.getLocationInWindow(startLocation);
        final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
        otherAdapter.setVisible(false);
        //添加到最后一个
        otherAdapter.addItem(channel);

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    int[] endLocation = new int[2];
                    //获取终点的坐标
                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                    moveAnim(moveImageView, startLocation , endLocation,userGridView);
                    userAdapter.setRemove(position);
                } catch (Exception localException) {
                    LogUtil.e("",localException);
                }
            }
        }, 50L);
    }

    /**
     * 获取点击的Item的对应View，
     * @param view
     * @return
     */
    public ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(getHoldingActivity());
        iv.setImageBitmap(cache);
        return iv;
    }
    /**
     * 点击ITEM移动动画
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param clickGridView
     */
    private void moveAnim(View moveView, int[] startLocation, int[] endLocation, final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                }else{
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }


    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }



    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getHoldingActivity().getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(getHoldingActivity());
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }



    @Override
    public void hideOtherAddUserItem(AdapterView<?> parent, View view,final int position, long id) {
        final ImageView moveImageView = getView(view);
        if (moveImageView != null){
            TextView newTextView = (TextView) view.findViewById(R.id.text_item);
            final int[] startLocation = new int[2];
            newTextView.getLocationInWindow(startLocation);
            final ChannelItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
            userAdapter.setVisible(false);
            //添加到最后一个
            userAdapter.addItem(channel);
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        int[] endLocation = new int[2];
                        //获取终点的坐标
                        userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                        moveAnim(moveImageView, startLocation , endLocation,otherGridView);
                        otherAdapter.setRemove(position);
                    } catch (Exception localException) {
                        LogUtil.e("",localException);
                    }
                }
            }, 50L);
        }
    }

    @Override
    public void setUserChannel(ArrayList<ChannelItem> userChannelList) {
        this.userChannelList = userChannelList;
    }

    @Override
    public void setOtherChannel(ArrayList<ChannelItem> otherChannelList) {
        this.otherChannelList = otherChannelList;
    }

    @Override
    public void updateUserChannel() {
        if(userAdapter != null){
            userAdapter.updateData(userChannelList);
        }
    }

    @Override
    public void updateOtherChannel() {
        if(otherAdapter != null){
            otherAdapter.updateData(otherChannelList);
        }
    }

    @Override
    public List<ChannelItem> getUserChannelList() {
        return userChannelList;
    }

    @Override
    public String getMuseumId() {
        return museumId;
    }

    @Override
    public void showFailedError() {

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
