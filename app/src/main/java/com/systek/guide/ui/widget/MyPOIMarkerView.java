package com.systek.guide.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.nimbledevices.indoorguide.ui.FloorPlanMarker;
import com.nimbledevices.indoorguide.ui.FloorPlanView;

import org.json.JSONObject;

/**
 * Created by qiang on 2016/12/2.
 */

public class MyPOIMarkerView extends TextView implements FloorPlanMarker {

    public static final String TAG = MyPOIMarkerView.class.getName();
    private static final float POI_ROTATION = -30.0F;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private Location mLocation;
    private Bundle extras;

    public MyPOIMarkerView(Context ctx) {
        this(ctx, new JSONObject());
    }

    public MyPOIMarkerView(Context ctx, JSONObject config) {
        super(ctx);
        this.mLocation = new Location("marker");
        this.setPadding(20, 5, 10, 5);
        this.setBackgroundDrawable(new Drawable() {
            public void draw(Canvas canvas) {
                int w = getMeasuredWidth();
                int h = getMeasuredHeight();
                float borderOffset = mBorderPaint.getStrokeWidth() / 1.9F;
                float arrInset = (float)getPaddingLeft() - (float)getPaddingRight() / 2.0F + borderOffset;
                Path p = new Path();
                p.moveTo(borderOffset, (float)h / 2.0F);
                p.rLineTo(arrInset, (float)(-h) / 2.0F + borderOffset);
                p.rLineTo((float)w - arrInset - 2.0F * borderOffset, 0.0F);
                p.rLineTo(0.0F, (float)h - borderOffset * 2.0F);
                p.rLineTo(-((float)w - arrInset - 2.0F * borderOffset), 0.0F);
                p.close();
                if(getBackgroundPaint() != null) {
                    canvas.drawPath(p, getBackgroundPaint());
                }

                if(getBorderPaint() != null) {
                    canvas.drawPath(p, getBorderPaint());
                }

            }

            public void setAlpha(int alpha) {
                if(mBackgroundPaint != null) {
                    mBackgroundPaint.setAlpha(alpha);
                }

                if(mBorderPaint != null) {
                    mBorderPaint.setAlpha(alpha);
                }

            }

            public void setColorFilter(ColorFilter cf) {
                mBackgroundPaint.setColorFilter(cf);
                mBorderPaint.setColorFilter(cf);
            }

            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        });
        this.updateWithConfig(config);
        this.setRotation(-30.0F);
        this.setPivotX(0.0F);
        this.setPivotY(0.0F);
    }

    public void updateWithConfig(JSONObject config) {
        this.setText(config.optString("name", "      "));
        this.setTextColor(FloorPlanView.parseColor(config.optString("textColor", "#000000")));
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
        this.mBackgroundPaint.setColor(FloorPlanView.parseColor(config.optString("backgroundColor", "#ff00ff")));
        int containerStrokeColor;
        if(config.optString("borderColor", (String)null) != null) {
            containerStrokeColor = FloorPlanView.parseColor(config.optString("borderColor"));
        } else {
            float[] hsv = new float[3];
            Color.colorToHSV(this.mBackgroundPaint.getColor(), hsv);
            hsv[2] *= 0.8F;
            containerStrokeColor = Color.HSVToColor(hsv);
        }

        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setColor(containerStrokeColor);
        this.mBorderPaint.setStrokeWidth(3.0F);
        this.mBorderPaint.setAntiAlias(true);
        this.mLocation.setLatitude(config.optDouble("lat"));
        this.mLocation.setLongitude(config.optDouble("lon"));
        this.mLocation.setAltitude(config.optDouble("alt"));
    }


    public Bundle getExtras(){
        return extras;
    }
    public void setExtras(Bundle extras){
        this.extras = extras;
    }

    private OnMarkerClickListener onClickListener;


    public interface OnMarkerClickListener {

        void onClick(FloorPlanMarker marker , Location location,Bundle extras);
    }


    public void setOnClickListener(OnMarkerClickListener listener){
        this.onClickListener=listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                //当点击位置位于控件内部,响应点击事件
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    onClickListener.onClick(this,mLocation,extras);
                }
                break;
        }
        return true;
    }

    public Paint getBackgroundPaint() {
        return this.mBackgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        this.mBackgroundPaint = paint;
    }

    public Paint getBorderPaint() {
        return this.mBorderPaint;
    }

    public void setBorderPaint(Paint paint) {
        this.mBorderPaint = paint;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setLocation(Location l) {
        this.mLocation = l;
    }

    public View getView() {
        return this;
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public PointF getAnchor() {
        return new PointF(0.0F, -0.5F);
    }


}
