package com.TuDime.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.TuDime.R;
import com.TuDime.utils.views.DoodleActivity;
import com.TuDime.ui.adapter.listeners.SetclickCallback;

public class EmojiGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    SetclickCallback setclickCallback;
    /*  protected OnWallpaperSelectedListener	onWallpaperSelectedListener;*/

    public EmojiGridAdapter(Context mContext) {
        this.mContext = mContext;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setclickCallback = (DoodleActivity) mContext;
    }

    @Override
    public int getCount() {
        return 173;
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

        int drawableResourceId =mContext.getResources().getIdentifier("emj_thumb_" + (position+ 1), "drawable", mContext.getPackageName());


        imgSticker.setBackground(mContext.getResources().getDrawable(drawableResourceId));
        imgSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setclickCallback.set_walpaper("emj_thumb_" + String.valueOf(position + 1));
            }
        });
        return convertView;
    }

    /*public void setOnWallpaperSelectedListener(OnWallpaperSelectedListener onWallpaperSelectedListener) {
        this.onWallpaperSelectedListener = onWallpaperSelectedListener;
    }*/

}
