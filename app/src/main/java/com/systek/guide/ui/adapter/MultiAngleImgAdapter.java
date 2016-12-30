package com.systek.guide.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.systek.guide.R;
import com.systek.guide.base.Constants;
import com.systek.guide.base.util.BitmapUtil;
import com.systek.guide.base.util.DensityUtil;
import com.systek.guide.base.util.FileUtil;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.bean.MultiAngleImg;

import java.io.File;
import java.util.List;

/**
 * Created by qiang on 2016/12/5.
 */

public class MultiAngleImgAdapter extends RecyclerView.Adapter<MultiAngleImgAdapter.ViewHolder> {

    private Context context;
    private List<MultiAngleImg> list;
    private LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;//点击监听

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public MultiAngleImg get(int position){
        return list==null?null:list.get(position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MultiAngleImgAdapter(Context c) {
        this.context = c.getApplicationContext();
        inflater = LayoutInflater.from(context);
    }
    public MultiAngleImgAdapter(Context c, List<MultiAngleImg> list) {
        this.context = c.getApplicationContext();
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_multi_angle_img, parent, false);
        //FontManager.applyFont(context, view);
        ViewHolder viewHolder = new ViewHolder(view,onItemClickListener);
        viewHolder.ivMultiAngle = (ImageView) view.findViewById(R.id.ivMultiAngle);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MultiAngleImg multiAngleImg=list.get(position);
        String iconUrl = multiAngleImg.getUrl();
        //ImageUtil.displayImage(url, holder.ivMultiAngle,true,false);
        String name= FileUtil.changeUrl2Name(iconUrl);
        String museumId= multiAngleImg.getMuseumId();
        String path= Constants.LOCAL_PATH+museumId+"/"+name;
        File file=new File(path);
        if(file.exists()){
            LogUtil.i("","图片路径为："+path);
            Bitmap bm= BitmapUtil.decodeSampledBitmapFromFile(
                    path,
                    DensityUtil.dp2px(context,120),DensityUtil.dp2px(context,120)
            );
            Bitmap roundBm=BitmapUtil.getRoundedCornerBitmap(bm);
            holder.ivMultiAngle.setImageBitmap(roundBm);
        }else{
            String url = Constants.BASE_URL+iconUrl;

            Glide.with(context)
                    .load(url)
                    .into(holder.ivMultiAngle);
        }


    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    /**
     * 更新列表
     * @param list MultiAngleImg 集合
     */
    public void updateData(List<MultiAngleImg> list){
        this.list=list;
        notifyDataSetChanged();
    }

    /**
     * 内部接口，用于点击事件
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }



    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        OnItemClickListener onItemClickListener;
        ImageView ivMultiAngle;
        public ViewHolder(View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener=onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

}
