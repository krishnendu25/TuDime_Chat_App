package obj.quickblox.sample.chat.java.ui.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.util.NetworkChangeReceiver;
import obj.quickblox.sample.chat.java.util.QBResRequestExecutor;
import obj.quickblox.sample.chat.java.utils.ErrorUtils;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.util.Locale;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.SMS_URL;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_profile;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_profile_qb_reference;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_tudime_subscription;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.sendMailUrl;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.update_user_profile;

public abstract class BaseActivity extends AppCompatActivity implements IJSONParseListener {
    private static final int RESTART_DELAY = 200;
    private ProgressDialog progressDialog = null;
    protected SharedPrefsHelper sharedPrefsHelper;
    protected QBResRequestExecutor requestExecutor;
    protected ActionBar actionBar;
    NetworkChangeReceiver mNetworkReceiver;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        requestExecutor = App.getInstance().getQbResRequestExecutor();
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
    }
    public void initDefaultActionBar() {
        String currentUserFullName = "";
        if (sharedPrefsHelper.getQbUser() != null) {
            currentUserFullName = sharedPrefsHelper.getQbUser().getFullName();
        }
    }
    public void setActionBarTitle(int title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
    public void setActionsetSubtitle(int title) {
        if (actionBar != null) {
            actionBar.setSubtitle(title);
        }
    }

    public void hideActionbar()
    {
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void setActionBarTitle(CharSequence title) {
        if (actionBar != null) {
            Spannable text = new SpannableString(actionBar.getTitle());
            text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(text);
        }
    }

    public void setActionbarSubTitle(String subTitle) {
        if (actionBar != null)
            actionBar.setSubtitle(subTitle);
    }

    public void removeActionbarSubTitle() {
        if (actionBar != null)
            actionBar.setSubtitle(null);
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("dummy_value", 0);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void showErrorSnackbar(@StringRes int resId, Exception e, View.OnClickListener clickListener) {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            ErrorUtils.showSnackbar(rootView, resId, e,
                    R.string.dialog_retry, clickListener).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void SetLan(String Lan_Code)
    {
        Locale locale2 = new Locale(Lan_Code);
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getBaseContext().getResources().updateConfiguration(config2,getBaseContext().getResources().getDisplayMetrics());
    }

    protected void showProgressDialog(@StringRes Integer messageId) {

        if(!isFinishing()){
            try{
                try {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);

                        // Disable the back button
                        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                return keyCode == KeyEvent.KEYCODE_BACK;
                            }
                        };
                        progressDialog.setOnKeyListener(keyListener);
                    }
                    progressDialog.setMessage(getString(messageId));
                    if(!isFinishing()){
                        progressDialog.show();
                    }

                } catch (WindowManager.BadTokenException e) {
                }
            }catch (Exception e)
            {
            }
        }



    }

    protected void hideProgressDialog() {

        try{
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (WindowManager.BadTokenException e) {
            }
        }catch (Exception e)
        {
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        hideNotifications();
        if (SharedPrefsHelper.getInstance().hasQbUser() && !QBChatService.getInstance().isLoggedIn()) {
            ChatHelper.getInstance().loginToChat(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    onResumeFinished();
                }

                @Override
                public void onError(QBResponseException e) {
                    onResumeFinished();
                }
            });
        } else {
            onResumeFinished();
        }
    }

    private void hideNotifications() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    public void onResumeFinished() {
        // Need to Override onResumeFinished() method in nested classes if we need to handle returning from background in Activity
    }

    public void hideSoftKeypad(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(BaseActivity.class.getSimpleName(), "hideSoftKeypad()", e);
        }
    }
    //Check Internet Connection
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            //     e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }


    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    void Login_SignUP(String Identifier)
    {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","verify_otp");
        if (Identifier.contains("@"))
        {
            parms.putString("mobile_no",Identifier);
        }else
        {
            parms.putString("mobile_no",Identifier.replace("+",""));
        }

        parms.putString("otp","269304");
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, SMS_URL,
                193, this, parms, false,false,Params_Object);
    }

    void Update_Profile_Update(String userid,String name,String QB_User_id)
    {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("userid",userid);
        parms.putString("name",name);
        if (SharedPrefsHelper.getInstance().getProfilePhotostatus()!=null)
        {
            if (SharedPrefsHelper.getInstance().getProfilePhotostatus().equalsIgnoreCase(""))
            {
                parms.putString("privacy_status",getString(R.string.public_profile));
            }
        }
        parms.putString("QB_User_id",QB_User_id);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, update_user_profile,
                584, this, parms, false,false,Params_Object);
    }

    void Fetch_Profile_Update(String userid)
    {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid",userid);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_profile,
                447, this, parms, false,false,Params_Object);
    }
     void Fetch_Membership_Details(String userid) {

        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid",userid);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_tudime_subscription,
                897, this, parms, false,false,Params_Object);
    }
    void Fetch_Membership_Details(String userid,String app) {

        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid",userid);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_tudime_subscription,
                897, this, parms, false,false,Params_Object);
    }
    void Fetch_User_QB(String QB_User_id)
    {
        showProgressDialog(R.string.load);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("QB_User_id",QB_User_id);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_profile_qb_reference,
                208, this, parms, false,false,Params_Object);
    }

    public void hitSendMail(String trim, String OTP) {
        showProgressDialog(R.string.load);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","send_mail");
        parms.putString("email",trim);
        parms.putString("text",OTP);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, sendMailUrl,
                286, this, parms, false,false,Params_Object);
    }
}