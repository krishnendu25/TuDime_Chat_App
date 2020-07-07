package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.Prefrences.CiaoPrefrences;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;

public class EcardsWebView   extends BaseActivity implements IJSONParseListener {
    private WebView web;
    private CiaoPrefrences prefrences;
    private String url_ecards = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_ecards_web_view);
        web = (WebView)findViewById(R.id.webview_ecards);
        prefrences = CiaoPrefrences.getInstance(EcardsWebView.this);

        hideActionbar();
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        try {
            if ((getIntent().getStringExtra("type")).equals("simple")) {
                hitEcardsApi();
            } else if ((getIntent().getStringExtra("type")).equals("custom")) {
                findViewById(R.id.btn_persoanlize).setVisibility(View.GONE);
                web.loadUrl(getIntent().getStringExtra("url"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
//        web.loadUrl("http://ritaraapps.com/cards45/index.html");

        findViewById(R.id.remove_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btn_persoanlize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EcardsWebView.this , Ecards_EditPage.class);
                i.putExtra("category" , getIntent().getStringExtra("category"));
                i.putExtra("card_id" , getIntent().getStringExtra("card_id"));
                startActivity(i);
//                startActivity(new Intent(EcardsWebView.this , Ecards_EditPage.class));
            }
        });

        try {
            web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CiaoPrefrences.getInstance(EcardsWebView.this).getEcardsUrl()){
            finish();
        }
        if(CiaoPrefrences.getInstance(EcardsWebView.this).getTudimeSendCardStatus()){
            finish();
        }
    }

    public void hitEcardsApi() {

        showProgressDialog(R.string.load);
        String url = ApiConstants.BASE_URL1 + "rule=big_cards";
        JSONObject data;
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle params = new Bundle();
        params.putString("category" , getIntent().getStringExtra("category"));
        params.putString("card_id" ,getIntent().getStringExtra("card_id") );
        if ((SharedPrefsHelper.getInstance().get_Language()).equals("es")) {
            params.putString("language", "spanish");
        } else if ((SharedPrefsHelper.getInstance().get_Language()).equals("en")) {
            params.putString("language", "english");
        } else if ((SharedPrefsHelper.getInstance().get_Language()).equals("hi")) {
            params.putString("language", "hindi");
        }
        data = new JSONObject();
        MyVolley.init(this);
        try {
            mResponse.getResponse(Request.Method.POST, url,
                    454, this, params, false, false, data);
        } catch (Exception e) {
            e.printStackTrace();
        }






    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        if (requestCode==454)
        {
            try {
               hideProgressDialog();

                if(response.getString("Status").equals("1")) {
                    url_ecards =  response.getString("message");
                    web.loadUrl(response.getString("message"));
                }
            } catch (Exception e) {
                hideProgressDialog();
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
