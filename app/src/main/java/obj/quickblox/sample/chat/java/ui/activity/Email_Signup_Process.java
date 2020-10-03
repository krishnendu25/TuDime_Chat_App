package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.Consts;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_profile_qb_reference;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.sendMailUrl;

public class Email_Signup_Process extends BaseActivity implements View.OnClickListener {
    private EditText login_with_email,login_with_nickname;
    private Button btnAgree_email;
    private String OTP_AllRa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_email__signup__process);
        hideActionbar();
        Instantiation();






    }

    private void Instantiation()
    {
        login_with_email = (EditText)findViewById(R.id.login_with_email);
        login_with_nickname = (EditText)findViewById(R.id.login_with_nickname);
        btnAgree_email = (Button)findViewById(R.id.btnAgree_email);
        btnAgree_email.setOnClickListener(this);
        ImageView back_button_icon = findViewById(R.id.back_button);
        back_button_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnAgree_email:

                Sign_Up();

                break;
        }

    }

    private void Sign_Up()
    {
        if (login_with_email.getText().toString().equalsIgnoreCase("") || login_with_email.getText().toString().isEmpty()) {
            login_with_email.setError(getString(R.string.entermail));
            login_with_email.requestFocus();
            return;
        }

        if (login_with_nickname.getText().toString().equalsIgnoreCase("") || login_with_nickname.getText().toString().isEmpty()) {
            login_with_nickname.setError(getString(R.string.name_cannot_be_empty));
            login_with_nickname.requestFocus();

            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(login_with_email.getText().toString()).matches()) {
            login_with_email.setError(getString(R.string.valid_mail));
            login_with_email.requestFocus();
            login_with_email.requestFocus();
            return;
        }

        OTP_AllRa = Constant.generateRandomNumber();
        hitSendMail(login_with_email.getText().toString().trim(),OTP_AllRa);


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
                if (e.getHttpStatusCode() == 401) {
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

        //Need to set password, because the server will not register to chat without password
        user.setPassword(App.USER_DEFAULT_PASSWORD);
        ChatHelper.getInstance().loginToChat(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d("Abc", "ChatService Login Successful");
                SharedPrefsHelper.getInstance().saveQbUser(user);
                DashBoard.User_Email=user.getEmail();
                SharedPrefsHelper.getInstance().setUserName(login_with_nickname.getText().toString());
                Intent tempIntent = new Intent(Email_Signup_Process.this, LoginService.class);
                PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
                LoginService.start(Email_Signup_Process.this, user, pendingIntent);
                DashBoard.start(Email_Signup_Process.this);
                finish();
                hideProgressDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                showErrorSnackbar(R.string.login_chat_login_error, e, null);
            }
        });
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        hideProgressDialog();
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode==286) {
            try{
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    hideProgressDialog();
                    ToastUtils.longToast("OTP mail Sent Successfully in Yout Email");
                    hideProgressDialog();
                    SignUpVerificationActivity.User_Email = login_with_email.getText().toString().trim();
                    SignUpVerificationActivity.OTP = OTP_AllRa;
                    SignUpVerificationActivity.Nick_Name =login_with_nickname.getText().toString();
                    startActivity(new Intent(getApplicationContext(), SignUpVerificationActivity.class));
                }else
                {
                    hideProgressDialog();
                    ToastUtils.longToast(response.getString("error_message"));
                }

            }catch (Exception e)
            {
                hideProgressDialog();
                ToastUtils.longToast("Some Error Occurred");
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
