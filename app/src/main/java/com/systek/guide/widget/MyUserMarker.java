package com.systek.guide.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;

import com.nimbledevices.indoorguide.ui.FloorPlanMarker;
import com.systek.guide.R;

/**
 * Created by qiang on 2016/12/9.
 */

public class MyUserMarker extends ImageView implements FloorPlanMarker {

    private float mOuterRadius;
    private float mInnerRadius;
    private float mCenter;
    private float mDirectionDeg = 0.0F / 0.0F;
    private float mDirectionAccuracyDeg = 0.0F / 0.0F;
    private Path mArrowPath;
    private Paint mBackgroundPaint;
    private Paint mCenterPaint;
    private Paint mArrowPaint;
    private Location mLocation = new Location("user");
    private static PointF _anchor = new PointF(-0.5F, -0.5F);

    public MyUserMarker(Context ctx, float size) {
        super(ctx);
        this.mCenter = size * 0.5F;
        this.mOuterRadius = this.mCenter * 0.8F;
        this.mInnerRadius = this.mOuterRadius * 0.6F;
        float arrowLength = this.mCenter - this.mInnerRadius;
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
        this.mBackgroundPaint.setColor(-1);
        this.mBackgroundPaint.setAntiAlias(true);
        this.mCenterPaint = new Paint();
        this.mCenterPaint.setStyle(Paint.Style.FILL);
        this.mCenterPaint.setColor(getResources().getColor(R.color.blue_600));
        this.mCenterPaint.setAntiAlias(true);
        this.mArrowPaint = new Paint();
        this.mArrowPaint.setStyle(Paint.Style.FILL);
        this.mArrowPaint.setColor(getResources().getColor(R.color.red_600));
        this.mArrowPaint.setAntiAlias(true);
        this.mArrowPath = new Path();
        float arrowHalfHeight = arrowLength * 0.5F;
        this.mArrowPath.moveTo(this.mCenter + arrowLength * 0.25F, this.mCenter + 0.0F);
        this.mArrowPath.lineTo(this.mCenter + 0.0F, this.mCenter + arrowHalfHeight);
        this.mArrowPath.lineTo(this.mCenter + arrowLength, this.mCenter + 0.0F);
        this.mArrowPath.lineTo(this.mCenter + 0.0F, this.mCenter - arrowHalfHeight);
        this.mArrowPath.close();
        this.setWillNotDraw(false);
    }

    void setPrimaryColor(int color) {
        this.mCenterPaint.setColor(color);
    }

    void setSecondaryColor(int color) {
        this.mBackgroundPaint.setColor(color);
    }

    void setHeading(float directionDeg, float accuracyPlusMinusDeg) {
        this.mDirectionDeg = directionDeg;
        this.mDirectionAccuracyDeg = accuracyPlusMinusDeg;
        this.invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension((int)(this.mCenter * 2.0F), (int)(this.mCenter * 2.0F));
    }

    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawCircle(this.mCenter, this.mCenter, this.mOuterRadius, this.mBackgroundPaint);
        if(!Float.isNaN(this.mDirectionDeg)) {
            float accuracyPercentage = 1.0F;
            if(!Float.isNaN(this.mDirectionAccuracyDeg)) {
                accuracyPercentage = 1.0F - Math.min(1.0F, Math.max(0.0F, this.mDirectionAccuracyDeg / 180.0F));
            }

            c.save();
            c.rotate(-90.0F - this.mDirectionDeg, this.mCenter, this.mCenter);
            c.translate(this.mInnerRadius * accuracyPercentage, 0.0F);
            c.drawPath(this.mArrowPath, this.mCenterPaint);
            c.restore();
        }

        c.drawCircle(this.mCenter, this.mCenter, this.mInnerRadius, this.mCenterPaint);
    }

    public View getView() {
        return this;
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public void setLocation(Location l) {
        this.mLocation = l;
        if(l.hasBearing()) {
            this.mDirectionDeg = l.getBearing();
            if(l.getExtras() != null) {
                this.mDirectionAccuracyDeg = l.getExtras().getFloat("bearingAccuracy", 0.0F / 0.0F);
            } else {
                this.mDirectionAccuracyDeg = 0.0F / 0.0F;
            }
        } else {
            this.mDirectionDeg = 0.0F / 0.0F;
        }

        this.invalidate();
    }

    public PointF getAnchor() {
        return _anchor;
    }

}
