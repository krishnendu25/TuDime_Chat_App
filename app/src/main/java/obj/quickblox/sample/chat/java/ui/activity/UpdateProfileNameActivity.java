package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import obj.quickblox.sample.chat.java.EmojiKeyBoard.EmojiKeyboard;
import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

public class UpdateProfileNameActivity extends BaseActivity implements View.OnClickListener , IJSONParseListener {

    private LinearLayout btn_cancel,btn_done;
    private EditText edtEmojicon;
    private ImageView imgOptions;
    private EmojiKeyboard emojiKeyboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_update_profile_name);
        Initialization();
        hideActionbar();


    }

    private void Initialization()
    {actionBar.setDisplayHomeAsUpEnabled(true);
        View v = findViewById(R.id.rootview);
        emojiKeyboard = new EmojiKeyboard(this, v);
        btn_cancel = (LinearLayout)findViewById(R.id.btn_cancel);
        btn_done = (LinearLayout)findViewById(R.id.btn_done);
        edtEmojicon = (EditText)findViewById(R.id.edtEmojicon);
        btn_cancel.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.enter_your_name));
        imgOptions = (ImageView) findViewById(R.id.imgOptions);
        imgOptions.setOnClickListener(this);
        edtEmojicon.setText(SharedPrefsHelper.getInstance().getUserName());
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_done:
                if (StringUtils.isNullOrEmpty(edtEmojicon.getText().toString().trim())) {
                    ToastUtils.shortToast(getString(R.string.name_cannot_be_empty));
                } else {

                    Update_QuickBlox_Name(edtEmojicon.getText().toString());

                }
                break;

            case R.id.btn_cancel:

                break;

            case R.id.imgOptions:
                ShowEmojieKeyBoard();
                break;

        }
    }

    private void Update_QuickBlox_Name(String toString) {
        showProgressDialog(R.string.load);
        QBUser qbUser = new QBUser();
        qbUser.setId(Integer.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
        qbUser.setFullName(toString);
        QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
                SharedPrefsHelper.getInstance().saveQbUser(qbUser);
                Update_Profile_Update(SharedPrefsHelper.getInstance().getUSERID(),
                        edtEmojicon.getText().toString(),
                        SharedPrefsHelper.getInstance().getQbUser().toString());
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });
    }

    private void ShowEmojieKeyBoard()
    {
        edtEmojicon.requestFocus();
        emojiKeyboard.createEmojiKeyboard();
        emojiKeyboard.showEmoji();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }


    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        if (requestCode==584)
        {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }

        }
        if (requestCode==447)
        {hideProgressDialog();
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                {
                    SharedPrefsHelper.getInstance().setUserName(response.getJSONArray("data").getJSONObject(0).getString("name"));
                    Toast.makeText(getApplicationContext(), "Your profile has been updated successfully", Toast.LENGTH_SHORT).show();
                }else
                {
                    ToastUtils.shortToast("Oops...something went wrong...");
                }
            } catch (JSONException e) {
                hideProgressDialog();
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
