package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

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

public class InitialTermsActivity extends BaseActivity implements IJSONParseListener {
    private FileOutputStream file_themes;
    private ArrayList<String > array_themes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());

        setContentView(R.layout.activity_initial_terms);
        hideActionbar();
        findViewById(R.id.txvInfo2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialTermsActivity.this , Terms_And_Conditions.class));
            }
        });

        findViewById(R.id.btnAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                findViewById(R.id.rel_terms).setVisibility(View.GONE);
                showDialogAccessContacts();
                hitThemesApi();
            }
        });

    }


    private void showDialogAccessContacts() {
        View v = getLayoutInflater().inflate(R.layout.access_contacts_dialog, (ViewGroup) findViewById(R.id.dislog_contacts), false);
        final Dialog dialog = new Dialog(InitialTermsActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(v);
        dialog.show();

        v.findViewById(R.id.allow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showDialogCameraAccess();
            }
        });
        v.findViewById(R.id.deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//				finishAffinity();
                dialog.dismiss();
                showDialogCameraAccess();
            }
        });
    }

    private void showDialogCameraAccess() {
        View v = getLayoutInflater().inflate(R.layout.access_camera_dialog, (ViewGroup) findViewById(R.id.dislog_contacts), false);
        final Dialog dialog = new Dialog(InitialTermsActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(v);
        dialog.show();

        v.findViewById(R.id.allow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showDialogGalleryAccess();
            }
        });
        v.findViewById(R.id.deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//				finishAffinity();
                dialog.dismiss();
                showDialogGalleryAccess();
            }
        });
    }

    private void showDialogGalleryAccess() {
        View v = getLayoutInflater().inflate(R.layout.access_gallery_dialog, (ViewGroup) findViewById(R.id.dislog_contacts), false);
        final Dialog dialog = new Dialog(InitialTermsActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(v);
        dialog.show();

        v.findViewById(R.id.allow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InitialTermsActivity.this, Choose_Sign_Up_type.class);
                startActivity(i);
                dialog.dismiss();
                finish();

            }
        });
        v.findViewById(R.id.deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//				finishAffinity();
                Intent i = new Intent(InitialTermsActivity.this, SignUpActivity.class);
                startActivity(i);
                dialog.dismiss();
                finish();
            }
        });
    }

    private void hitThemesApi(){
        String url = ApiConstants.BASE_URL1 + "rule=app_themes";

        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.GET, url,
                456, this, parms, false,false,Agent_Array_Object);



    }

    private void downloadFiles(String image_url, int i){
        File path = new File(Environment.getExternalStorageDirectory(), ".Tudime_themes");
        if (!path.exists())
            path.mkdirs();

        try {
            URL u = new URL(image_url);
            DataInputStream dis = new DataInputStream(u.openStream());
            byte[] buffer = new byte[8192];
            int length;
            FileOutputStream fos = new FileOutputStream(new File(path, "themes_" + i + ".png"));
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject jsonObject, int requestCode) {
        if (requestCode == 456) {

            try {  array_themes = new ArrayList<>();

                if(jsonObject.getString("status").equals("1")){
                    final JSONArray array = jsonObject.getJSONArray("message");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    downloadFiles(object.getString("images") , i+1);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
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


