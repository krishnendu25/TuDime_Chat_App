package obj.quickblox.sample.chat.java.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.ui.adapter.StatusListAdapter;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.update_user_profile;

/* renamed from: com.tudime.ui.activity.StatusChangeActivity */
public class StatusChangeActivity extends BaseActivity implements IJSONParseListener,View.OnClickListener {
    public StatusListAdapter adapter;
    private ListView lsvStatus;
    protected String mSelectedStatus = "";
    public TextView showEditStatus;
    public ArrayList<String> statusList;
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView((int) R.layout.activity_status_change);
        hideActionbar();
        this.statusList = SharedPrefsHelper.getInstance().getAllStatus(StatusChangeActivity.this);
        findViewById(R.id.imgEdit).setOnClickListener(this);
        this.showEditStatus = (TextView) findViewById(R.id.txvStatus);
        this.showEditStatus.setText(SharedPrefsHelper.getInstance().getCurrentStatus());
        this.lsvStatus = (ListView) findViewById(R.id.lsvStatus);
        this.adapter = new StatusListAdapter(this);
        this.adapter.setListData(SharedPrefsHelper.getInstance().getAllStatus(StatusChangeActivity.this));
        this.lsvStatus.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        this.lsvStatus.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                StatusChangeActivity.this.mSelectedStatus = (String) StatusChangeActivity.this.statusList.get(position);
                StatusChangeActivity.this.adapter.setSelected(position);
                StatusChangeActivity.this.adapter.notifyDataSetChanged();
                StatusChangeActivity.this.hitApiStatusUpdate(SharedPrefsHelper.getInstance().getUSERID(),statusList.get(position).trim());
            }
        });

    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.showEditStatus.setText(SharedPrefsHelper.getInstance().getCurrentStatus());
    }

    private void setDefaultStatus() {
        if (StringUtils.isNullOrEmpty(SharedPrefsHelper.getInstance().getCurrentStatus())) {
            SharedPrefsHelper.getInstance().setCurrentStatus((String) SharedPrefsHelper.getInstance().getAllStatus(StatusChangeActivity.this).get(0));
            this.adapter.setSelected(0);
            this.showEditStatus.setText((CharSequence) SharedPrefsHelper.getInstance().getAllStatus(StatusChangeActivity.this).get(0));
        } else {
            this.adapter.setSelected(SharedPrefsHelper.getInstance().getAllStatus(StatusChangeActivity.this).indexOf(SharedPrefsHelper.getInstance().getCurrentStatus()));
            SharedPrefsHelper.getInstance().setCurrentStatus((String) this.statusList.get(0));
            this.showEditStatus.setText((CharSequence) this.statusList.get(0));
        }
        this.mSelectedStatus = this.showEditStatus.getText().toString();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1006) {
            /*this.statusList =  SharedPrefsHelper.getInstance().getAllStatus(StatusChangeActivity.this);
            this.adapter.setListData(this.statusList);
            SharedPrefsHelper.getInstance().setCurrentStatus((String) this.statusList.get(0));
            this.adapter.setSelected(0);
            this.adapter.notifyDataSetChanged();
            this.showEditStatus.setText( SharedPrefsHelper.getInstance().getCurrentStatus());*/
            if (resultCode== Activity.RESULT_OK){
                finish();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack /*2131624203*/:
                finish();
                break;
            case R.id.imgEdit /*2131624541*/:
                Intent intent = new Intent(this, StatusEditActivity.class);
                if (this.mSelectedStatus.equals("") || this.mSelectedStatus.equals("null") || this.mSelectedStatus.equals(null)) {
                    intent.putExtra(AppConstants.EXTRA_USER_STATUS, this.showEditStatus.getText().toString());
                } else {
                    intent.putExtra(AppConstants.EXTRA_USER_STATUS, this.mSelectedStatus);
                }
                startActivityForResult(intent, 1006);
                break;
        }
    }

    /* access modifiers changed from: private */
    private void hitApiStatusUpdate(String userid, String Status) {
        showProgressDialog(R.string.dlg_loading);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("userid",userid);
        parms.putString("Bio",Status);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, update_user_profile,
                442, this, parms, false,false,Params_Object);

    }

















    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode)
    {
        if (requestCode==442)
        {
            try {
                hideProgressDialog();
                try {
                    if (response.getString("status").equalsIgnoreCase("success"))
                    {
                        Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
                        Toast.makeText(StatusChangeActivity.this, R.string.Status_Updated_Successfully, Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(StatusChangeActivity.this, "Oops...some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode==447)
        {hideProgressDialog();
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success"))
                { showEditStatus.setText(response.getJSONArray("data").getJSONObject(0).getString("Bio"));
                SharedPrefsHelper.getInstance().setCurrentStatus(response.getJSONArray("data").getJSONObject(0).getString("Bio"));
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
