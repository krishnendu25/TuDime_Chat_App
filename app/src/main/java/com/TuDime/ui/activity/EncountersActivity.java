package com.TuDime.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import com.TuDime.R;
import com.TuDime.Prefrences.SharedPrefsHelper;

public class EncountersActivity extends BaseActivity implements View.OnClickListener {
    private Dialog encounter_dialog , age_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_encounters);
        hideActionbar();
        showDialogBox();



    }


   protected void onResume()
    {
        super.onResume();
        showDialogBox();

    }

    private void showDialogBox(){

        encounter_dialog = new Dialog(EncountersActivity.this);
        encounter_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        encounter_dialog.setContentView(R.layout.dialog_encounter_yes_or_no);
        encounter_dialog.setCancelable(false);
        encounter_dialog.show();
       TextView encounter_use_txt=encounter_dialog.findViewById(R.id.encounter_use_txt);
        encounter_dialog.findViewById(R.id.allow).setOnClickListener(this);
        encounter_dialog.findViewById(R.id.deny).setOnClickListener(this);
        encounter_use_txt.setText(getResources().getText(R.string.encounter_use));
    }

    private void showAgeDialogBox(){
        age_dialog = new Dialog(EncountersActivity.this);
        age_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        age_dialog.setContentView(R.layout.dialog_to_enter_age);
        age_dialog.setCancelable(false);
        age_dialog.show();
    TextView age_value_ed = age_dialog.findViewById(R.id.age_value_ed);
        TextView age = age_dialog.findViewById(R.id.age);
        age.setText(getResources().getText(R.string.age));
        age_value_ed.setText(getResources().getText(R.string.age_value));
        age_dialog.findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.allow:
                encounter_dialog.dismiss();
                showAgeDialogBox();
                break;

            case R.id.deny:
                encounter_dialog.dismiss();
                hitApi(false);
                break;

            case R.id.submit:


                try{
                    int i = Integer.parseInt(((EditText) age_dialog.findViewById(R.id.enter_age)).getText().toString());
                    if(i >=18){
                        hitApi(true);
                    }else{
                        Toast.makeText(this, getString(R.string.cant_use_encounters), Toast.LENGTH_SHORT).show();
                        hitApi(false);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.dialog_retry), Toast.LENGTH_SHORT).show();
                }



                break;




        }
    }

    private void hitApi(boolean b)
    {
        startActivity(new Intent(EncountersActivity.this , InitialTermsActivity.class));
        age_dialog.dismiss();
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
