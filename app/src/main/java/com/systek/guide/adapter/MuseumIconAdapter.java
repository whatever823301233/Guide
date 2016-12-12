package com.systek.guide.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.systek.guide.R;
import com.systek.guide.base.AppManager;
import com.systek.guide.base.Constants;
import com.systek.guide.base.util.AndroidUtil;
import com.systek.guide.base.util.BitmapUtil;
import com.systek.guide.base.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiang on 2016/12/2.
 *
 * 主页横向滑动图片的adapter
 */

public class MuseumIconAdapter extends BaseRecyclerAdapter<MuseumIconAdapter.ViewHolder>{

    private Context context;
    private List<String> list;
    private LayoutInflater inflater;
    private String  museumId;


    public MuseumIconAdapter(Context context) {
        this.context = context.getApplicationContext();
        inflater=LayoutInflater.from(this.context);
        this.list = new ArrayList<>();
    }

    public MuseumIconAdapter(Context context,String museumId) {
        this.context = context.getApplicationContext();
        inflater = LayoutInflater.from(this.context);
        this.museumId=museumId;
        this.list = new ArrayList<>();
    }

    public void setMuseumId(String museumId) {
        this.museumId = museumId;
    }


    public void updateData(List<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_museum_icon, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
        return viewHolder;
    }


    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition) {
        if(!(viewHolder instanceof ViewHolder)){return;}
        ViewHolder holder = (ViewHolder) viewHolder;
        String  url = list.get(realPosition);
        boolean isFileExists = FileUtil.checkFileExists(url,museumId);
        if(isFileExists){
            String name = FileUtil.changeUrl2Name(url);
            Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(Constants.LOCAL_PATH + museumId + "/" + name,
                    AndroidUtil.getMobileWidth(AppManager.getInstance(context).getCurrentActivity()),
                    AndroidUtil.getMobileHeight(AppManager.getInstance(context).getCurrentActivity()));
            holder.imageView.setImageBitmap(bitmap);
        }else{
            Glide.with(context)
                    .load(Constants.BASE_URL + url)
                    .into(holder.imageView);


            //QVolley.getInstance(null).loadImage(,holder.imageView,0,0);
            //holder.imageView.displayImage(Constants.BASE_URL+url);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
