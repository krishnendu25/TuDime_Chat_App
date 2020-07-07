package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;


public class SetLanguage extends BaseActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener
{

    private Spinner spinner;
    private ArrayList<String> language_aaray;
    private Toolbar toolbar;
    private Button btnAgree;
    private String Language_Select="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_language);
        Intili();
        hideActionbar();


    }

    private void Intili()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.set_lang));
        hideActionbar();
        language_aaray = new ArrayList<>();
        language_aaray.add(getString(R.string.select_your_language));//0
        language_aaray.add(getString(R.string.eng));//1
        language_aaray.add(getString(R.string.spanish));//2
        language_aaray.add(getString(R.string.hindi));//3
        language_aaray.add(getString(R.string.Thai));//4
        language_aaray.add(getString(R.string.Ben));//5
        language_aaray.add(getString(R.string.Tel));//6
        language_aaray.add(getString(R.string.Mar));//7
        language_aaray.add(getString(R.string.Fre));//8
        language_aaray.add(getString(R.string.Chi));//9
        language_aaray.add(getString(R.string.Kor));//10
        language_aaray.add(getString(R.string.Ara));//11
        language_aaray.add(getString(R.string.Jap));//12
        language_aaray.add(getString(R.string.Mal));//13
        language_aaray.add(getString(R.string.Por));//14
        language_aaray.add(getString(R.string.Vie));//15
        language_aaray.add(getString(R.string.tl));//16
        language_aaray.add(getString(R.string.it));//17




        spinner=(Spinner)findViewById(R.id.spin);
        btnAgree = (Button)findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetLanguage.this , R.layout.lnaguage_select_textview  , R.id.txt_selct_lang, language_aaray);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
    {

        switch(position) {
            case 0:
                break;
            case 1:
               change_Lan("en");
            break;
            case 2:
                change_Lan("es");
                break;
            case 3:
                change_Lan("hi");
                break;
            case 4:
                change_Lan("th");
                break;
            case 5:
                change_Lan("bn");
                break;
            case 6:
                change_Lan("te");
                break;
            case 7:
                change_Lan("mr");
                break;
            case 8:
                change_Lan("fr");
                break;
            case 9:
                change_Lan("zh");
                break;
            case 10:
                change_Lan("ko");
                break;
            case 11:
                change_Lan("ar");
                break;
            case 12:
                change_Lan("ja");
                break;
            case 13:
                change_Lan("ms");
                break;
            case 14:
                change_Lan("pt");
                break;
            case 15:
                change_Lan("vi");
                break;
            case 16:
                change_Lan("tl");
                break;
            case 17:
                change_Lan("it");
                break;
            default:
                break;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }


    void change_Lan(String LN)
    {
        Language_Select = LN;
        SharedPrefsHelper.getInstance().set_Language(Language_Select);
        Locale locale2 = new Locale(Language_Select);
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getBaseContext().getResources().updateConfiguration(config2,getBaseContext().getResources().getDisplayMetrics());
    }






    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnAgree:

                if (Language_Select.equals("") || Language_Select.isEmpty())
                {
                    ToastUtils.showSnackBar(view, getString(R.string.selct_lang_camt_be_null));
                }else {
                    if (!spinner.getSelectedItem().equals(language_aaray.get(0))) {
                        try {
                            if (getIntent().getStringExtra("coming_from").equals("dashboard")) {
                                Intent ii =  new Intent(getApplicationContext(),DashBoard.class);
                                ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(ii);
                                Toast.makeText(SetLanguage.this, getString(R.string.lang_change_success), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            startActivity(new Intent(SetLanguage.this, Choose_Sign_Up_type.class));
                        }

                    } else {
                        ToastUtils.showSnackBar(spinner, getString(R.string.selct_lang_camt_be_null));
                    }
                }
                break;
        }
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
