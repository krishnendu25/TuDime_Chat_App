package com.TuDime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.R;
import com.TuDime.ui.Model.Subscription_Model;
import com.TuDime.utils.Constant;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

public class TuDime_Membership extends BaseActivity {

    @BindView(R.id.plane_type)
    TextView planeType;
    @BindView(R.id.Plan_Details_tv)
    TextView PlanDetailsTv;
    @BindView(R.id.buy_membership)
    TextView buyMembership;
    ArrayList<Subscription_Model> Subscription_Model = new ArrayList<>();
    private String Account_Create="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tudime_membership);
        ButterKnife.bind(this);
        Instantiation();

    }

    private void Instantiation() {
        actionBar.setTitle("Account Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        if (requestCode==447)
        {hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    Fetch_Membership_Details(SharedPrefsHelper.getInstance().getUSERID());
                     Account_Create = response.getJSONArray("data").getJSONObject(0).getString("create_dt_timestamp");
                } else
                {ToastUtils.shortToast("Oops...something went wrong...");}
            } catch (JSONException e) {
                hideProgressDialog();
            }

        }

        if (requestCode==897)
        {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    Subscription_Model.clear();
                    JSONArray all_data = response.getJSONArray("data");

                    for (int i=0; i<all_data.length(); i++)
                    {
                        Subscription_Model sb = new Subscription_Model();
                        sb.setStart_time(all_data.getJSONObject(i).getString("start_time_unix_timestamp"));
                        sb.setEnd_time(all_data.getJSONObject(i).getString("end_time_unix_timestamp"));
                        Subscription_Model.add(sb);
                    }
                    Filter_Subscription(Account_Create);
                }else
                {
                    ToastUtils.shortToast("Oops...something went wrong...");
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }

    }

    private void Filter_Subscription(String account_create) {
        String Acount_Create = Constant.Get_back_date(account_create);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Integer Day_Count = (int)  Constant.getDateDiff(Acount_Create, timeStamp);








        if (Day_Count>45) {
            planeType.setText(getString(R.string.Free_Trial_Expired));
            PlanDetailsTv.setText(getString(R.string.Free_Trial_Expired));
            buyMembership.setVisibility(View.VISIBLE);
        }else
        {
            if (Subscription_Model.size() > 0) {
                int count=0;
                for (int i=0; i<Subscription_Model.size();i++)
                {

                    String Day_Count_v = String.valueOf(Constant.getDateDiff(timeStamp, Constant.Get_back_date(Subscription_Model.get(i).getEnd_time().toString())));
                    if (Integer.valueOf(Day_Count_v)>0)
                    {
                        count++;
                        planeType.setText(getString(R.string.Annual_Membership));
                        PlanDetailsTv.setText(Day_Count_v+getString(R.string.TuDime_Annual));
                        buyMembership.setVisibility(View.GONE);
                    }
                }
                if (count==0)
                {
                    if (Integer.valueOf(Day_Count) <= 45 &&Integer.valueOf(Day_Count)>=0)  {
                        planeType.setText(getString(R.string.Free_Trial));
                        Integer day_left = 45-Integer.valueOf(Day_Count);
                        PlanDetailsTv.setText(day_left+getString(R.string.Premium_Features));
                        buyMembership.setVisibility(View.VISIBLE);
                    }else
                    {
                        planeType.setText(getString(R.string.Free_Trial_Expired));
                        PlanDetailsTv.setText(getString(R.string.Free_Trial_Expired));
                        buyMembership.setVisibility(View.VISIBLE);
                    }
                }

            }else
            {
                if (Integer.valueOf(Day_Count) <= 45 &&Integer.valueOf(Day_Count)>=0)  {
                    planeType.setText(getString(R.string.Free_Trial));
                    Integer day_left = 45-Integer.valueOf(Day_Count);
                    PlanDetailsTv.setText(day_left+getString(R.string.Premium_Features));
                    buyMembership.setVisibility(View.VISIBLE);
                }else
                {
                    planeType.setText(getString(R.string.Free_Trial_Expired));
                    PlanDetailsTv.setText(getString(R.string.Free_Trial_Expired));
                    buyMembership.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }

    @OnClick(R.id.buy_membership)
    public void onViewClicked() {
        startActivity(new Intent(this,Buy_TuDime_Subscription.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
    }


}