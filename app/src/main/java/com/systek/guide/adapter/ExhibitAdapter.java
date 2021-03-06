package com.systek.guide.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.systek.guide.R;
import com.systek.guide.base.BaseRecyclerAdapter;
import com.systek.guide.base.Constants;
import com.systek.guide.util.BitmapUtil;
import com.systek.guide.util.DensityUtil;
import com.systek.guide.util.FileUtil;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.db.handler.ExhibitHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiang on 2016/12/2.
 */

public class ExhibitAdapter extends BaseRecyclerAdapter<ExhibitAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Exhibit> exhibitList;

    public ExhibitAdapter(Context c){
        this.context = c.getApplicationContext();
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Exhibit> exhibits){
        this.exhibitList = exhibits;
        if(exhibits == null){
            this.exhibitList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    public Exhibit getExhibit(int position){
        return exhibitList == null ? null : exhibitList.get(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_exhibit,parent,false);
        ViewHolder holder=new ViewHolder(view);
        holder.tvExhibitName = (TextView) view.findViewById(R.id.tvExhibitName);
        holder.tvExhibitYears = (TextView) view.findViewById(R.id.tvExhibitYears);
        holder.tvExhibitPosition = (TextView) view.findViewById(R.id.tvExhibitPosition);
        holder.ivExhibitIcon = (ImageView) view.findViewById(R.id.ivExhibitIcon);
        holder.tvExhibitDistance = (TextView) view.findViewById(R.id.tvExhibitDistance);
        holder.llCollectionBtn = (LinearLayout) view.findViewById(R.id.llCollectionBtn);
        holder.ivCollection = (ImageView) view.findViewById(R.id.ivCollection);
        holder.exhibitNumber = (TextView) view.findViewById(R.id.number);
        holder.like = (TextView) view.findViewById(R.id.like);
        holder.ivPlayAnim = (ImageView) view.findViewById(R.id.ivPlayAnim);
        return holder;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder,final int realPosition) {
        ViewHolder holder = (ViewHolder)viewHolder;
        final Exhibit exhibit = exhibitList.get(realPosition);
        holder.tvExhibitName.setText(exhibit.getName());
        holder.tvExhibitYears.setText(exhibit.getLabels());
        holder.tvExhibitYears.setText(exhibit.getLabels());
        int text=exhibit.getNumber();
        /*此处直接赋值int会调用 一下方法，到资源中找此id，从而异常
         * @android.view.RemotableViewMethod
        public final void setText(@StringRes int resid) {
        setText(getContext().getResources().getText(resid));
        }*/
        holder.exhibitNumber.setText(String.valueOf(text));
        holder.tvExhibitPosition.setText(exhibit.getContent());
        holder.like.setText("收藏");
        String iconUrl = exhibit.getIconurl();
        String name = FileUtil.changeUrl2Name(iconUrl);
        String museumId = exhibit.getMuseumId();
        String path = Constants.LOCAL_PATH+museumId+"/"+name;
        File file = new File(path);
        if(file.exists()){
            //LogUtil.i("","图片路径为："+path);
            Bitmap bm = BitmapUtil.decodeSampledBitmapFromFile(
                    path,
                    DensityUtil.dp2px(context,120),
                    DensityUtil.dp2px(context,120)
            );
            Bitmap roundBm = BitmapUtil.getRoundedCornerBitmap(bm);
            holder.ivExhibitIcon.setImageBitmap(roundBm);
        }else{
            String url = Constants.BASE_URL + iconUrl;
            Glide.with(context)
                    .load(url)
                    .into(holder.ivExhibitIcon);
        }

        holder.llCollectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isFavorite = exhibit.getIsFavorite();
                if(isFavorite == 0){
                    exhibit.setIsFavorite(1);
                    ExhibitHandler.updateExhibit(exhibit);
                    notifyItemChanged(realPosition);
                }else{
                    exhibit.setIsFavorite(0);
                    ExhibitHandler.updateExhibit(exhibit);
                    notifyItemChanged(realPosition);
                }
            }
        });
        int isFavorite = exhibit.getIsFavorite();
        if(isFavorite == 0){
            holder.ivCollection.setImageDrawable(context.getResources().getDrawable(R.drawable.iv_heart_empty));
        }else{
            holder.ivCollection.setImageDrawable(context.getResources().getDrawable(R.drawable.iv_heart_full));
        }
    }

    @Override
    public int getItemCount() {
        return exhibitList == null ? 0 : exhibitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvExhibitName, tvExhibitYears,
                tvExhibitPosition,tvExhibitDistance;
        TextView exhibitNumber,like;
        ImageView ivExhibitIcon;
        ImageView ivCollection,ivPlayAnim;
        LinearLayout llCollectionBtn;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
