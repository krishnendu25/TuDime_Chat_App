package com.TuDime.ui.activity;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import com.TuDime.R;
import com.TuDime.services.LoginService;
import com.TuDime.Prefrences.SharedPrefsHelper;

public class Choose_Sign_Up_type extends BaseActivity implements View.OnClickListener {

    private ImageView Select_Phone_to,Select_Email_to;

    public static void start(Context context) {
        Intent intent = new Intent(context, Choose_Sign_Up_type.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_choose__sign__up_type);

        hideActionbar();
        Initialization();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
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
    private void Initialization()
    {
        Select_Phone_to =(ImageView)findViewById(R.id.Select_Phone_to);
        Select_Email_to =(ImageView)findViewById(R.id.Select_Email_to);
        Select_Phone_to.setOnClickListener(this);
        Select_Email_to.setOnClickListener(this);
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
            case R.id.Select_Email_to:
                startActivity(new Intent(getApplicationContext(),Email_Signup_Process.class));

                break;


            case R.id.Select_Phone_to:
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            LoginService.start(getApplicationContext(), sharedPrefsHelper.getQbUser());
        } catch (Exception e) {
        }
    }
}
