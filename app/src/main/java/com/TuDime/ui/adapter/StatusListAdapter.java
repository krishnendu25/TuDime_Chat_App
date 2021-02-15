package com.TuDime.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.TuDime.R;

public class StatusListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mListdata;
    private LayoutInflater mInflator;
    private int mSelectedPos;

    public StatusListAdapter(Context context) {
        mContext = context;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(ArrayList<String> pStatusList) {
        mListdata = pStatusList;
    }

    @Override
    public int getCount() {
        return mListdata == null ? 0 : mListdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mListdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.list_item_status, null);
            holder = new ViewHolder();
            holder.txvStatus = (TextView) convertView.findViewById(R.id.txvStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSelectedPos == position) {
            holder.txvStatus.setTextColor(mContext.getResources().getColor(R.color.primary_blue));
        } else {
            holder.txvStatus.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        holder.txvStatus.setText(mListdata.get(position));

        return convertView;
    }

    public void setSelected(int pos) {
        mSelectedPos = pos;
    }

    private int getSelectedPos() {
        return mSelectedPos;
    }

    public  class ViewHolder {

        private TextView txvStatus;

    }

}