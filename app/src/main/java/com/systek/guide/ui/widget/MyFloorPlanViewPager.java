package com.systek.guide.ui.widget;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nimbledevices.indoorguide.GuideManager;
import com.nimbledevices.indoorguide.ui.FloorPlanMarker;
import com.nimbledevices.indoorguide.ui.FloorPlanView;
import com.nimbledevices.indoorguide.ui.OnFloorChangeListener;
import com.nimbledevices.indoorguide.ui.qozix.layouts.ZoomPanLayout;
import com.nimbledevices.indoorguide.ui.qozix.tileview.graphics.BitmapDecoder;
import com.nimbledevices.indoorguide.ui.util.FloorPlanAdapter;
import com.nimbledevices.indoorguide.ui.util.VerticalViewPager;

import org.json.JSONObject;


/**
 * Created by qiang on 2016/12/9.
 *
 */

public class MyFloorPlanViewPager extends VerticalViewPager implements ZoomPanLayout.ZoomPanListener {

    protected static final String TAG = MyFloorPlanViewPager.class.getSimpleName();
    protected FloorPlanAdapter mAdapter;
    private FloorPlanView currentFloor;
    boolean floorSwipeEnabled = true;
    private Thread mainThread;
    private OnFloorChangeListener onFloorChangeListener;
    private FloorPlanMarker mUserMarker;

    private void commonSetup(Context context) {
        this.mainThread = Looper.getMainLooper().getThread();
        this.mAdapter = new FloorPlanAdapter(context);
        this.setAdapter(this.mAdapter);
        this.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                FloorPlanView v = MyFloorPlanViewPager.this.mAdapter.getFloorPlanViewForPosition(position);
                if(v != null && MyFloorPlanViewPager.this.currentFloor != v) {
                    if(MyFloorPlanViewPager.this.currentFloor != null) {
                        MyFloorPlanViewPager.this.currentFloor.removeZoomPanListener(MyFloorPlanViewPager.this);
                        MyFloorPlanViewPager.this.currentFloor.setScale(0.0D);
                    }

                    MyFloorPlanViewPager.this.currentFloor = v;
                    MyFloorPlanViewPager.this.currentFloor.addZoomPanListener(MyFloorPlanViewPager.this);
                }

                if(MyFloorPlanViewPager.this.onFloorChangeListener != null && MyFloorPlanViewPager.this.mAdapter.getCount() > 0) {
                    MyFloorPlanViewPager.this.onFloorChangeListener.onFloorChanged(MyFloorPlanViewPager.this.mAdapter.getFloorIndexForPosition(position), (double)MyFloorPlanViewPager.this.mAdapter.getAltitudeForPosition(position));
                }

            }
        });
    }

    public MyFloorPlanViewPager(Context context) {
        super(context);
        this.commonSetup(context);
    }

    public MyFloorPlanViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.commonSetup(context);
    }

    public void setDataSource(GuideManager gm) {
        this.setBitmapDecoder(gm);
        this.setConfiguration(gm.getWidgetConfiguration());
        this.invalidate();
    }

    public void setConfiguration(JSONObject floorConf) {
        assert this.mainThread.equals(Thread.currentThread());

        this.mAdapter.setConfiguration(floorConf);
        this.setAdapter(this.mAdapter);
    }

    public void setBitmapDecoder(BitmapDecoder decoder) {
        this.mAdapter.setDecoder(decoder);
    }

    public void setUserLocation(Location location) {
        this.setUserLocation(location, true);
    }

    public void setUserLocation(Location location, boolean allowAutomaticFloorChange) {
        assert this.mainThread.equals(Thread.currentThread());

        boolean changeFloor;
        if(this.mUserMarker == null) {
            this.mUserMarker = new MyUserMarker(this.getContext(), 50.0F);
            this.mUserMarker.setLocation(location);
            this.addMarker(this.mUserMarker);
            changeFloor = true;
        } else {
            changeFloor = this.moveMarker(this.mUserMarker, location);
            this.mUserMarker.setLocation(location);
        }

        if(changeFloor && allowAutomaticFloorChange) {
            Integer position = this.mAdapter.getPositionForAltitude((int)location.getAltitude());
            if(position != null) {
                this.setCurrentItem(position.intValue());
            }
        }

    }

    public void setRoute(double[][] triplets) {
        this.mAdapter.setRoute(triplets);
    }

    private void setFloorSwipeEnabled(boolean floorSwipeEnabled) {
        this.floorSwipeEnabled = floorSwipeEnabled;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(this.floorSwipeEnabled) {
            super.onTouchEvent(event);
            return false;
        } else {
            return false;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.floorSwipeEnabled && event.getPointerCount() == 1?super.onInterceptTouchEvent(event):false;
    }

    public void addMarker(FloorPlanMarker marker) {
        this.mAdapter.addMarker(marker);
    }

    public void removeMarker(FloorPlanMarker marker) {
        this.mAdapter.removeMarker(marker);
    }

    public boolean moveMarker(FloorPlanMarker marker, Location newLocation) {
        return this.mAdapter.moveMarker(marker, newLocation);
    }

    public int getFloorCount() {
        return this.mAdapter.getCount();
    }

    public int getFloorIndex() {
        return this.mAdapter.getCount() - 1 - this.getCurrentItem();
    }

    public void setFloorIndex(int index) {
        this.setCurrentItem(this.mAdapter.getCount() - 1 - index, true, 20);
    }

    public void floorUp() {
        this.setFloorIndex(this.getFloorIndex() + 1);
    }

    public void floorDown() {
        this.setFloorIndex(this.getFloorIndex() - 1);
    }

    public void setOnFloorChangeListener(OnFloorChangeListener onFloorChangeListener) {
        this.onFloorChangeListener = onFloorChangeListener;
    }

    public void onScaleChanged(double scale) {
        if(Math.abs(scale - this.currentFloor.getMinimumScale()) < 0.01D && this.getAdapter().getCount() > 1) {
            this.setFloorSwipeEnabled(true);
        } else {
            this.setFloorSwipeEnabled(false);
        }

    }

    public void onScrollChanged(int x, int y) {
    }

    public void onZoomStart(double scale) {
    }

    public void onZoomComplete(double scale) {
    }

    public void onZoomPanEvent() {
    }
}
