package com.systek.guide.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.systek.guide.ui.BaseFragment;

import java.util.ArrayList;

/**
 * Created by qiang on 2016/12/5.
 */

public class LyricViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BaseFragment> fragmentBases = new ArrayList<BaseFragment>();

    public void setFragmentBases(ArrayList<BaseFragment> fragmentBases) {
        this.fragmentBases = fragmentBases;
    }

    public LyricViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public LyricViewPagerAdapter(FragmentManager fragmentManager, ArrayList<BaseFragment> fragmentBases) {
        super(fragmentManager);
        this.fragmentBases = fragmentBases;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentBases.get(position);
    }

    @Override
    public int getCount() {
        return fragmentBases.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container, position, object);
    }



}
