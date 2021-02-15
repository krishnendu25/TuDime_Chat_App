package com.TuDime.ui.activity;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import com.TuDime.Prefrences.CiaoPrefrences;
import com.TuDime.R;
import com.TuDime.ui.fragments.EcardsCategoriesPage;
import com.TuDime.ui.fragments.EcardsHomePage;
import com.TuDime.Prefrences.SharedPrefsHelper;

public class Ecards3D_Activity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private static final int REQUEST_CODE_DOODLE = 10006;
    private Spinner lang_sel;
    private CiaoPrefrences prefrences;
    public static ArrayList<String> backStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_ecards3_d_);

        hideActionbar();
        backStack = new ArrayList<>();

        changeFragment(new EcardsHomePage(), "");

        lang_sel = (Spinner) findViewById(R.id.select_lang);
        prefrences = CiaoPrefrences.getInstance(Ecards3D_Activity.this);

        final ArrayList<String> language_aaray = new ArrayList<>();
        language_aaray.add(getString(R.string.select_your_language));
        language_aaray.add(getString(R.string.eng));
        language_aaray.add(getString(R.string.spanish));
        language_aaray.add(getString(R.string.hindi));
       /* language_aaray.add(getString(R.string.Thai));
        language_aaray.add(getString(R.string.Ben));
        language_aaray.add(getString(R.string.Tel));
        language_aaray.add(getString(R.string.Mar));
        language_aaray.add(getString(R.string.Fre));
        language_aaray.add(getString(R.string.Chi));
        language_aaray.add(getString(R.string.Kor));
        language_aaray.add(getString(R.string.Ara));
        language_aaray.add(getString(R.string.Jap));
        language_aaray.add(getString(R.string.Mal));
        language_aaray.add(getString(R.string.Por));
        language_aaray.add(getString(R.string.Vie));*/

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Ecards3D_Activity.this, android.R.layout.simple_list_item_1, language_aaray);
        lang_sel.setAdapter(adapter);

        lang_sel.setOnItemSelectedListener(this);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeLanguage(prefrences.getAppLang());
//                finish();
                onBackPressed();
            }
        });

        findViewById(R.id.home_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new EcardsHomePage(), "");
                ((ImageView) findViewById(R.id.category_image)).setImageResource(R.drawable.category_icon);
                ((TextView) findViewById(R.id.category_txt)).setTextColor(getResources().getColor(R.color.white));
                ((ImageView) findViewById(R.id.home_image)).setImageResource(R.drawable.home_active_icon);
                ((TextView) findViewById(R.id.home_txt)).setTextColor(getResources().getColor(R.color.pink_color));
                findViewById(R.id.customize).setVisibility(View.GONE);
                findViewById(R.id.select_lang).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.category_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new EcardsCategoriesPage(), "");
                ((ImageView) findViewById(R.id.category_image)).setImageResource(R.drawable.category_active_icon);
                ((TextView) findViewById(R.id.category_txt)).setTextColor(getResources().getColor(R.color.pink_color));
                ((ImageView) findViewById(R.id.home_image)).setImageResource(R.drawable.home_inactive_icon);
                ((TextView) findViewById(R.id.home_txt)).setTextColor(getResources().getColor(R.color.white));
                findViewById(R.id.customize).setVisibility(View.GONE);
                findViewById(R.id.select_lang).setVisibility(View.GONE);
            }
        });

        /*findViewById(R.id.customize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDoodle = new Intent(Ecards3D_Activity.this, com.TuDime.doodle.utils.views.DoodleActivity.class);
                startActivityForResult(intentDoodle, REQUEST_CODE_DOODLE);
            }
        });*/

    }


    public void changeFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        if (!title.equals("")) {
            ((TextView) findViewById(R.id.title_cards)).setText(title);
            findViewById(R.id.customize).setVisibility(View.VISIBLE);
            findViewById(R.id.select_lang).setVisibility(View.GONE);
            findViewById(R.id.footer_part).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.title_cards)).setText(getString(R.string.tudime_from_heart));
            findViewById(R.id.customize).setVisibility(View.GONE);
            findViewById(R.id.select_lang).setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        try {
            ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.transparent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (position) {
            case 0:
                break;
            case 1:
                changeLanguage("en");

                SharedPrefsHelper.getInstance().set_Language("en");
//                changeFragment(new EcardsHomePage() , "");
                finish();
                startActivity(new Intent(Ecards3D_Activity.this, Ecards3D_Activity.class));
                break;
            case 2:
                changeLanguage("es");

                SharedPrefsHelper.getInstance().set_Language("es");
//                changeFragment(new EcardsHomePage() , "");
                finish();
                startActivity(new Intent(Ecards3D_Activity.this, Ecards3D_Activity.class));
                break;
            case 3:
                changeLanguage("hi");
                SharedPrefsHelper.getInstance().set_Language("hi");
                prefrences.setEcardAppLang("hindi");
//                changeFragment(new EcardsHomePage() , "");
                finish();
                startActivity(new Intent(Ecards3D_Activity.this, Ecards3D_Activity.class));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void changeLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 88124) {




            }
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
