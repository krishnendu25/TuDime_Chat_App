package obj.quickblox.sample.chat.java.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import obj.quickblox.sample.chat.java.Internet_Calling.Internet_Calling_Activity;
import obj.quickblox.sample.chat.java.Internet_Calling.quickstart.VoiceActivity;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TuDime_CAN extends BaseActivity {

    @BindView(R.id.note)
    TextView note;
    @BindView(R.id.editText_to_call)
    EditText editTextToCall;
    @BindView(R.id.add_contacts)
    ImageView addContacts;
    @BindView(R.id.edit_number)
    Button editNumber;
    @BindView(R.id.txt_1)
    TextView txt1;
    @BindView(R.id.txt_2)
    TextView txt2;
    @BindView(R.id.txt_3)
    TextView txt3;
    @BindView(R.id.txt_4)
    TextView txt4;
    @BindView(R.id.txt_5)
    TextView txt5;
    @BindView(R.id.txt_6)
    TextView txt6;
    @BindView(R.id.txt_7)
    TextView txt7;
    @BindView(R.id.txt_8)
    TextView txt8;
    @BindView(R.id.txt_9)
    TextView txt9;
    @BindView(R.id.txt_12)
    TextView txt12;
    @BindView(R.id.txt_0)
    TextView txt0;
    @BindView(R.id.txt_11)
    TextView txt11;
    @BindView(R.id.call_btn)
    ImageView callBtn;
    @BindView(R.id.call_rates)
    TextView callRates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tu_dime_can);
        ButterKnife.bind(this);
        requestPermission();

    }

    @OnClick({R.id.add_contacts, R.id.edit_number, R.id.txt_1, R.id.txt_2, R.id.txt_3, R.id.txt_4, R.id.txt_5, R.id.txt_6, R.id.txt_7, R.id.txt_8, R.id.txt_9, R.id.txt_12, R.id.txt_0, R.id.txt_11, R.id.call_btn, R.id.call_rates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_contacts:
                Intent intent = new Intent(Intent.ACTION_PICK , ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 12);
                }
                break;
            case R.id.edit_number:
                String s = editTextToCall.getText().toString();
                String s1 = "";
                for(int i=0 ; i<s.length()-1 ; i++){
                    s1 = s1 + s.charAt(i);
                }
                editTextToCall.setText(s1);
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_1:
                editTextToCall.setText(editTextToCall.getText() + "1");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_2:
                editTextToCall.setText(editTextToCall.getText() + "2");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_3:
                editTextToCall.setText(editTextToCall.getText() + "3");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_4:
                editTextToCall.setText(editTextToCall.getText() + "4");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_5:
                editTextToCall.setText(editTextToCall.getText() + "5");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_6:
                editTextToCall.setText(editTextToCall.getText() + "6");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_7:
                editTextToCall.setText(editTextToCall.getText() + "7");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_8:
                editTextToCall.setText(editTextToCall.getText() + "8");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_9:
                editTextToCall.setText(editTextToCall.getText() + "9");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_0:
                editTextToCall.setText(editTextToCall.getText() + "0");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_12:
                editTextToCall.setText(editTextToCall.getText() + "+");
                editTextToCall.setSelection(editTextToCall.getText().length());
                break;
            case R.id.txt_11:
                break;
            case R.id.call_btn:
                if(editTextToCall.getText().toString().startsWith("+")) {
                    /*CallClient callClient = sinchClient.getCallClient();
                    callClient.callPhoneNumber(editTextToCall.getText().toString().trim());*/
                    Intent intent1 = new Intent(this, VoiceActivity.class);
                   // intent1.putExtra("number",editTextToCall.getText().toString().trim());
                    startActivity(intent1);
                }  else{
                    ToastUtils.shortToast("Enter Country Code With +");
                }
                break;
            case R.id.call_rates:
                startActivity(new Intent(this , PriceRatesWebView.class));
                break;



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();
                Cursor c = getContentResolver().query(contactUri, null, null, null, null);
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            String ContctMobVar = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            editTextToCall.setText(ContctMobVar);
                        }

                    }
                }
            }
        }
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(this, new
                    String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 856);
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