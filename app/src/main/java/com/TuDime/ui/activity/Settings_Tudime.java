package com.TuDime.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.KeyboardUtils;
import com.TuDime.utils.ToastUtils;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import com.TuDime.R;

public class Settings_Tudime extends BaseActivity implements View.OnClickListener {

    private TextView edit_profile_tv,accounts_tv,notifications_tv,about_amp_help_tv,contact_us;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__tudime);
        Instantiation();






    }

    private void Instantiation() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        edit_profile_tv = findViewById(R.id.edit_profile_tv);
        accounts_tv = findViewById(R.id.accounts_tv);
        contact_us = findViewById(R.id.contact_us);
        notifications_tv = findViewById(R.id.notifications_tv);
        about_amp_help_tv = findViewById(R.id.about_amp_help_tv);
        edit_profile_tv.setOnClickListener(this);
        accounts_tv.setOnClickListener(this);
        notifications_tv.setOnClickListener(this);
        about_amp_help_tv.setOnClickListener(this);
        contact_us.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.edit_profile_tv:
                startActivity(new Intent(this, UpdateProfileActivity.class));
                break;

            case R.id.accounts_tv:
                startActivity(new Intent(this, AccountSettingsActivity.class));
                break;
            case R.id.contact_us:
                startActivity(new Intent(this, Contact_Us.class));
                break;
            case R.id.notifications_tv:
                startActivity(new Intent(this, NotificationsSettingsActivity.class));
                break;

            case R.id.about_amp_help_tv:
                startActivity(new Intent(this,AboutAndHelpSetting.class));
                break;

        }

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

    public void gotoChatBackUp(View view) {
        startActivity(new Intent(this,Cloud_Backup.class));
    }

    public void gotoHiddenChat(View view) {
        try{
            if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS().equals("true")){
                Show_Password_alert();
            }else{
                startActivity(new Intent(Settings_Tudime.this, Archive_Chat.class));
            }
        }catch (Exception e){
            startActivity(new Intent(Settings_Tudime.this, Archive_Chat.class));
        }
    }

    private void Show_Password_alert() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings_Tudime.this);
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
                    alertDialog.hide();
                    startActivity(new Intent(Settings_Tudime.this, Archive_Chat.class));
                }
            }


        });


        alertDialog.show();
    }
    private void openforgotpassword()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Settings_Tudime.this);
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
                        startActivity(new Intent(Settings_Tudime.this, Archive_Chat.class));
                    }

                }
            }
        });
        alertDialog.show();
    }
}
