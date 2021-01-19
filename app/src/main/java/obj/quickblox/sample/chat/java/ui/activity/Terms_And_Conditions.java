package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;

public class Terms_And_Conditions extends BaseActivity implements IJSONParseListener {
    private WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_terms__and__conditions);
        Intial();


    }

    private void Intial()
    {


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button_icon);
        getSupportActionBar().setTitle(getString(R.string.terms10));

        web = (WebView)findViewById(R.id.webview);
        // Enable Javascript
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);



        try{
            web.loadUrl("file:///android_asset/terms_condition.html");
            //hitApi();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void hitApi()
    {
        showProgressDialog(R.string.dlg_loading);

        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle params = new Bundle();
        if ((SharedPrefsHelper.getInstance().get_Language()).equals("es")) {
            params.putString("language", "spanish");
        } else if ((SharedPrefsHelper.getInstance().get_Language()).equals("en")) {
            params.putString("language", "english");
        } else if ((SharedPrefsHelper.getInstance().get_Language()).equals("hi")) {
            params.putString("language", "hindi");
        }
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, ApiConstants.BASE_URL1 + "rule=terms_webview",
                454, this, params, false,false,Agent_Array_Object);



    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject jsonObject, int requestCode) {

        if (requestCode==454)
        {
            hideProgressDialog();
            try {

                if (jsonObject.getString("status").equals("1")) {
                    web.loadUrl(jsonObject.getString("URL"));
                } else {
                }
            } catch (Exception e) {
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
