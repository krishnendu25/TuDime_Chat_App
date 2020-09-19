package obj.quickblox.sample.chat.java.ui.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.util.ChatPingAlarmManager;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.Consts;
import obj.quickblox.sample.chat.java.utils.SettingsUtil;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import obj.quickblox.sample.chat.java.utils.WebRtcSessionManager;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;

import com.quickblox.core.helper.Utils;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.SMS_URL;

public class SignUpVerificationActivity extends BaseActivity implements View.OnClickListener, IJSONParseListener {
    private static final int UNAUTHORIZED = 401;
    public static String User_PhoneNo,OTP, country_code, Nick_Name,User_Email;
    private TextView txvNum, txt_send_code_again;
    private EditText etVerificationCode;
    private Button btnDone;
    private String last2Digit = ".com";
    private SmsVerifyCatcher smsVerifyCatcher;
    QBUser user_api;
    private String OTP_re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_phone__no__verification);
        Initialization();
        hideActionbar();
        if (!StringUtils.isNullOrEmpty(User_PhoneNo)) {
            last2Digit = User_PhoneNo.substring(User_PhoneNo.length() - 2);
        }


        txvNum.setText("xxx xxx xx" + last2Digit);
        etVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    VerifyOTP(etVerificationCode.getText().toString().trim());
                }

            }
        });
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                etVerificationCode.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });

    }

    private void VerifyOTP(String trim)
    {
        showProgressDialog(R.string.dlg_loading);
        if (trim.equalsIgnoreCase(OTP)) {
            ToastUtils.shortToast("Otp Verified Successfully");
            if (User_PhoneNo == null) {
                QBUser qbUser = new QBUser();
                qbUser.setLogin(User_Email);
                qbUser.setEmail(User_Email);
                qbUser.setFullName(Nick_Name);
                qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
                signIn(qbUser);
            } else if (User_Email == null) {
                QBUser qbUser = new QBUser();
                qbUser.setLogin(country_code + User_PhoneNo);
                qbUser.setPhone(country_code + User_PhoneNo);
                qbUser.setFullName(Nick_Name);
                qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
                signIn(qbUser);
            }

        }else
        {
            hideProgressDialog();
            ToastUtils.shortToast("Otp Was Not Valid");
        }
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private void Initialization() {
        txvNum = (TextView) findViewById(R.id.txvNum);
        txt_send_code_again = (TextView) findViewById(R.id.txt_send_code_again);
        txt_send_code_again.setOnClickListener(this);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_send_code_again:
                showProgressDialog(R.string.dlg_loading);
                SendOTPagain();
                return;
            case R.id.btnDone:


                if(etVerificationCode.getText().toString().isEmpty())
                { ToastUtils.longToast("Enter Your OTP"); return;
                }else if (etVerificationCode.getText().toString().length()!=6)
                {ToastUtils.longToast("OTP minimum length is 6");return;
                }else
                { VerifyOTP(etVerificationCode.getText().toString().trim()); }

                return;
        }
    }

    private void SendOTPagain() {

        if (User_Email.equalsIgnoreCase("")||User_Email==null)
        {
            showProgressDialog(R.string.dlg_loading);
            JSONObject Params_Object = new JSONObject();
            JSONRequestResponse mResponse = new JSONRequestResponse(this);
            Bundle parms = new Bundle();
            parms.putString("task","send_otp");
            parms.putString("mobile_no",country_code+User_PhoneNo);
            MyVolley.init(this);
            mResponse.getResponse(Request.Method.POST, SMS_URL,
                    875, this, parms, false,false,Params_Object);
        }else
        {
            OTP_re = Constant.generateRandomNumber();
            hitSendMail(User_Email,OTP_re);
        }

    }





    private void signIn(final QBUser user) {
        showProgressDialog(R.string.dlg_login);
        ChatHelper.getInstance().login(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser userFromRest, Bundle bundle) {
                if (userFromRest.getLogin().equals(user.getLogin())) {
                    loginToChat(user);
                } else {
                    //Need to set password NULL, because server will update user only with NULL password
                    user.setPassword(null);
                    updateUser(user);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                if (e.getHttpStatusCode() == UNAUTHORIZED) {
                    signUp(user);
                } else {
                    hideProgressDialog();
                    showErrorSnackbar(R.string.login_chat_login_error, e, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signIn(user);
                        }
                    });
                }
            }
        });
    }

    private void updateUser(final QBUser user) {
        ChatHelper.getInstance().updateUser(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                loginToChat(user);
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                showErrorSnackbar(R.string.login_chat_login_error, e, null);
            }
        });
    }

    private void loginToChat(final QBUser user) {
        user_api=user;
        user.setPassword(App.USER_DEFAULT_PASSWORD);
        ChatHelper.getInstance().loginToChat(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                if (User_PhoneNo == null) {
                    Login_SignUP(User_Email);
                } else if (User_Email == null) {
                    Login_SignUP(country_code + User_PhoneNo);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                showErrorSnackbar(R.string.login_chat_login_error, e, null);
            }

        });
    }

    private void signUp(final QBUser newUser) {
        SharedPrefsHelper.getInstance().removeQbUser();
        QBUsers.signUp(newUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                hideProgressDialog();
                signIn(newUser);
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                showErrorSnackbar(R.string.login_sign_up_error, e, null);
            }
        });
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        hideProgressDialog();
        if (error instanceof NoConnectionError) {
            if (!isFinishing()) {
                Constant.showErrorAlert(this, "Communication timed out");
            }

        } else if (error instanceof AuthFailureError) {
            //TODO
            if (!isFinishing()) {
                Constant.showErrorAlert(this, "Authorization failure");
            }

        } else if (error instanceof ServerError) {
            //TODO
            if (!isFinishing()) {
                Constant.showErrorAlert(this, "The server timed out");
            }

        } else if (error instanceof NetworkError) {
            //TODO
            if (!isFinishing()) {
                Constant.showErrorAlert(this, "Network error");
            }

        }
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode==875)
        {
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    hideProgressDialog();
                    ToastUtils.shortToast(response.getString("success_message"));
                    this.OTP = response.getString("data");
                }else
                {hideProgressDialog();
                    ToastUtils.shortToast(response.getString("error_message"));
                }
            } catch (Exception e) {hideProgressDialog();
                e.printStackTrace();
            }
        }
        if (requestCode==193)
        {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    String USERID = response.getJSONObject("data").getString("id");

                    SharedPrefsHelper.getInstance().setPhoneNum(user_api.getPhone());
                    SharedPrefsHelper.getInstance().setUSERID(USERID);
                    SharedPrefsHelper.getInstance().saveQbUser(user_api);
                    SharedPrefsHelper.getInstance().setUserName(Nick_Name);

                    DashBoard.User_PhoneNo = User_PhoneNo;
                    Intent tempIntent = new Intent(SignUpVerificationActivity.this, LoginService.class);
                    PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
                    LoginService.start(SignUpVerificationActivity.this, user_api, pendingIntent);
                    DashBoard.start(SignUpVerificationActivity.this);
                    finish();
                }else
                {ToastUtils.shortToast(response.getString("error_message ")); }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode==286) {
            ToastUtils.shortToast("Otp Sent Successfully");
            hideProgressDialog();
            SignUpVerificationActivity.OTP = OTP_re;
            hideProgressDialog();
        }

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        if (requestCode==286) {
            ToastUtils.shortToast("Otp Sent Successfully");
            hideProgressDialog();
            SignUpVerificationActivity.OTP = OTP_re;
            hideProgressDialog();
        }
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
        if (requestCode==286) {
            ToastUtils.shortToast("Otp Sent Successfully");
            hideProgressDialog();
            SignUpVerificationActivity.OTP = OTP_re;
            hideProgressDialog();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }
}