package obj.quickblox.sample.chat.java.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Model.Location_Venues;
import obj.quickblox.sample.chat.java.util.FourSquareNearbyPlaceResponseModel;

public class LocationsListAdapter extends BaseAdapter {

    private ArrayList<Location_Venues> mVenuesList;
    private Context mContext;
    private LayoutInflater mInflator;

    public LocationsListAdapter(Context context) {
        mContext = context;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(ArrayList<Location_Venues> venuesList) {
        mVenuesList = venuesList;
    }

    @Override
    public int getCount() {
        return mVenuesList == null ? 0 : mVenuesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVenuesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_locations, null);
            holder = new ViewHolder();
            holder.txvLocationName = (TextView) view.findViewById(R.id.txvLocationName);
            holder.txvDistance = (TextView) view.findViewById(R.id.txvDistance);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txvLocationName.setText(mVenuesList.get(position).getName() + "");
        holder.txvDistance.setText(mVenuesList.get(position).getDistance() + "");
        return view;
    }

    public static class ViewHolder {
        TextView	txvLocationName;
        TextView	txvDistance;
    }

}
