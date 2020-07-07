package obj.quickblox.sample.chat.java.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import obj.quickblox.sample.chat.java.Prefrences.CiaoPrefrences;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

public class AccountSettingsActivity extends BaseActivity implements View.OnClickListener {
    TextView privacy12, disable_account;
    @BindView(R.id.Archive_Chat)
    TextView ArchiveChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ButterKnife.bind(this);
        Instantiation();
    }

    private void Instantiation() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        privacy12 = findViewById(R.id.privacy12);
        privacy12.setOnClickListener(this);
        disable_account = findViewById(R.id.disable_account);
        disable_account.setOnClickListener(this);
        ArchiveChat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacy12:
                startActivity(new Intent(this, PrivacyActivity.class));
                break;
            case R.id.Archive_Chat:
                startActivity(new Intent(this, Archive_Chat.class));
                break;
            case R.id.disable_account:
                Delete_Your_Account(SharedPrefsHelper.getInstance().getQbUser().getId());
                break;
        }
    }

    private void Delete_Your_Account(int getUserId) {
        showProgressDialog(R.string.dlg_loading);
        QBUsers.deleteUser(getUserId).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                SharedPrefsHelper.clearAllData();
                CiaoPrefrences.getInstance(getApplicationContext()).clearPrefrences();
                Intent ii = new Intent(getApplicationContext(), SetLanguage.class);
                ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ii);
                finish();
                ToastUtils.longToast(R.string.DELETED_SUCCESSFULLY);
                hideProgressDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
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
