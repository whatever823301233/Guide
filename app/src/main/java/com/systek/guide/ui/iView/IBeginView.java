package com.systek.guide.ui.iView;

import android.content.Context;
import android.content.Intent;


/**
 * Created by Qiang on 2016/9/22.
 */
public interface IBeginView {

    void goToNextActivity(Intent intent);

    Context getContext();

}
