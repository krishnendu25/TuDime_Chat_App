package com.TuDime.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import com.TuDime.R;
import com.TuDime.ui.adapter.CountryListAdapter;
import com.TuDime.ui.adapter.listeners.SetclickCallback;
import com.TuDime.Prefrences.SharedPrefsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowCountryList extends BaseActivity implements SetclickCallback
{
    private RecyclerView lsvCountries;
    private CountryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_show_country_list);


        Initialization();

    }

    private void Initialization()
    {
        hideActionbar();
        List<String> countriesListComplete=new ArrayList<>();

        lsvCountries =(RecyclerView)findViewById(R.id.lsvCountries);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        lsvCountries.setLayoutManager(mLayoutManager);

        String[] countriesArray = getApplicationContext().getResources().getStringArray(R.array.CountryCodes);
        countriesListComplete = Arrays.asList(countriesArray);




        adapter = new CountryListAdapter(ShowCountryList.this,countriesListComplete);
        lsvCountries.setAdapter(adapter);




    }

    @Override
    public void onclick()
    {
        finish();
    }

    @Override
    public void set_walpaper(String Photo_Name) {
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}
