package com.systek.guide.iView;


import android.location.Location;

/**
 * Created by qiang on 2016/12/2.
 */

public interface IMapView {

    boolean isUseRoutedLocation();
    void setUseRoutedLocation(boolean useRoutedLocation);

    void setUserLocation(Location location);

    void setRoute(double[][] triplets);

    void setTitle();
}
