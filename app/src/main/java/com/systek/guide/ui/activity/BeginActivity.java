package com.systek.guide.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.systek.guide.R;
import com.systek.guide.util.Utility;
import com.systek.guide.ui.iView.IBeginView;
import com.systek.guide.presenter.BeginPresenter;
import com.systek.guide.ui.AppActivity;
import com.systek.guide.ui.BaseFragment;

public class BeginActivity extends AppActivity implements IBeginView {


    private BeginPresenter presenter;

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉虚拟按键全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_welcom);
        presenter = new BeginPresenter(this);
        presenter.onCreate();
    }

    @Override
    public void cancelAllTask() {
    }

    @Override
    public void goToNextActivity(Intent intent) {
        Utility.startActivity(getActivity(),intent);
        finish();
    }


}
