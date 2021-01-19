package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;

public class InitialTermsActivity extends BaseActivity {
    TextView txvInfo2;
    Button btnAgree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_initial_terms);

        hideActionbar();
        ImageView back_button_icon = findViewById(R.id.back_button);
        back_button_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.txvInfo2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialTermsActivity.this , Terms_And_Conditions.class));
            }
        });

        findViewById(R.id.txvInfo1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialTermsActivity.this , Terms_And_Conditions.class));
            }
        });


        findViewById(R.id.btnAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitialTermsActivity.this , Choose_Sign_Up_type.class));
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


