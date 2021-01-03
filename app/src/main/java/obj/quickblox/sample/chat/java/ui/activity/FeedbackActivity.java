package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.sendMailUrl;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener, IJSONParseListener {

    EditText edt_feedback;
    Button submit_feedback;
    String Loginid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Instantiation();
    }

    private void Instantiation() {
        edt_feedback = findViewById(R.id.edt_feedback);
        submit_feedback = findViewById(R.id.submit_feedback);
        submit_feedback.setOnClickListener(this);
        try {
            Loginid = SharedPrefsHelper.getInstance().getQbUser().getLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_feedback:
            if (edt_feedback.getText().toString().isEmpty())
            {
                Toast.makeText(this, R.string.feedback_,Toast.LENGTH_LONG).show();
            }else
            {
                hit_feedback(edt_feedback.getText().toString());
            }
            break;
        }
    }

    private void hit_feedback(String toString)
    {
        showProgressDialog(R.string.dlg_loading);
        String url = ApiConstants.BASE_URL1;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("rule","feedback");
        parms.putString("message",toString);
        parms.putString("name", SharedPrefsHelper.getInstance().getQbUser().getFullName());
        MyVolley.init(this);
      mResponse.getResponse(Request.Method.POST, url,
                965, this, parms, false,false,Params_Object);


    }

    private void hitSendEmail(String feedBack) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","send_mail");
        parms.putString("email","InsuranceHunter@aol.com");
        parms.putString("FeedBack","User Name-->"+ SharedPrefsHelper.getInstance().getUserName()+"\n"+
                        "User Login ID-->"+Loginid+"\n"+"FeedBack--> "+
                feedBack);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, sendMailUrl,
                286, this, parms, false,false,Params_Object);
    }



    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        hideProgressDialog();
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        if (requestCode==965)
        {

            try {
                if (response.getString("status").equals("1")) {
                    hitSendEmail(edt_feedback.getText().toString().trim());
                    Toast.makeText(FeedbackActivity.this, "" + getString(R.string.feedback_received), Toast.LENGTH_SHORT).show();

                } else {
                    hideProgressDialog();
                }
            } catch (JSONException e) {
                hideProgressDialog();
                e.printStackTrace();
            }
        }if (requestCode==286) {
            try{
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    finish();
                    hideProgressDialog();
                    Toast.makeText(FeedbackActivity.this, "" + getString(R.string.feedback_received), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e)
            {
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
