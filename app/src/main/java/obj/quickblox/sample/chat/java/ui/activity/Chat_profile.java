package obj.quickblox.sample.chat.java.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import obj.quickblox.sample.chat.java.R;

public class Chat_profile extends BaseActivity {

    TextView User_Name_tv, show_status_tv, show_phone_number, show_Email_number;
    ImageView profile_picture_iv;
    String User_Name, User_Login, User_Image_url;
    @BindView(R.id.Cover_Picture_iv)
    ImageView CoverPictureIv;
    public static String QB_User_Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);
        ButterKnife.bind(this);
        Instantiation();
        hideActionbar();
        Set_Details();
        if (QB_User_Id!=null)
        {Fetch_User_QB(QB_User_Id);}


    }

    private void Set_Details() {

        User_Name = getIntent().getStringExtra("User_Name");
        User_Login = getIntent().getStringExtra("User_Login");
        User_Image_url = getIntent().getStringExtra("User_Image_url");

        if (User_Login.contains("@")) {
            show_Email_number.setVisibility(View.VISIBLE);
            show_phone_number.setVisibility(View.GONE);
            show_Email_number.setText(User_Login);
        } else {
            show_phone_number.setVisibility(View.VISIBLE);
            show_Email_number.setVisibility(View.GONE);
            show_phone_number.setText(User_Login);
        }


        show_Email_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", User_Login, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TuDime");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "TuDime Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });


        show_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(Chat_profile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Chat_profile.this, new String[]{Manifest.permission.CALL_PHONE}, 256);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + User_Login));
                    startActivity(intent);
                }
            }
        });
    }

    private void Instantiation() {
        User_Name_tv = findViewById(R.id.User_Name_tv);
        show_status_tv = findViewById(R.id.show_status_tv);
        show_Email_number = findViewById(R.id.show_Email_number);
        show_phone_number = findViewById(R.id.show_phone_number);
        profile_picture_iv = findViewById(R.id.profile_picture_iv);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        hideProgressDialog();
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode==208)
        {hideProgressDialog();

            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    if (response.getJSONArray("data").length()==0)
                    {User_Name_tv.setText(User_Name);
                        show_status_tv.setText(getResources().getString(R.string.status_default_ciaom));

                        Picasso.get().load(User_Image_url).placeholder(R.drawable.navigation_drawer_pro_pic)
                                .into(profile_picture_iv);
                        CoverPictureIv.setImageDrawable(getResources().getDrawable(R.drawable.navigation_drawer_cover_bg));
                    }else
                    {
                        if (!response.getJSONArray("data").getJSONObject(0).getString("name").equalsIgnoreCase(""))
                        {User_Name_tv.setText(response.getJSONArray("data").getJSONObject(0).getString("name"));}else
                        {User_Name_tv.setText(User_Name);}

                        if (!response.getJSONArray("data").getJSONObject(0).getString("Bio").equalsIgnoreCase(""))
                        {show_status_tv.setText(response.getJSONArray("data").getJSONObject(0).getString("Bio"));}
                        else
                        {show_status_tv.setText(getResources().getString(R.string.status_default_ciaom));}//


                        if (!response.getJSONArray("data").getJSONObject(0).getString("Cover_pic_url").equalsIgnoreCase(""))
                        {
                            try {
                                Picasso.get().load(response.getJSONArray("data").getJSONObject(0).getString("Cover_pic_url") ).placeholder(R.drawable.navigation_drawer_pro_pic)
                                        .into(profile_picture_iv);
                                Picasso.get().load(response.getJSONArray("data").getJSONObject(0).getString("pic1_url")).placeholder(R.drawable.default_image_call)
                                        .into(CoverPictureIv);
                            }catch (Exception e)
                            {
                                Picasso.get().load("").placeholder(R.drawable.navigation_drawer_pro_pic).into(profile_picture_iv);
                                CoverPictureIv.setImageDrawable(getResources().getDrawable(R.drawable.navigation_drawer_cover_bg));
                                hideProgressDialog(); }
                        }
                        else
                        {
                            Picasso.get().load(User_Image_url).placeholder(R.drawable.navigation_drawer_pro_pic)
                                    .into(profile_picture_iv);
                        }
                    }






        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        hideProgressDialog();
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
        hideProgressDialog();
    }
}
