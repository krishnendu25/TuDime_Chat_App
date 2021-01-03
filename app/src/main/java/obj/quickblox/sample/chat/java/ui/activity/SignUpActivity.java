package obj.quickblox.sample.chat.java.ui.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.util.AppSignatureHashHelper;
import obj.quickblox.sample.chat.java.util.MySMSBroadcastReceiver;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.SMS_URL;

public class SignUpActivity extends BaseActivity implements IJSONParseListener, View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int UNAUTHORIZED = 401;
    private EditText loginEt,login_with_nickname_ph;
  /*  private EditText usernameEt;*/
    private Button btnAgree;
    protected boolean isDelivered = false;
    protected boolean mIsSentSuccessful = false;
    private TextView txvCountry,txtCCode;
    public static String country_code,country_name;
    private BroadcastReceiver br,br_delever;
    private static final int RC_HINT = 1000;
    private GoogleApiClient mCredentialsApiClient;
    public static void start(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_login);
        hideActionbar();
        Instantiation();
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
    }

    private void Instantiation() {
        mCredentialsApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        loginEt = findViewById(R.id.login);
        /*usernameEt = findViewById(R.id.user_name);*/
        btnAgree = findViewById(R.id.btnAgree);
        login_with_nickname_ph = findViewById(R.id.login_with_nickname_ph);
        btnAgree.setOnClickListener(this);
        txvCountry =(TextView)findViewById(R.id.txvCountry);
        txvCountry.setOnClickListener(this);
        txtCCode = (TextView)findViewById(R.id.txtCCode);

        try{
            getphonenumber();
        }catch (Exception e){}

        try{
            String[] countriesArray = getApplicationContext().getResources().getStringArray(R.array.CountryCodes);
            List<String> countriesListComplete  = Arrays.asList(countriesArray);
            for (int i=0;i<countriesListComplete.size();i++)
            {
                String[] countryInfo   =  (String[]) countriesListComplete.get(i).split(",");
                String countryname = GetCountryZipCode(countryInfo[1]).trim();
                try {
                    Locale l = new Locale("", Objects.requireNonNull(getUserCountry(this)));
                    if (countryname.equalsIgnoreCase(l.getDisplayCountry()))
                    {
                        country_code="+" + countryInfo[0];
                        country_name=countryname;
                        String pngName = countryInfo[1].trim().toLowerCase();
                    }
                }catch (Exception e){}
            }
        }catch (Exception ex)
        {

        }

        try {
             if (country_name!=null){
                if (!country_name.equalsIgnoreCase("")){
                    txvCountry.setText(country_name);
                }
            }
        }catch (Exception e)
        {


        }

        ImageView back_button_icon = findViewById(R.id.back_button);
        back_button_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    private void getphonenumber() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0, new Bundle());
        } catch (Exception e) {
            Log.e("Login", "Could not start hint picker Intent", e);
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    if (credential.getId().length() > 10) {
                        try {
                            String phon = credential.getId().trim().replaceAll(" ","");
                            loginEt.setText(phon.substring(phon.length() - 10));
                        }catch (Exception e)
                        {loginEt.setText(credential.getId().substring(credential.getId().length() - 10));}
                    } else {loginEt.setText(credential.getId()); }
                } else {
                    Toast.makeText(this, "err", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }
    private void SendOTP()
    {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","send_otp");
        parms.putString("mobile_no",country_code+loginEt.getText().toString().trim());
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, SMS_URL,
                875, this, parms, false,false,Params_Object);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.txvCountry:
               startActivity(new Intent(getApplicationContext(),ShowCountryList.class));
                break;

            case R.id.btnAgree:
                if (loginEt.getText().toString().equalsIgnoreCase("") || loginEt.getText().toString().isEmpty()) {
                    loginEt.setError("Please enter your phone number");
                    loginEt.requestFocus();
                    return; }
              else  if (login_with_nickname_ph.getText().toString().equalsIgnoreCase("") || login_with_nickname_ph.getText().toString().isEmpty()) {
                    login_with_nickname_ph.setError("Please enter your nickname");
                    login_with_nickname_ph.requestFocus();
                    return;}else
                {
                    SendOTP();
                }

                break;
        }
    }



    public void onStop() {
        super.onStop();
        try {
            if (br!=null)
            unregisterReceiver(this.br);
            if (br_delever!=null)
            unregisterReceiver(this.br_delever);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* access modifiers changed from: protected */
    public void onPause() {
       /* LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mLocalBroadcastSmsReceiver);*/
        super.onPause();
    }


    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        if (error instanceof NoConnectionError) {
            Constant.showErrorAlert(this, "Connection Timed Out");
        } else if (error instanceof AuthFailureError) {
            //TODO
            Constant.showErrorAlert(this, "Authorization failure");
        } else if (error instanceof ServerError) {
            //TODO
            Constant.showErrorAlert(this, "Server Timed out");
        } else if (error instanceof NetworkError) {
            //TODO
            Constant.showErrorAlert(this, "Network Error");
        }

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode==875)
        {hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    hideProgressDialog();
                    SharedPrefsHelper.getInstance().setCountryCode(country_code);
                    ToastUtils.shortToast(response.getString("success_message"));
                    SignUpVerificationActivity.User_PhoneNo = loginEt.getText().toString().trim();
                    SignUpVerificationActivity.country_code = country_code;
                    SignUpVerificationActivity.OTP = response.getString("data");
                    SignUpVerificationActivity.Nick_Name =login_with_nickname_ph.getText().toString();
                    startActivity(new Intent(getApplicationContext(), SignUpVerificationActivity.class));

                }else
                { hideProgressDialog();
                    ToastUtils.shortToast(response.getString("error_message"));
                }
            } catch (Exception e) { hideProgressDialog();
                e.printStackTrace();
            }
        }


    }

    public void onResume() {
        super.onResume();
      /*  LocalBroadcastManager.getInstance(this).registerReceiver(this.mLocalBroadcastSmsReceiver, new IntentFilter(AppConstants.LOCAL_BROADCAST_SMS));*/
        try {
            txtCCode.setText(country_code);
            if (country_name!=null){
                if (!country_name.equalsIgnoreCase("")){
                    txvCountry.setText(country_name);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toUpperCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }
    private String GetCountryZipCode(String ssid) {
        Locale loc = new Locale("", ssid);

        return loc.getDisplayCountry().trim();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}