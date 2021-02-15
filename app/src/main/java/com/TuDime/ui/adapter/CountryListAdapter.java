package com.TuDime.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.TuDime.R;
import com.TuDime.ui.activity.SignUpActivity;
import com.TuDime.ui.adapter.listeners.SetclickCallback;

import java.util.List;
import java.util.Locale;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> countriesListComplete;
    SetclickCallback setclickCallback;
    public CountryListAdapter(Context mContext, List<String> data) {
        this.mContext = mContext;
        this.countriesListComplete = data;
        this.setclickCallback = (SetclickCallback) mContext;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_countries, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {

        String[] countryInfo   =  (String[]) countriesListComplete.get(position).split(",");


        String countryname = GetCountryZipCode(countryInfo[1]).trim();

        holder.txvCountryName.setText(countryname);

        holder.txvCountryCode.setText("+" + countryInfo[0]);

        String pngName = countryInfo[1].trim().toLowerCase();
        holder.imgFlag.setImageResource(mContext.getResources().getIdentifier("drawable/" + pngName, null, mContext.getPackageName()));
        holder.Select_Cou_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SignUpActivity.country_code="+" + countryInfo[0];
                SignUpActivity.country_name=GetCountryZipCode(countryInfo[1]).trim();
                setclickCallback.onclick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return countriesListComplete.size();
    }

    //View Holder
    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView imgFlag;
        TextView txvCountryName;
        TextView txvCountryCode;
        LinearLayout Select_Cou_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgFlag = (ImageView) itemView.findViewById(R.id.imgFlag);
            txvCountryName = (TextView) itemView.findViewById(R.id.txvCountryName);
            txvCountryCode = (TextView) itemView.findViewById(R.id.txvCountryCode);
            Select_Cou_view = (LinearLayout) itemView.findViewById(R.id.Select_Cou_view);

        }
    }
    private String GetCountryZipCode(String ssid) {
        Locale loc = new Locale("", ssid);

        return loc.getDisplayCountry().trim();
    }
}