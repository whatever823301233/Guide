package com.systek.guide.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.systek.guide.R;
import com.systek.guide.ui.adapter.BaseRecyclerAdapter;
import com.systek.guide.ui.adapter.MuseumAdapter;
import com.systek.guide.bean.Museum;
import com.systek.guide.ui.iView.IMuseumChooseView;
import com.systek.guide.presenter.MuseumChoosePresenter;
import com.systek.guide.ui.BaseFragment;

import java.util.List;


/**
 *  博物馆列表 fragment
 */
public class MuseumListFragment extends BaseFragment implements IMuseumChooseView{

    private OnFragmentInteractionListener mListener;
    private static final String CITY_NAME = "city_name";
    private String cityName;
    private RecyclerView recyclerView;
    private String city;//当前所选城市
    private List<Museum> museumList;//博物馆列表
    private MuseumAdapter adapter;//适配器
    private TextView toolbarTitle;
    private MuseumChoosePresenter presenter;
    private ProgressDialog progressDialog;

    public MuseumListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cityName 城市
     * @return A new instance of fragment MuseumListFragment.
     */
    public static MuseumListFragment newInstance(String cityName) {
        MuseumListFragment fragment = new MuseumListFragment();
        Bundle args = new Bundle();
        args.putString(CITY_NAME, cityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MuseumChoosePresenter(this);
        if (getArguments() != null) {
            cityName = getArguments().getString(CITY_NAME);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        mListener.setNavigationMenu();
        recyclerView  = (RecyclerView)findViewById(R.id.museumRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false
        );
        recyclerView.setLayoutManager(manager);
        adapter = new MuseumAdapter(getHoldingActivity());
        View header = LayoutInflater
                .from(getHoldingActivity())
                .inflate(R.layout.header_museum_list,recyclerView,false);
        adapter.setHeaderView(header);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Museum museum = adapter.getMuseum(position);
                presenter.onMuseumChoose(museum);
                //ProgressBarManager.loadWaitPanel(getActivity(),"正在加载。。。",false);
            }
        });


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onViewCreated();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_museum_list;
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
    public void refreshMuseumList() {
        if(adapter == null){return;}
        adapter.updateData(museumList);
    }

    @Override
    public void skit2MuseumHome(Museum museum) {
        MuseumHomeFragment fragment = MuseumHomeFragment.newInstance(museum);
        showFragment(fragment);
    }

    @Override
    public void setChooseMuseum(Museum museum) {
        mListener.setCurrentMuseum(museum);
    }

    @Override
    public void onNoData() {

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
    public void setTitle(String cityName) {
        mListener.setTitle(cityName);
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public void setMuseumList(List<Museum> museumList) {
        this.museumList = museumList;
    }

    @Override
    public void showFailedError() {

    }

    @Override
    public void hideErrorView() {

    }

    @Override
    public void showToast(String content) {

    }

    @Override
    public void closeDrawer() {

    }

    @Override
    public Museum getChooseMuseum() {
        return mListener.getCurrentMuseum();
    }

    @Override
    public void showDownloadTipDialog() {
        /* 这里使用了 android.support.v7.app.AlertDialog.Builder
           可以直接在头部写 import android.support.v7.app.AlertDialog
           那么下面就可以写成 AlertDialog.Builder
           */
        AlertDialog.Builder builder = new AlertDialog.Builder(getHoldingActivity(),R.style.dialog);
        builder.setTitle("温馨提示");
        builder.setMessage("收听讲解需要先下载至本地，是否下载？");
        builder.setNegativeButton("取消",dialogListener );
        builder.setPositiveButton("确定", dialogListener);
        builder.show();
    }

    private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case AlertDialog.BUTTON_POSITIVE:
                    presenter.onDownloadMuseum();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    dialog.dismiss();
            }
        }
    };


    @Override
    public void updateProgress(int progress, int totalSize) {
        if(progressDialog == null){
            return;
        }
        int percent = progress * 100 / totalSize;
        progressDialog.setProgress(percent);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIcon(R.drawable.ic_file_download_grey600_24dp);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("进度");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if(progressDialog == null){return;}
        progressDialog.dismiss();
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

        void setTitle(String cityName);

        void setNavigationMenu();

        void setCurrentMuseum(Museum museum);

        Museum getCurrentMuseum();
    }
}
