package com.TuDime.ui.activity;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.TuDime.NetworkOperation.IJSONParseListener;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.constants.AppConstants;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

import static com.TuDime.constants.ApiConstants.update_user_profile;

public class StatusEditActivity extends BaseActivity implements View.OnClickListener, IJSONParseListener {

    private EditText edtStatus;
    private TextView txvCount;
    private int MAX_STATUS_LENGTH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_status_edit);
        hideActionbar();
        String status = getIntent().getStringExtra(AppConstants.EXTRA_USER_STATUS);
        edtStatus = (EditText) findViewById(R.id.edtStatus);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(MAX_STATUS_LENGTH);
        edtStatus.setFilters(fArray);
        edtStatus.setText(status + "");
        edtStatus.setSelection(edtStatus.getText().toString().length());
        txvCount = (TextView) findViewById(R.id.txvCount);
        txvCount.setText(MAX_STATUS_LENGTH - status.length() + "");

        edtStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txvCount.setText(MAX_STATUS_LENGTH - s.length() + "");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button_icon);
        getSupportActionBar().setTitle(getString(R.string.status_add));

        findViewById(R.id.txvDone).setOnClickListener(this);
        findViewById(R.id.txvCancel).setOnClickListener(this);




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txvCancel:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.txvDone:
                if (edtStatus.getText().toString().trim().equals("")) {
                ToastUtils.shortToast("Enter Your Status");
                    return;
                }
                else {
                    Hit_Status_Update(SharedPrefsHelper.getInstance().getUSERID(),edtStatus.getText().toString().trim());
                }
                break;
            default:
                break;
        }
    }

    private void Hit_Status_Update(String userid, String Status)
    {
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
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode==442)
        {
            try {
                hideProgressDialog();
                try {
                    if (response.getString("status").equalsIgnoreCase("success"))
                    {
                        Toast.makeText(StatusEditActivity.this, R.string.Status_Updated_Successfully, Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }else
                    {
                        Toast.makeText(StatusEditActivity.this, "Oops...some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    hideProgressDialog();
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
