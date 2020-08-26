package obj.quickblox.sample.chat.java.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.ui.dialog.ProgressDialogFragment;
import obj.quickblox.sample.chat.java.utils.KeyboardUtils;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;

import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {
    private static final int SPLASH_DELAY = 4000;

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPrefsHelper.getInstance().set_LOGIN_SERVICE_STATUS("stop");
    hideActionbar();
        if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS() == null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            try {
                fillVersion();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SharedPrefsHelper.getInstance().hasQbUser()) {
                            try{
                                DashBoard.start(SplashActivity.this);
                            }catch (Exception e)
                            {DashBoard.start(SplashActivity.this);}
                        } else {
                            startActivity(new Intent(getApplicationContext(), SetLanguage.class));
                            finish();
                        }
                    }
                }, SPLASH_DELAY);
            } catch (Exception e) {
               
            }

        } else {

            if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS().equals("true")) {
                Show_Password_alert();


            } else if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS().equals("false")) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
                try {
                    fillVersion();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (SharedPrefsHelper.getInstance().hasQbUser()) {
                                try{
                                    DashBoard.start(SplashActivity.this);
                                    finish();
                                }catch (Exception e)
                                { }
                            } else {
                                startActivity(new Intent(getApplicationContext(), SetLanguage.class));
                                finish();
                            }
                        }
                    }, SPLASH_DELAY);
                } catch (Exception e) {
                   
                }
            }else if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS().equals(""))
            {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
                try {
                    fillVersion();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (SharedPrefsHelper.getInstance().hasQbUser()) {
                                //zrestoreChatSession();
                                try{
                                //    LoginService.start(SplashActivity.this, sharedPrefsHelper.getQbUser());
                                    DashBoard.start(SplashActivity.this);
                                    finish();
                                }catch (Exception e)
                                {
                                   
                                }
                            } else {
                                startActivity(new Intent(getApplicationContext(), SetLanguage.class));
                                finish();
                            }
                        }
                    }, SPLASH_DELAY);
                } catch (Exception e) {
                   
                }
            }


        }


    }

    private void Show_Password_alert() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.enter_password_to_open, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        EditText enter_password = dialogView.findViewById(R.id.enter_password);
        Button save = dialogView.findViewById(R.id.save);
        TextView forgot_password = dialogView.findViewById(R.id.forgot_password);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        enter_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==4)
                {
                    KeyboardUtils.hideKeyboard(enter_password);
                }

            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openforgotpassword();
            }
        });




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_password.getText().toString().length() != 4) {
                    ToastUtils.shortToast("Please enter atleast 4 Digits/Characters Password");
                    return;
                } else if (!enter_password.getText().toString().equals(SharedPrefsHelper.getInstance().get_PASSWORD())) {
                    ToastUtils.shortToast("Password Mismatch...");
                    return;
                } else {
                    alertDialog.dismiss();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    try {
                        fillVersion();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (SharedPrefsHelper.getInstance().hasQbUser()) {
                                    //zrestoreChatSession();
                                    try{
                                      //  LoginService.start(SplashActivity.this, sharedPrefsHelper.getQbUser());
                                        DashBoard.start(SplashActivity.this);
                                        finish();
                                    }catch (Exception e)
                                    {
                                       
                                    }

                                } else {
                                    startActivity(new Intent(getApplicationContext(), SetLanguage.class));
                                    finish();
                                }
                            }
                        }, SPLASH_DELAY);
                    } catch (Exception e) {
                       
                    }

                }
            }


        });


        alertDialog.show();
    }

    private void openforgotpassword()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forgot_password, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        EditText enter_security_answer = dialogView.findViewById(R.id.enter_security_answer);
        TextView my_seurity_qustion = dialogView.findViewById(R.id.my_seurity_qustion);
        Button save = dialogView.findViewById(R.id.save);

        LinearLayout password_section = dialogView.findViewById(R.id.password_section);
        EditText enter_password = dialogView.findViewById(R.id.enter_password);
        EditText confirm_password = dialogView.findViewById(R.id.confirm_password);

        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==4)
                {
                    KeyboardUtils.hideKeyboard(enter_password);

                }

            }
        });

      if (SharedPrefsHelper.getInstance().get_SECURITY_QUSTION()!=null)
      { my_seurity_qustion.setText(SharedPrefsHelper.getInstance().get_SECURITY_QUSTION()); }


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (password_section.getVisibility()==View.GONE){
                    try{
                        if (enter_security_answer.getText().toString().trim().equalsIgnoreCase(SharedPrefsHelper.getInstance().get_SECURITY_ANSWER()))
                        {
                            enter_security_answer.setEnabled(false);
                            enter_security_answer.setAlpha(0.5f);
                            password_section.setVisibility(View.VISIBLE);

                        }else
                        {
                            ToastUtils.longToast(R.string.Wrong_Security_answer);
                        }
                    }catch (Exception e)
                    {
                       
                    }
                }else
                {

                    if (enter_password.getText().toString().length() != 4) {
                        ToastUtils.shortToast(R.string.digite);
                        return;
                    } else if (!enter_password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
                        ToastUtils.shortToast(R.string.Mismatch);
                        return;
                    }else
                    {
                        SharedPrefsHelper.getInstance().set_PASSWORD(confirm_password.getText().toString().trim());
                        ToastUtils.longToast(R.string.YOUR_PASSWORD_SET_SUCCESSFULLY);
                        alertDialog.dismiss();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().hide();
                        }
                        try {
                            fillVersion();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (SharedPrefsHelper.getInstance().hasQbUser()) {
                                        try{
                                            DashBoard.start(SplashActivity.this);
                                            finish(); }catch (Exception e)
                                        { }

                                    } else {
                                        startActivity(new Intent(getApplicationContext(), SetLanguage.class));
                                        finish();
                                    }
                                }
                            }, SPLASH_DELAY);
                        } catch (Exception e) {
                           
                        }

                    }

                }




            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void fillVersion() {

        try{
            String appName = getString(R.string.app_name);
            ((TextView) findViewById(R.id.text_splash_app_title)).setText(appName);
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                ((TextView) findViewById(R.id.text_splash_app_version)).setText(versionName);
            } catch (PackageManager.NameNotFoundException e) {
                showErrorSnackbar(R.string.error, e, null);
            }
        }catch (Exception e)
        {
           
        }

    }

   /* private void restoreChatSession() {
        if (ChatHelper.getInstance().isLogged()) {
            try{
                LoginService.start(SplashActivity.this, sharedPrefsHelper.getQbUser());
                DashBoard.start(this);
            }catch (Exception e)
            {
               
            }

            finish();
        } else {
            QBUser currentUser = getUserFromSession();
            if (currentUser == null) {
                SignUpActivity.start(this);
                finish();
            } else {
                loginToChat(currentUser);
            }
        }
    }*/

    /*private QBUser getUserFromSession() {
        QBUser user = SharedPrefsHelper.getInstance().getQbUser();
        QBSessionManager qbSessionManager = QBSessionManager.getInstance();
        if (qbSessionManager.getSessionParameters() == null) {
            ChatHelper.getInstance().destroy();
            return null;
        }
        Integer userId = qbSessionManager.getSessionParameters().getUserId();
        user.setId(userId);
        return user;
    }*/

  /*  private void loginToChat(final QBUser user) {
        ProgressDialogFragment.show(getSupportFragmentManager(), R.string.dlg_restoring_chat_session);

        ChatHelper.getInstance().loginToChat(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                try {
                    Log.v(TAG, "Chat login onSuccess()");
                    ProgressDialogFragment.hide(getSupportFragmentManager());

                    try{
                        LoginService.start(SplashActivity.this, sharedPrefsHelper.getQbUser());
                        DashBoard.start(SplashActivity.this);
                    }catch (Exception e)
                    {
                       
                    }


                    finish();
                } catch (Exception e) {
                   
                }

            }

            @Override
            public void onError(QBResponseException e) {

                try {
                    if (e.getMessage().equals("You have already logged in chat")) {
                        loginToChat(user);
                    } else {
                        ProgressDialogFragment.hide(getSupportFragmentManager());
                        Log.w(TAG, "Chat login onError(): " + e);
                        showErrorSnackbar(R.string.error_recreate_session, e,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loginToChat(user);
                                    }
                                });
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }


            }
        });
    }*/



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