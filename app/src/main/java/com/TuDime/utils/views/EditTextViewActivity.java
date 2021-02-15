package com.TuDime.utils.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.TuDime.doodle.utils.preferences.DoodlePreferences;
import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.TuDime.R;
import com.TuDime.utils.doodleutils.photosortr.UpdateDrawable;
import com.TuDime.ui.activity.BaseActivity;
import com.TuDime.Prefrences.SharedPrefsHelper;

/**
 * Created by AppRoutes on 03-02-2016.
 */
public class EditTextViewActivity extends BaseActivity implements ColorPickerView.OnColorChangedListener, View.OnClickListener {

    protected ColorPickerView mColorPickerView;
    private ColorPanelView mOldColorPanelView;
    private ColorPanelView mNewColorPanelView;

    private Button mOkButton;
    protected Button mCancelButton;
    private EditText etColorView;
    private UpdateDrawable updateDrawablelistener;
    private DoodleActivity activity;
    public static Context context;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    public EditTextViewActivity()
    {

    }
    public EditTextViewActivity(Context context)
    {
this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getSupportActionBar().hide();
        setContentView
                (R.layout.activity_color_picker);

        init();
        etColorView.setTypeface(null, Typeface.BOLD);
        etColorView.setTextSize(36f);
        etColorView.setText(DoodlePreferences.getInstance(this).getdoodleText());
        etColorView.setHorizontallyScrolling(false);
//        etColorView.setMaxLines(5);
        etColorView.setImeActionLabel("Custom_text" , KeyEvent.KEYCODE_ENTER);


        etColorView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    DoodlePreferences.getInstance(EditTextViewActivity.this).setdoodleText(etColorView.getText().toString().trim());
                    addImageDrawable(true);
                }
                return false;
            }
        });



//        activity=(DoodleActivity)context ;

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
//    public void EventListener( UpdateDrawable updateDrawablelistener)
//    {
//
//        this.updateDrawablelistener= updateDrawablelistener;
//    }
    private void init() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("color_3", 0xFF000000);

        mColorPickerView = (ColorPickerView) findViewById(R.id.colorpickerview__color_picker_view);
        mOldColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_old);
        mNewColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_new);
        etColorView = (EditText) findViewById(R.id.tvColorView);
        mOkButton = (Button) findViewById(R.id.okButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);



        ((LinearLayout) mOldColorPanelView.getParent()).setPadding(
                mColorPickerView.getPaddingLeft(), 0,
                mColorPickerView.getPaddingRight(), 0);


        mColorPickerView.setOnColorChangedListener(this);
        mColorPickerView.setColor(initialColor, true);
        mOldColorPanelView.setColor(initialColor);

        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

    }

    @Override
    public void onColorChanged(int newColor) {
        mNewColorPanelView.setVisibility(View.GONE);
        mNewColorPanelView.setColor(mColorPickerView.getColor());
        etColorView.setTextColor(mColorPickerView.getColor());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.okButton:
                DoodlePreferences.getInstance(this).setdoodleText(etColorView.getText().toString().trim());
                    addImageDrawable(true);

                break;
            case R.id.cancelButton:
                addImageDrawable(false);
                finish();
                break;
        }

    }

    private void addImageDrawable(boolean isConfirmed) {
        DoodleActivity main = (DoodleActivity) context;
        updateDrawablelistener = main;
        if (etColorView != null)
        {
            System.out.println("view is not null.....");
            if (!etColorView.getText().toString().trim().equals("")) {
                etColorView.setText(DoodlePreferences.getInstance(this).getdoodleText());
                etColorView.setCursorVisible(false);
                etColorView.setDrawingCacheEnabled(true);
                etColorView.buildDrawingCache();
                Bitmap bm = etColorView.getDrawingCache();

                updateDrawablelistener.updateDrawable(storeImage(bm, "textdoodleImage"),isConfirmed);
            }
            else {
                updateDrawablelistener.updateDrawable(null,isConfirmed);
            }
        }

        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putInt("color_3", mColorPickerView.getColor());
        edit.commit();

        finish();
    }





    public Drawable storeImage(Bitmap imageData, String filename) {
        String iconsStoragePath = Environment.getExternalStorageDirectory()
                + "/DoodleImages";
        Drawable d=null;


        File sdIconStorageDir = new File(iconsStoragePath);
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir.toString() + "/" + filename
                    + ".png";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(
                    fileOutputStream);

            // compress image according to your format
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

          d = Drawable.createFromPath(filePath);

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return null;
        }

        return d;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addImageDrawable(false);
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
