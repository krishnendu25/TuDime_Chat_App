package obj.quickblox.sample.chat.java.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.SMS_URL;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.buy_tudime_subscription;
import static obj.quickblox.sample.chat.java.constants.PAYPAL_CONFIGURATION.CLIENT_ID;
import static obj.quickblox.sample.chat.java.constants.PAYPAL_CONFIGURATION.MERCHENT_NAME;
import static obj.quickblox.sample.chat.java.utils.Constant.getCountOfDays;

public class Buy_TuDime_Subscription extends BaseActivity {
    private static final String TAG = "paymentExample";
     private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    // private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
   // private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID)
            .merchantName(MERCHENT_NAME)
            .merchantPrivacyPolicyUri(Uri.parse("http://18.219.14.108/tudime_sms/Mobile_en.html"))
            .merchantUserAgreementUri(Uri.parse("http://18.219.14.108/tudime_sms/Mobile_en.html"));
    @BindView(R.id.Buy_subscribe)
    Button BuySubscribe;
    private String Membership_Price="";
    private String Account_Create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_tudime_subscription);
        ButterKnife.bind(this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString());
                        Verify_Payment(confirm.toJSONObject());
                    } catch (Exception e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void Verify_Payment(JSONObject toJSONObject)
    {
        try {
            if (toJSONObject.getJSONObject("response").getString("state").equalsIgnoreCase("approved"))
            {
               hit_Buy_Subscrption( toJSONObject.getJSONObject("response").getString("id"),Membership_Price);
            }else
            {
                ToastUtils.longToast("Transaction Successful");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hit_Buy_Subscrption(String Payment_id,String Price) {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", SharedPrefsHelper.getInstance().getUSERID());
        parms.putString("plan_name","TuDime Yearly Membership");
        parms.putString("plan_price",Price);
        parms.putString("Payment_Referance_no",Payment_id);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST,buy_tudime_subscription,570,this,parms,false,false,Params_Object);
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(Membership_Price), "USD", "Subscription",
                paymentIntent);
    }


    @OnClick(R.id.Buy_subscribe)
    public void onViewClicked() {
        onBuyPressed();
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if(requestCode==570)
        {
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                ToastUtils.longToast("Enjoy Your Annual TuDime Subscription");
                finish();
                }
            } catch (JSONException e) {

            }

        }

        if (requestCode==447)
        {hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    Account_Create = response.getJSONArray("data").getJSONObject(0).getString("create_dt_timestamp");
                    String Acount_Create = Constant.Get_back_date(Account_Create);
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                    Integer Day_Count = (int)  Constant.getDateDiff(Acount_Create, timeStamp);


                    if (Integer.valueOf(Day_Count)>45)
                    {
                        Membership_Price="15";
                    }else if (Integer.valueOf(Day_Count)<=15)
                    {
                        Membership_Price="11.25";
                    }


                } else
                {ToastUtils.shortToast("Oops...something went wrong...");}
            } catch (JSONException e) {
                hideProgressDialog();
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