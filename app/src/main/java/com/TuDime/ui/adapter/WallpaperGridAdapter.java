package com.TuDime.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.TuDime.R;
import com.TuDime.ui.adapter.listeners.SetclickCallback;

public class WallpaperGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    SetclickCallback setclickCallback;
  /*  protected OnWallpaperSelectedListener	onWallpaperSelectedListener;*/
    
    List<String> imgArr = new ArrayList<>();
    public WallpaperGridAdapter(Context mContext, List<String> imgArr) {
        this.mContext = mContext;
        this.imgArr=imgArr;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setclickCallback = (SetclickCallback) mContext;
    }

    @Override
    public int getCount() {
        return 106;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        ImageView imgSticker;
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.grid_item_wallpaper_view, null);
            imgSticker = (ImageView) convertView.findViewById(R.id.imgSticker);
            convertView.setTag(imgSticker);
        } else {
            imgSticker = (ImageView) convertView.getTag();
        }
        try{
            Picasso.get().load(imgArr.get(position)).placeholder(R.drawable.theme_2)
                    .into(imgSticker);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        imgSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    setclickCallback.set_walpaper(String.valueOf(imgArr.get(position)));
                }
        });
        return convertView;
    }

    /*public void setOnWallpaperSelectedListener(OnWallpaperSelectedListener onWallpaperSelectedListener) {
        this.onWallpaperSelectedListener = onWallpaperSelectedListener;
    }*/

}
