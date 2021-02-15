package com.TuDime.utils.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.TuDime.R;


public class ColorGridAdapter extends BaseAdapter {

	private Context			mContext;
	private String[]		colorArray			= { "#ffffff", "#fead11",
			"#fedf11", "#11b41c", "#52b5db", "#000000", "#777777", "#a76fd7",
			"#7b519d", "#ff3b30"				};
	private LayoutInflater	mInflator;
	private int				colorDrawableLight;
	private int				colorDrawableDark;
	private int				selectedPosition	= 8;

	public ColorGridAdapter(Context context) {
		mContext = context;
		mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		colorDrawableLight = mContext.getResources().getColor(R.color.gray_light_bg);
		colorDrawableDark = mContext.getResources().getColor(R.color.gray_background);
	}

	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}

	@Override
	public int getCount() {
		return colorArray.length;
	}

	@Override
	public Object getItem(int position) {
		return colorArray[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ImageView imgColor;
		if (view == null) {
			view = mInflator.inflate(R.layout.grid_view_color, null);
			imgColor = (ImageView) view.findViewById(R.id.imgColor);
			view.setTag(imgColor);
		} else {
			imgColor = (ImageView) view.getTag();
		}
		if (selectedPosition == position) {
			imgColor.setBackgroundColor(colorDrawableDark);
		} else {
			imgColor.setBackgroundColor(colorDrawableLight);
		}
		imgColor.setImageDrawable(new ColorDrawable(Color.parseColor(colorArray[position])));

		return view;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

}
