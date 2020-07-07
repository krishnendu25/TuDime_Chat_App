package obj.quickblox.sample.chat.java.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.buy_call_balence_update;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_tudime_fetch_my_call_balence;
import static obj.quickblox.sample.chat.java.constants.PAYPAL_CONFIGURATION.CLIENT_ID;
import static obj.quickblox.sample.chat.java.constants.PAYPAL_CONFIGURATION.MERCHENT_NAME;

public class PapPallIntegration extends BaseActivity {

    @BindView(R.id.enter_amount)
    EditText enterAmount;
    @BindView(R.id.amount_paypal)
    Button amountPaypal;
    private static final String TAG = "paymentExample";
    //  private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    // private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID)
            .merchantName(MERCHENT_NAME)
            .merchantPrivacyPolicyUri(Uri.parse("http://18.219.14.108/tudime_sms/Mobile_en.html"))
            .merchantUserAgreementUri(Uri.parse("http://18.219.14.108/tudime_sms/Mobile_en.html"));
    @BindView(R.id.My_Balence)
    TextView MyBalence;
    private String Balence_Api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_pap_pall_integration);
        ButterKnife.bind(this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Buy Call Credit");


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

    private void Verify_Payment(JSONObject toJSONObject) {
        try {
            if (toJSONObject.getJSONObject("response").getString("state").equalsIgnoreCase("approved")) {
                hit_Call_Balance(toJSONObject.getJSONObject("response").getString("id"), enterAmount.getText().toString());
            } else {
                ToastUtils.longToast("Transaction Successful");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hit_Call_Balance(String paypal_ency, String price) {
        int Total_Balance = 0;
        if (Balence_Api != null) {
            if (Balence_Api.equalsIgnoreCase("")) {
                Balence_Api = "0";

                Total_Balance = Integer.valueOf(Balence_Api) + Integer.valueOf(price);
            } else {
                Total_Balance = Integer.valueOf(Balence_Api) + Integer.valueOf(price);
            }
        } else {
            Balence_Api = "0";
            Total_Balance = Integer.valueOf(Balence_Api) + Integer.valueOf(price);
        }


        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", SharedPrefsHelper.getInstance().getUSERID());
        parms.putString("plan_name", "TuDime Credit Recharge");
        parms.putString("plan_price", String.valueOf(Total_Balance));
        parms.putString("Payment_Referance_no", paypal_ency);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, buy_call_balence_update, 892, this, parms, false, false, Params_Object);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Fetch_My_Call_Balence(SharedPrefsHelper.getInstance().getUSERID());
    }

    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(enterAmount.getText().toString()), "USD", "Call Credit",
                paymentIntent);
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        hideProgressDialog();
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == 892) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    ToastUtils.longToast("Your Recharge Successful");
                    Fetch_My_Call_Balence(SharedPrefsHelper.getInstance().getUSERID());
                } else {
                    hideProgressDialog();
                    ToastUtils.longToast("Recharge Unsuccessful");
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }

        if (requestCode == 715) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    JSONArray data = response.getJSONArray("data");

                    if (data.length()<1)
                    { Balence_Api="0";
                        MyBalence.setText("$"+Balence_Api);
                    }else
                    {
                        Balence_Api = data.getJSONObject(0).getString("plan_price");
                        if (Balence_Api.equalsIgnoreCase(""))
                        {
                            Balence_Api="0";
                        }
                        MyBalence.setText("$"+Balence_Api);
                        enterAmount.setText("");
                    }





                } else {
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }

    }

    private void Fetch_My_Call_Balence(String userid) {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", userid);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_tudime_fetch_my_call_balence, 715, this, parms, false, false, Params_Object);


    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        hideProgressDialog();
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
        hideProgressDialog();
    }

    @OnClick(R.id.amount_paypal)
    public void onViewClicked() {
        if (enterAmount.getText().toString().isEmpty()) {
            ToastUtils.shortToast("Enter Call Credit");
            return;
        } else  if (Integer.valueOf(enterAmount.getText().toString())<5) {
            ToastUtils.shortToast("Minimum Recharge $5");
            return;
        } else {
            onBuyPressed();
        }
    }
}
