package obj.quickblox.sample.chat.java.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.RequestBody;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import okhttp3.MediaType;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.buy_tudime_subscription;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.sendMailUrl;

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