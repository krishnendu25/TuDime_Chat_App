package com.TuDime.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.stripe.android.Stripe;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.constants.AppConstants;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

import static com.TuDime.constants.ApiConstants.payforsubscrption;


public class CheckoutActivityJava extends BaseActivity {
    String number;
    String exp_month;
    String exp_year;
    String cvc;
    String name;
    String line1;
    String postal_code;
    String city;
    String state;
    String country;
    String amount;
    @BindView(R.id.payButton)
    Button payButton;
    @BindView(R.id.enter_email)
    EditText enterEmail;
    @BindView(R.id.cardInputWidget)
    CardMultilineWidget cardInputWidget;
    private String Membership_Price = "", stripe_token = "";
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_java);
        ButterKnife.bind(this);
        getValuesIntent();
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(AppConstants.Stripe_Publishable_key)
        );
    }

    private void getValuesIntent() {
        Membership_Price = getIntent().getStringExtra("Membership_Price");
        stripe_token = getIntent().getStringExtra("stripe_token");
    }

    @OnClick(R.id.payButton)
    public void onViewClicked() {
        try {
            if (cardInputWidget.getCard().getNumber() == null || cardInputWidget.getCard().getExpMonth() == null || cardInputWidget.getCard().getExpYear() == null || cardInputWidget.getCard().getCvc() == null) {
                ToastUtils.shortToast("Enter A All Payment Related Details");
                return;
            } else if (cardInputWidget.getCard().getNumber().toString().equalsIgnoreCase("null") || cardInputWidget.getCard().getNumber().toString().equalsIgnoreCase("") ||
                    cardInputWidget.getCard().getExpMonth().toString().equalsIgnoreCase("null") || cardInputWidget.getCard().getExpMonth().toString().equalsIgnoreCase("") ||
                    cardInputWidget.getCard().getExpYear().toString().equalsIgnoreCase("null") || cardInputWidget.getCard().getExpYear().toString().equalsIgnoreCase("") ||
                    cardInputWidget.getCard().getCvc().toString().equalsIgnoreCase("null") || cardInputWidget.getCard().getCvc().toString().equalsIgnoreCase("")) {
                ToastUtils.shortToast("Enter A All Payment Related Details");
                return;
            } else if (enterEmail.getText().toString().trim().equalsIgnoreCase("")) {
                ToastUtils.shortToast("Enter A Email Address");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(enterEmail.getText().toString()).matches()) {
                ToastUtils.shortToast("Enter A Valid Email Address");
                return;
            } else {
                number = cardInputWidget.getCard().getNumber() == null ? "" : cardInputWidget.getCard().getNumber().toString();
                exp_month = cardInputWidget.getCard().getExpMonth() == null ? "" : cardInputWidget.getCard().getExpMonth().toString();
                exp_year = cardInputWidget.getCard().getExpYear() == null ? "" : cardInputWidget.getCard().getExpYear().toString();
                cvc = cardInputWidget.getCard().getCvc() == null ? "" : cardInputWidget.getCard().getCvc().toString();
                name = SharedPrefsHelper.getInstance().getUserName();
                line1 = cardInputWidget.getCard().getAddressLine1() == null ? "" : cardInputWidget.getCard().getAddressLine1().toString();
                postal_code = cardInputWidget.getCard().getAddressZip() == null ? "" : cardInputWidget.getCard().getAddressZip().toString();
                city = cardInputWidget.getCard().getAddressCity() == null ? "" : cardInputWidget.getCard().getAddressCity().toString();
                state = cardInputWidget.getCard().getAddressState() == null ? "" : cardInputWidget.getCard().getAddressState().toString();
                country = cardInputWidget.getCard().getCountry() == null ? "" : cardInputWidget.getCard().getCountry().toString();
                amount = Membership_Price;
                hitPaymentApi();
            }
        } catch (Exception e) {
            ToastUtils.shortToast("Please Enter All The Details");
        }


    }

    private void hitPaymentApi() {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("number", number);
        parms.putString("exp_month", exp_month);
        parms.putString("exp_year", exp_year);
        parms.putString("cvc", cvc);
        parms.putString("name", name);
        parms.putString("line1", "510 Townsend St");
        parms.putString("postal_code", postal_code);
        parms.putString("city", "sanfransico");
        parms.putString("state", "ca");
        parms.putString("country", "US");
        parms.putString("amount", amount);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, payforsubscrption,
                972, this, parms, false, false, Params_Object);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == 972) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    Intent intent = new Intent();
                    intent.putExtra("invoice", response.getJSONObject("data").getString("receipt_url"));
                    intent.putExtra("email", enterEmail.getText().toString().trim());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    ToastUtils.shortToast(response.getString("success_message"));
                } else {
                    ToastUtils.shortToast(response.getString("error_message"));
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
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