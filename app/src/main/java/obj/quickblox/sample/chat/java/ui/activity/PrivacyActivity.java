package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

public class PrivacyActivity extends BaseActivity implements View.OnClickListener {

    TextView set_password, remove_password;
    ArrayList<String> SEC_QUS = new ArrayList<>();
    Switch Active_password_swithch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        Instantiation();
        Active_password_swithch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (SharedPrefsHelper.getInstance().get_PASSWORD() != null) {
                    if (!SharedPrefsHelper.getInstance().get_PASSWORD().equalsIgnoreCase("")) {
                        if (isChecked) {
                            SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("true");
                        } else {
                            SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("false");
                        }

                    } else {
                        ToastUtils.shortToast(R.string.First_password);
                        Active_password_swithch.setChecked(false);
                        SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("false");
                    }
                } else {
                    Active_password_swithch.setChecked(false);
                    ToastUtils.shortToast(R.string.First_password);
                    SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("false");
                }


                if (isChecked) {
                    SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("true");
                } else {
                    SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("false");
                }


            }
        });

    }

    private void Instantiation()
    {actionBar.setDisplayHomeAsUpEnabled(true);
        set_password = findViewById(R.id.set_password);
        set_password.setOnClickListener(this);
        remove_password = findViewById(R.id.remove_password);
        Active_password_swithch = findViewById(R.id.Active_password_swithch);
        remove_password.setOnClickListener(this);
        SEC_QUS.clear();
        SEC_QUS.add("What was the house number and street name you lived in as a child?");
        SEC_QUS.add("What were the last four digits of your childhood telephone number?");
        SEC_QUS.add(" What primary school did you attend?");
        SEC_QUS.add(" In what town or city was your first full time job?");
        SEC_QUS.add(" In what town or city did you meet your spouse/partner?");
        SEC_QUS.add(" What is the middle name of your oldest child?");
        SEC_QUS.add(" What are the last five digits of your driver's licence number?");
        SEC_QUS.add(" What is your grandmother's (on your mother's side) maiden name?");
        SEC_QUS.add(" What is your spouse or partner's mother's maiden name?");
        SEC_QUS.add("  In what town or city did your mother and father meet?");
        SEC_QUS.add("  What time of the day were you born? (hh:mm)");
        SEC_QUS.add(" What time of the day was your first child born? (hh:mm)");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_password:
                if (SharedPrefsHelper.getInstance().get_PASSWORD() != null) {
                    if (SharedPrefsHelper.getInstance().get_PASSWORD().equalsIgnoreCase("")) {
                        Open_Set_Password_view(this);
                    } else {
                        ToastUtils.shortToast(getString(R.string.password_already_set));
                    }
                } else {
                    Open_Set_Password_view(this);
                }
                break;
            case R.id.remove_password:
                if (SharedPrefsHelper.getInstance().get_PASSWORD() != null) {
                    if (!SharedPrefsHelper.getInstance().get_PASSWORD().equalsIgnoreCase("")) {
                        Remove_Password(this);
                    } else {
                        ToastUtils.shortToast(R.string.First_password);
                    }
                } else {
                    ToastUtils.shortToast(R.string.First_password);
                }


                break;
        }

    }

    private void Remove_Password(Activity mActivity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (mActivity).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.remove_password, null);
        dialogBuilder.setView(dialogView);


        EditText old_pass = dialogView.findViewById(R.id.old_pass);
        EditText sec_ans1 = dialogView.findViewById(R.id.sec_ans1);
        TextView sec_ques = dialogView.findViewById(R.id.sec_ques);
        Button save = dialogView.findViewById(R.id.save);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        if (SharedPrefsHelper.getInstance().get_SECURITY_QUSTION()!=null)
        { sec_ques.setText(SharedPrefsHelper.getInstance().get_SECURITY_QUSTION()); }


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (old_pass.getText().toString().length() != 4) {
                    ToastUtils.shortToast(R.string.digite);
                    return;
                } else if (sec_ans1.getText().toString().isEmpty()) {
                    ToastUtils.shortToast(R.string.Entersecurity_answer);
                    return;
                } else {
                Save_Password(old_pass.getText().toString(),
                        sec_ans1.getText().toString());
            }
        }

            private void Save_Password(String old_pass, String sec_ans1) {
                alertDialog.dismiss();
                if (SharedPrefsHelper.getInstance().get_PASSWORD().equalsIgnoreCase(old_pass) &&
                        SharedPrefsHelper.getInstance().get_SECURITY_ANSWER().equalsIgnoreCase(sec_ans1)) {
                    SharedPrefsHelper.getInstance().set_PASSWORD("");
                    SharedPrefsHelper.getInstance().set_SECURITY_QUSTION("");
                    SharedPrefsHelper.getInstance().set_SECURITY_ANSWER("");
                    ToastUtils.longToast(R.string.remove_password_scu);
                    Active_password_swithch.setChecked(false);
                    SharedPrefsHelper.getInstance().set_PASSWORD_STATUS("false");
                    alertDialog.dismiss();
                } else {
                    ToastUtils.longToast(R.string.REMOVE_PASSWORD);
                }
            }
        });


        alertDialog.show();

    }

    private void Open_Set_Password_view(Activity mActivity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (mActivity).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.set_password_view, null);
        dialogBuilder.setView(dialogView);

        EditText enter_password = dialogView.findViewById(R.id.enter_password);
        EditText confirm_password = dialogView.findViewById(R.id.confirm_password);
        EditText sec_ans1 = dialogView.findViewById(R.id.sec_ans1);
        Spinner sec_ques1 = dialogView.findViewById(R.id.sec_ques1);
        Button save = dialogView.findViewById(R.id.save);
        TextView change_password = dialogView.findViewById(R.id.change_password);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SEC_QUS);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sec_ques1.setAdapter(spinnerArrayAdapter);


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_password.getText().toString().length() != 4) {
                    ToastUtils.shortToast(R.string.digite);
                    return;
                } else if (!enter_password.getText().toString().equals(confirm_password.getText().toString())) {
                    ToastUtils.shortToast(R.string.Mismatch);
                    return;
                } else if (sec_ans1.getText().toString().isEmpty()) {
                    ToastUtils.shortToast(R.string.security_answer_security_answer);
                    return;
                } else if (sec_ques1.getSelectedItem().toString().isEmpty()) {
                    ToastUtils.shortToast(R.string.security_question);
                    return;
                } else {
                    Save_Password(enter_password.getText().toString(),
                            confirm_password.getText().toString(),
                            sec_ans1.getText().toString(),
                            sec_ques1.getSelectedItem().toString());
                }
            }

            private void Save_Password(String enter_password, String confirm_password, String sec_ans1, String sec_ques1) {
                alertDialog.dismiss();
                SharedPrefsHelper.getInstance().set_PASSWORD(confirm_password);
                SharedPrefsHelper.getInstance().set_SECURITY_QUSTION(sec_ques1);
                SharedPrefsHelper.getInstance().set_SECURITY_ANSWER(sec_ans1);
                ToastUtils.longToast(R.string.YOUR_PASSWORD_SET_SUCCESSFULLY);
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openforgotpassword();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();


    }


        private void openforgotpassword()
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (this).getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.forgot_password, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);

            EditText enter_security_answer = dialogView.findViewById(R.id.enter_security_answer);
            TextView my_seurity_qustion = dialogView.findViewById(R.id.my_seurity_qustion);
            Button save = dialogView.findViewById(R.id.save);

            LinearLayout password_section = dialogView.findViewById(R.id.password_section);
            EditText enter_password = dialogView.findViewById(R.id.enter_password);
            EditText confirm_password = dialogView.findViewById(R.id.confirm_password);

            if (SharedPrefsHelper.getInstance().get_SECURITY_QUSTION()!=null)
            { my_seurity_qustion.setText(SharedPrefsHelper.getInstance().get_SECURITY_QUSTION()); }


            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (password_section.getVisibility()==View.GONE){
                        try{
                            if (enter_security_answer.getText().toString().trim().equalsIgnoreCase(SharedPrefsHelper.getInstance().get_SECURITY_ANSWER()))
                            {
                                enter_security_answer.setEnabled(false);
                                enter_security_answer.setAlpha(0.5f);
                                password_section.setVisibility(View.VISIBLE);

                            }else
                            {
                                ToastUtils.longToast(R.string.Wrong_Security_answer);
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }else
                    {

                        if (enter_password.getText().toString().length() != 4) {
                            ToastUtils.shortToast(R.string.digite);
                            return;
                        } else if (!enter_password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
                            ToastUtils.shortToast(R.string.Mismatch);
                            return;
                        }else
                        {
                            SharedPrefsHelper.getInstance().set_PASSWORD(confirm_password.getText().toString().trim());
                            ToastUtils.longToast(R.string.YOUR_PASSWORD_SET_SUCCESSFULLY);
                            alertDialog.dismiss();

                        }

                    }




                }
            });
            alertDialog.show();
        }


    @Override
    protected void onResume() {
        super.onResume();

        if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS() != null) {
            if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS().equals("true")) {
                Active_password_swithch.setChecked(true);
            } else if (SharedPrefsHelper.getInstance().get_PASSWORD_STATUS().equals("false")) {
                Active_password_swithch.setChecked(false); } } else { Active_password_swithch.setChecked(false); }


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
