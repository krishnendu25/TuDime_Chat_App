package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import obj.quickblox.sample.chat.java.R;

public class AboutAndHelpSetting extends BaseActivity implements View.OnClickListener {
    TextView FAQ, terms, feedback, version_ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_and_help_setting);
        Instantiation();
        setversion();

    }

    private void Instantiation() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        FAQ = findViewById(R.id.FAQ);
        terms = findViewById(R.id.terms);
        feedback = findViewById(R.id.feedback);
        version_ss = findViewById(R.id.version);
        feedback.setOnClickListener(this);
        terms.setOnClickListener(this);
        FAQ.setOnClickListener(this);
    }
    private void setversion(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            version_ss.setText(getString(R.string.Version_no)+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.FAQ:
                startActivity(new Intent(this,ExtraFaqActivity.class));
                break;

            case R.id.feedback:
                startActivity(new Intent(this,FeedbackActivity.class));
                break;

            case R.id.terms:
                startActivity(new Intent(this,Terms_And_Conditions.class));
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
