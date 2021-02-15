package com.TuDime.ui.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import com.TuDime.R;
import com.TuDime.ui.fragments.Secret_Chat_Dialog;
import com.TuDime.ui.fragments.Sercet_Chat_Contact;

public class Secret_Chat extends BaseActivity   {
    TabLayout Scercet_tabs;
    ViewPager Scercet_pager;
    MyPagerAdapter myPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_chat);
        Instantiation();









    }

    private void Instantiation() {
        Scercet_tabs = findViewById(R.id.Scercet_tabs);
        Scercet_pager = findViewById(R.id.Scercet_pager);
        Scercet_tabs.setupWithViewPager(Scercet_pager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        Scercet_pager.setAdapter(myPagerAdapter);
        myPagerAdapter.notifyDataSetChanged();
        Scercet_pager.setCurrentItem(1);
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

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Secret_Chat_Dialog.newInstance();
                case 1:
                    return Sercet_Chat_Contact.newInstance();
                default:
                    return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return "CHATS";
            } else if (position == 1) {
                return "CONTACTS";
            }
        return "";
        }
    }
}
