package com.TuDime.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

import java.text.DecimalFormat;

import static com.TuDime.constants.ApiConstants.buy_call_balence_update;
import static com.TuDime.constants.ApiConstants.get_user_tudime_fetch_my_call_balence;
import static com.TuDime.constants.ApiConstants.sendMailUrl;

public class PapPallIntegration extends BaseActivity {

    @BindView(R.id.enter_amount)
    EditText enterAmount;
    @BindView(R.id.amount_paypal)
    Button amountPaypal;
    private static final String TAG = "paymentExample";
    @BindView(R.id.My_Balence)
    TextView MyBalence;
    private String Balence_Api="";
    private String stripe_token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_pap_pall_integration);
        ButterKnife.bind(this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Buy Call Credit");
        getEphemeral_key();

    }



    private void Verify_Payment(String toJSONObject) {
        try {

                hit_Call_Balance(toJSONObject, enterAmount.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getEphemeral_key() {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.GET, stripe_token, 548, this, parms, false, false, Params_Object);
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
        Intent intent = new Intent(this, CheckoutActivityJava.class);
        intent.putExtra("Membership_Price", enterAmount.getText().toString().trim());
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
                Verify_Payment(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                ToastUtils.longToast("Payment Failure");
            }
        }
    }//onActivityResult

    private void hitSendEmail(String invoice, String email) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","send_mail");
        parms.putString("email",email);
        parms.putString("text","Tax Invoice of TuDime CAN Credit Recharge"+"\n\n"+invoice);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, sendMailUrl,
                286, this, parms, false,false,Params_Object);
    }

   /* private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(enterAmount.getText().toString()), "USD", "Call Credit",
                paymentIntent);
    }*/

    @Override
    public void onDestroy() {

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
                        MyBalence.setText("$"+new DecimalFormat("##.##").format(Balence_Api));
                    }else
                    {
                        Balence_Api = data.getJSONObject(0).getString("plan_price");
                        if (Balence_Api.equalsIgnoreCase(""))
                        {
                            Balence_Api="0";
                        }
                        MyBalence.setText("$"+new DecimalFormat("##.##").format(Balence_Api));
                        enterAmount.setText("");
                    }





                } else {
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
