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

import com.android.volley.VolleyError;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.Consts;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;

public class Email_Signup_Process extends BaseActivity implements View.OnClickListener {
    private EditText login_with_email,login_with_nickname;
    private Button btnAgree_email;


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

        /*String Email_Str = login_with_email.getText().toString().trim();
        String NickName_Str = login_with_nickname.getText().toString().trim();
        QBUser qbUser = new QBUser();
        qbUser.setLogin(Email_Str);
        qbUser.setFullName(NickName_Str);
        qbUser.setEmail(Email_Str);
        qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        signIn(qbUser);*/

        String OTP = Constant.generateRandomNumber();
        showProgressDialog(R.string.load);
        BackgroundMail.newBuilder(this)
                .withUsername("otptudime@gmail.com")
                .withPassword("Dirk2019")
                .withMailto(login_with_email.getText().toString())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("OTP Verification")
                .withBody("Your Email Verification OTP is: "+ OTP )
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.shortToast("Otp Sent Successfully");
                        hideProgressDialog();
                        SignUpVerificationActivity.User_Email = login_with_email.getText().toString().trim();
                        SignUpVerificationActivity.OTP = OTP;
                        SignUpVerificationActivity.Nick_Name =login_with_nickname.getText().toString();
                        startActivity(new Intent(getApplicationContext(), SignUpVerificationActivity.class));
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        ToastUtils.shortToast("Otp Sent UnSuccessfully");
                        hideProgressDialog();
                    }
                })
                .send();




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

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}
