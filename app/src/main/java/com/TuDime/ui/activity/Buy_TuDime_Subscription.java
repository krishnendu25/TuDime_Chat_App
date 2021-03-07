package com.TuDime.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.stripe.android.Stripe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.utils.Constant;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

import static com.TuDime.constants.ApiConstants.buy_tudime_subscription;
import static com.TuDime.constants.ApiConstants.sendMailUrl;

public class Buy_TuDime_Subscription extends BaseActivity {
    @BindView(R.id.Buy_subscribe)
    Button BuySubscribe;
    private String Membership_Price = "";
    private String Account_Create, stripe_token = "";
    private String paymentIntentClientSecret;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_tudime_subscription);
        ButterKnife.bind(this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
        getEphemeral_key();
       showPaymentDialog();
    }

    private void showPaymentDialog() {

       new AlertDialog.Builder(Buy_TuDime_Subscription.this)
                .setMessage("Buy the membership of $15  per year to use this application. Your Payment Card information will not be saved by TuDime. If you buy the membership within 15 days, a 25% discount will be given, making the first year cost $11.25  ")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();



    }

    private void getEphemeral_key() {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.GET, stripe_token, 548, this, parms, false, false, Params_Object);
    }



    private void hit_Buy_Subscrption(String Payment_id, String Price) {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", SharedPrefsHelper.getInstance().getUSERID());
        parms.putString("plan_name", "TuDime Yearly Membership");
        parms.putString("plan_price", Price);
        parms.putString("Payment_Referance_no", Payment_id);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, buy_tudime_subscription, 570, this, parms, false, false, Params_Object);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.Buy_subscribe)
    public void onViewClicked() {
        Intent intent = new Intent(this, CheckoutActivityJava.class);
        intent.putExtra("Membership_Price", Membership_Price);
        intent.putExtra("stripe_token", stripe_token);
        startActivityForResult(intent,325);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 325) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("invoice");
                String email=data.getStringExtra("email");
                hitSendEmail(result,email);
                hit_Buy_Subscrption(result, Membership_Price);
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }//onActivityResult

    private void hitSendEmail(String invoice, String email) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","send_mail");
        parms.putString("email",email);
        parms.putString("text","Tax Invoice of TuDime Prime Yearly Membership"+"\n\n"+invoice);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, sendMailUrl,
                286, this, parms, false,false,Params_Object);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == 570) {
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    ToastUtils.longToast("Enjoy Your Annual TuDime Subscription");
                    finish();
                }
            } catch (JSONException e) {

            }



        }
        if (requestCode==286) {
            try{
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    ToastUtils.longToast("Your Annual TuDime Subscription Tax Invoice Send To Your Email");
                }

            }catch (Exception e)
            {
            }
        }
        if (requestCode == 447) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    Account_Create = response.getJSONArray("data").getJSONObject(0).getString("create_dt_timestamp");
                    String Acount_Create = Constant.Get_back_date(Account_Create);
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                    Integer Day_Count = (int) Constant.getDateDiff(Acount_Create, timeStamp);


                    if (Integer.valueOf(Day_Count) > 45) {
                        Membership_Price = "15";
                    } else if (Integer.valueOf(Day_Count) <= 15) {
                        Membership_Price = "11.25";
                    }


                } else {
                    ToastUtils.shortToast("Oops...something went wrong...");
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }

        }
        if (requestCode == 548) {
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    stripe_token = response.getJSONObject("data").getString("secret");
                }
            } catch (JSONException e) {

            }
        }

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}