package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import obj.quickblox.sample.chat.java.R;

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
}
