package obj.quickblox.sample.chat.java.ui.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

public class Contact_Us extends BaseActivity implements IJSONParseListener {

    @BindView(R.id.name_ed)
    EditText nameEd;
    @BindView(R.id.email_ed)
    EditText emailEd;
    @BindView(R.id.phone_ed)
    EditText phoneEd;
    @BindView(R.id.address_ed)
    EditText addressEd;
    @BindView(R.id.contry_ed)
    EditText contryEd;
    @BindView(R.id.pincode_ed)
    EditText pincodeEd;
    @BindView(R.id.comments_ed)
    EditText commentsEd;
    @BindView(R.id.submit_btn)
    TextView submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__us);
        ButterKnife.bind(this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.Contact_Us);
    }

    @OnClick(R.id.submit_btn)
    public void onViewClicked() {
        if (nameEd.getText().toString().isEmpty())
        {
            ToastUtils.shortToast(R.string.enter_your_name);
            return;
        }else if (emailEd.getText().toString().isEmpty())
        {ToastUtils.shortToast(R.string.enter_mail);
            return;
        }else if (phoneEd.getText().toString().isEmpty())
        { ToastUtils.shortToast(R.string.ph_number);
            return;
        }else if (contryEd.getText().toString().isEmpty())
        {ToastUtils.shortToast(R.string.country_enter);
            return;
        }else if (commentsEd.getText().toString().isEmpty())
        {ToastUtils.shortToast(R.string.comments_enter);
            return;
        }else if(!isValidEmailId(emailEd.getText().toString().trim())){
            ToastUtils.shortToast(R.string.valid_mail);
            return;
        }else
        {
            hit_contact_us(nameEd.getText().toString(),
                    emailEd.getText().toString(),
                    phoneEd.getText().toString(),
                    "",
                    contryEd.getText().toString(),
                    "",
                    commentsEd.getText().toString());
        }

    }

    private void hit_contact_us(String nameEd, String emailEd, String phoneEd, String addressEd,
                                String contryEd, String pincodeEd, String commentsEd) {

        showProgressDialog(R.string.dlg_loading);
        String url = ApiConstants.contact_us;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task","contact_us");
        parms.putString("name",nameEd);
        parms.putString("email",emailEd);
        parms.putString("mobile_no",phoneEd);
        parms.putString("address",addressEd);
        parms.putString("country",contryEd);
        parms.putString("pincode",pincodeEd);
        parms.putString("comments",commentsEd);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, url,
                554, this, parms, false,false,Params_Object);
    }


    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode==554)
        {
            hideProgressDialog();

            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    ToastUtils.shortToast(R.string.Thank_You);
                     nameEd.setText("");
                     emailEd.setText("");
                     phoneEd.setText("");
                     addressEd.setText("");
                     contryEd.setText("");
                     pincodeEd.setText("");
                     commentsEd.setText("");
                     finish();
                }else
                {
                    ToastUtils.longToast(R.string.Request_Failed);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
