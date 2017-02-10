package com.systek.guide.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.systek.guide.R;
import com.systek.guide.adapter.CityAdapter;
import com.systek.guide.bean.City;
import com.systek.guide.iView.ICityChooseView;
import com.systek.guide.presenter.CityChoosePresenter;
import com.systek.guide.base.BaseFragment;
import com.systek.guide.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 城市选择Fragment
 */
public class CityFragment extends BaseFragment implements ICityChooseView {

    private OnCityFragmentInteractionListener mListener;
    private CityAdapter adapter;//适配器
    //private ClearEditText mClearEditText;
    private List<City> cities;
    //private AMapLocationClient locationClient;
    //private AMapLocationClientOption locationOption;
    private TextView currentCity,suggestCity;
    private ProgressBar loadingProgress;

    private CityChoosePresenter presenter;


    public interface OnCityFragmentInteractionListener {

        void setTitle(String title);

        void skip2MuseumListFragment(String cityName);

        void setNavigationNone();

        void setCurrentCity(String cityName);
    }

    /**
     * 工厂方法创建对象
     * @return
     */
    public static CityFragment newInstance() {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCityFragmentInteractionListener) {
            mListener = (OnCityFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CityChoosePresenter(this);
        mListener.setTitle("城市选择");

    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setNavigationNone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.initCity();

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        SideBar sideBar = (SideBar) findViewById(R.id.sidebar);
        TextView dialog = (TextView) findViewById(R.id.dialog);
        if (sideBar != null) {
            sideBar.setTextView(dialog);
        }
        RecyclerView cityRecyclerView = (RecyclerView) findViewById(R.id.city_recyclerView);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        LinearLayoutManager linearLayoutManage=new LinearLayoutManager(getActivity());
        linearLayoutManage.setOrientation(LinearLayoutManager.VERTICAL);
        if (cityRecyclerView != null) {
            cityRecyclerView.setLayoutManager(linearLayoutManage);
        }
        //mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        currentCity = (TextView) findViewById(R.id.currentCity);
        suggestCity = (TextView) findViewById(R.id.suggestCity);
        //refreshBtn=(Button)mErrorView.findViewById(R.id.refreshBtn);

        //mClearEditText.clearFocus();
        cities = new ArrayList<>();
        // 根据a-z进行排序源数据
        Collections.sort(cities, pinyinComparator);
        adapter = new CityAdapter(getHoldingActivity());
        if (cityRecyclerView != null) {
            cityRecyclerView.setAdapter(adapter);
            //去除滑动到末尾时的阴影
            cityRecyclerView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        }

        adapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                City city = adapter.getItem(position);
                presenter.onCityChoose(city);
            }
        });

    }

    private Comparator<City> pinyinComparator = new  Comparator<City>(){

        @Override
        public int compare(City lhs, City rhs) {
            if (lhs.getAlpha().equals("@")
                    || rhs.getAlpha().equals("#")) {
                return -1;
            } else if (lhs.getAlpha().equals("#")
                    || rhs.getAlpha().equals("@")) {
                return 1;
            } else {
                return lhs.getAlpha().compareTo(rhs.getAlpha());
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_city;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void refreshView() {
        adapter.updateData(cities);
    }

    @Override
    public String getCurrentInput() {
        return null;
    }

    @Override
    public List<City> getListCities() {
        return null;
    }

    @Override
    public void setListCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public void updateTipCities(List<City> cities) {

    }

    @Override
    public void setLocationCity(City city) {

    }

    @Override
    public void clearInputCity() {

    }

    @Override
    public void toNextFragment( String cityName) {
        mListener.skip2MuseumListFragment(cityName);
    }


    @Override
    public void showFailedError() {

    }

    @Override
    public void hideErrorView() {

    }

    @Override
    public void setTitle(String str) {

    }

    @Override
    public void setCurrentCity(String cityName) {
        mListener.setCurrentCity(cityName);
    }
}
