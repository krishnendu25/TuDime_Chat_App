package obj.quickblox.sample.chat.java.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.activity.Ecards_EditPage;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static android.app.Activity.RESULT_OK;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.detectlanguage;
import static org.webrtc.ContextUtils.getApplicationContext;

public class EditTextColor extends BaseFragment implements IJSONParseListener, View.OnClickListener, ColorPickerView.OnColorChangedListener, AdapterView.OnItemSelectedListener {
    private View v;
    private Dialog enter_text_dialog , color_picker_dialog;
    private String text = "";
    private Boolean isStyleVisible = false;
    private Boolean isFontSize = false;
    private Boolean isAlign = false;
    private int mColorPickerViewColor = 0xFF000000;
    private Typeface myTypeFace1;
    private String fontType = "Rope5.ttf";
    private int fontSize = 16;
    private EditText edit_text;
    private ColorPickerView mColorPickerView;
    private ColorPanelView mOldColorPanelView , mNewColorPanelView;
    private Button mOkButton , mCancelButton;
    private final String defaultStr="";
    private RelativeLayout size;
    private LinearLayout alignment;
    private HorizontalScrollView style;
    private ImageView google_translater_iv;
    private EditText text_Tra;
    private String Language_String;
    private LinearLayout Translation_View;
    private String SelectLang_Code="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.text_edit_activity , container , false);

        alignment = (LinearLayout) v.findViewById(R.id.alignmnet_change);
        size = (RelativeLayout) v.findViewById(R.id.font_size_change);
        style = (HorizontalScrollView) v.findViewById(R.id.font_style_change);
//        edit_text.setText(text);
        google_translater_iv = v.findViewById(R.id.google_translater_iv);
        v.findViewById(R.id.back_image).setOnClickListener(this);
        v.findViewById(R.id.take_text).setOnClickListener(this);
        v.findViewById(R.id.enter_text).setOnClickListener(this);
        v.findViewById(R.id.stylish_text).setOnClickListener(this);
        v.findViewById(R.id.coloured_text).setOnClickListener(this);
        v.findViewById(R.id.size_of_text).setOnClickListener(this);

        v.findViewById(R.id.font_1).setOnClickListener(this);
        v.findViewById(R.id.font_2).setOnClickListener(this);
        v.findViewById(R.id.font_3).setOnClickListener(this);
        v.findViewById(R.id.font_4).setOnClickListener(this);
        v.findViewById(R.id.font_5).setOnClickListener(this);
        v.findViewById(R.id.font_6).setOnClickListener(this);
        v.findViewById(R.id.font_7).setOnClickListener(this);
        v.findViewById(R.id.font_8).setOnClickListener(this);
        v.findViewById(R.id.font_9).setOnClickListener(this);
        v.findViewById(R.id.font_10).setOnClickListener(this);
        v.findViewById(R.id.font_11).setOnClickListener(this);
        v.findViewById(R.id.font_12).setOnClickListener(this);
        v.findViewById(R.id.font_13).setOnClickListener(this);
        v.findViewById(R.id.font_14).setOnClickListener(this);
        v.findViewById(R.id.font_15).setOnClickListener(this);
        v.findViewById(R.id.left_align).setOnClickListener(this);
        v.findViewById(R.id.center_align).setOnClickListener(this);
        v.findViewById(R.id.right_align).setOnClickListener(this);
        ArrayList<String> Language_List = new ArrayList<>();
         Translation_View = (LinearLayout) v.findViewById(R.id.Translation_View);
        Button tranlate_btn = (Button) v.findViewById(R.id.tranlate_btn);
        ImageView dismiss = (ImageView) v.findViewById(R.id.dismiss);
         text_Tra = (EditText) v.findViewById(R.id.text_tra);
         edit_text = (EditText) v.findViewById(R.id.text_main);
        ImageView speech_to_text = (ImageView) v.findViewById(R.id.speech_to_text);
        Spinner spin_translate = (Spinner) v.findViewById(R.id.spin_translate);

        Language_List.add(getString(R.string.select_your_language));//0
        Language_List.add(getString(R.string.eng));//1
        Language_List.add(getString(R.string.spanish));//2
        Language_List.add(getString(R.string.hindi));//3
        Language_List.add(getString(R.string.Thai));//4
        Language_List.add(getString(R.string.Ben));//5
        Language_List.add(getString(R.string.Tel));//6
        Language_List.add(getString(R.string.Mar));//7
        Language_List.add(getString(R.string.Fre));//8
        Language_List.add(getString(R.string.Chi));//9
        Language_List.add(getString(R.string.Kor));//10
        Language_List.add(getString(R.string.Ara));//11
        Language_List.add(getString(R.string.Jap));//12
        Language_List.add(getString(R.string.Mal));//13
        Language_List.add(getString(R.string.Por));//14
        Language_List.add(getString(R.string.Vie));//15
        Language_List.add(getString(R.string.tl));//16
        Language_List.add(getString(R.string.it));//17


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.lnaguage_select_textview, R.id.txt_selct_lang, Language_List);
        spin_translate.setAdapter(adapter);
        spin_translate.setOnItemSelectedListener(this);
        
        speech_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer(1212);
            }
        });
        google_translater_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Translation_View.getVisibility() == View.VISIBLE) {
                    Translation_View.setVisibility(View.GONE);
                } else {
                    Translation_View.setVisibility(View.VISIBLE);
                }
            }
        });
        
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Translation_View.getVisibility() == View.VISIBLE) {
                    Translation_View.setVisibility(View.GONE);
                } else {
                    Translation_View.setVisibility(View.VISIBLE);
                }
            }
        });
        
        tranlate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SelectLang_Code.equalsIgnoreCase(""))
                {
                    ToastUtils.shortToast( R.string.Select_one_language);
                    return;
                }else if (text_Tra.getText().toString().trim().equalsIgnoreCase(""))
                {
                    ToastUtils.shortToast("Enter Your Text");
                    return;
                }else
                {
                 Translet_send(text_Tra.getText().toString().trim(), SelectLang_Code);
                }
            }
        });


        SeekBar seekbar = (SeekBar)v.findViewById(R.id.seekbar);
        seekbar.setMax(7);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress + 20);
                fontSize = progress + 16;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return v;
    }

    private void Translet_send(String toString,String Language_Code) {
        showDialog(getActivity().getResources().getString(R.string.dlg_loading));
        String url =detectlanguage;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        parms.putString("text",toString);
        parms.putString("targetlan",Language_Code);
        MyVolley.init(getActivity());
        mResponse.getResponse(Request.Method.POST, url,
                880,EditTextColor.this, parms, false,false,Params_Object);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }
    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer(int SPEECH_REQUEST_CODE) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1212) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                text_Tra.append(spokenText);
            }
        }
        
        
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_image:
                getActivity().getSupportFragmentManager().popBackStack ("frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                ((CreateFramesActivity)getActivity()).backFromFragment();
                break;
            case R.id.take_text:
                getActivity().getSupportFragmentManager().popBackStack ("frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((Ecards_EditPage)getActivity()).showText(edit_text.getText().toString(), myTypeFace1, mColorPickerViewColor, fontSize, fontType, edit_text.getGravity());
                break;
            case R.id.enter_text:
                style.setVisibility(View.GONE);
                size.setVisibility(View.GONE);
                isStyleVisible = false;
                isFontSize = false;
                if(!isAlign) {
                    alignment.setVisibility(View.VISIBLE);
                    animationSlideUp(R.anim.slide_up , alignment);
                    isAlign = true;
                }else{
                    animationSlideUp(R.anim.slide_down , alignment);
                    alignment.setVisibility(View.GONE);
                    isAlign = false;
                }
//                showTextDialog();
                break;
            case R.id.stylish_text:
                size.setVisibility(View.GONE);
                alignment.setVisibility(View.GONE);
                isAlign = false;
                isFontSize = false;
                if(!isStyleVisible) {
                    style.setVisibility(View.VISIBLE);
                    animationSlideUp(R.anim.slide_up , style);
                    isStyleVisible = true;
                }else{
                    animationSlideUp(R.anim.slide_down , style);
                    style.setVisibility(View.GONE);
                    isStyleVisible = false;
                }
                break;
            case R.id.coloured_text:
                style.setVisibility(View.GONE);
                size.setVisibility(View.GONE);
                alignment.setVisibility(View.GONE);
                isAlign = false;
                isStyleVisible = false;
                isFontSize = false;
                showColourPickerDialogBox();
                break;
            case R.id.size_of_text:
                style.setVisibility(View.GONE);
                alignment.setVisibility(View.GONE);
                isAlign = false;
                isStyleVisible = false;
                if(!isFontSize) {
                    size.setVisibility(View.VISIBLE);
                    animationSlideUp(R.anim.slide_up , size);
                    isFontSize = true;
                }else{
                    size.setVisibility(View.GONE);
                    animationSlideUp(R.anim.slide_down , size);
                    isFontSize = false;
                }
                break;
            case R.id.font_1:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AL Cinderella.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "AL Cinderella.ttf";
                break;
            case R.id.font_2:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/Alice_in_Wonderland_3.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "Alice_in_Wonderland_3.ttf";
                break;
            case R.id.font_3:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AlphaRope.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "AlphaRope.ttf";
                break;
            case R.id.font_4:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/BLACKJAR.TTF");
                edit_text.setTypeface(myTypeFace1);
                fontType = "BLACKJAR.ttf";
                break;
            case R.id.font_5:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/CONNP___.TTF");
                edit_text.setTypeface(myTypeFace1);
                fontType = "CONNP___.ttf";
                break;
            case R.id.font_6:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/CoolDots.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "CoolDots.ttf";
                break;
            case R.id.font_7:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/Fibography_PersonalUse.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "Fibography_PersonalUse.ttf";
                break;
            case R.id.font_8:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/First Crush.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "First Crush.ttf";
                break;
            case R.id.font_9:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/Mf Hug Me Tight.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "Mf Hug Me Tight.ttf";
                break;
            case R.id.font_10:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/NewWalt1003.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "NewWalt1003.ttf";
                break;
            case R.id.font_11:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/Nightmare Before Christmas.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "Nightmare Before Christmas.ttf";
                break;
            case R.id.font_12:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/PrincesS AND THE FROG.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "PrincesS AND THE FROG.ttf";
                break;
            case R.id.font_13:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/Rope5.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "Rope5.ttf";
                break;
            case R.id.font_14:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/So Random!.ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "So Random!.ttf";
                break;
            case R.id.font_15:
                myTypeFace1 = Typeface.createFromAsset(getActivity().getAssets(), "font/Upon A Dream (Maleficent).ttf");
                edit_text.setTypeface(myTypeFace1);
                fontType = "Upon A Dream (Maleficent).ttf";
                break;
            case R.id.okButton:
                edit_text.setTextColor(mColorPickerView.getColor());
                color_picker_dialog.dismiss();
                break;
            case R.id.cancelButton:
                color_picker_dialog.dismiss();
                break;
            case R.id.left_align:
                edit_text.setGravity(Gravity.LEFT);
                break;
            case R.id.center_align:
                edit_text.setGravity(Gravity.CENTER);
                break;
            case R.id. right_align:
                edit_text.setGravity(Gravity.RIGHT);
                break;
            default:
                break;
        }
    }

    private void animationSlideUp(int animation , View view){
        Animation animateIn = AnimationUtils.loadAnimation(getActivity(), animation);
        view.startAnimation(animateIn);
    }


    private void showColourPickerDialogBox(){
        color_picker_dialog = new Dialog(getActivity());
        getActivity().setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        color_picker_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        color_picker_dialog.setContentView(R.layout.color_picker_view_dialog);
        color_picker_dialog.show();

        mColorPickerView = (ColorPickerView)color_picker_dialog. findViewById(R.id.colorpickerview__color_picker_view);
        mOldColorPanelView = (ColorPanelView)color_picker_dialog. findViewById(R.id.colorpickerview__color_panel_old);
        mNewColorPanelView = (ColorPanelView)color_picker_dialog. findViewById(R.id.colorpickerview__color_panel_new);
        mOkButton = (Button)color_picker_dialog. findViewById(R.id.okButton);
        mCancelButton = (Button)color_picker_dialog. findViewById(R.id.cancelButton);

        ((LinearLayout) mOldColorPanelView.getParent()).setPadding(mColorPickerView.getPaddingLeft(), 0, mColorPickerView.getPaddingRight(), 0);

        mColorPickerView.setOnColorChangedListener(this);
        mColorPickerView.setColor(0xFF000000, true);
        mOldColorPanelView.setColor(0xFF000000);

        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onColorChanged(int newColor) {
//        mNewColorPanelView.setVisibility(View.GONE);
        mNewColorPanelView.setColor(mColorPickerView.getColor());
        edit_text.setTextColor(mColorPickerView.getColor());
        mColorPickerViewColor = mColorPickerView.getColor();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                break;
            case 1:
                SelectLang_Code="en";
                break;
            case 2:
                SelectLang_Code="es";
                break;
            case 3:
                SelectLang_Code="hi";
                break;
            case 4:
                SelectLang_Code="th";
                break;
            case 5:
                SelectLang_Code="bn";
                break;
            case 6:
                SelectLang_Code="te";
                break;
            case 7:
                SelectLang_Code="mr";
                break;
            case 8:
                SelectLang_Code="fr";
                break;
            case 9:
                SelectLang_Code="zh";
                break;
            case 10:
                SelectLang_Code="ko";
                break;
            case 11:
                SelectLang_Code="ar";
                break;
            case 12:
                SelectLang_Code="ja";
                break;
            case 13:
                SelectLang_Code="ms";
                break;
            case 14:
                SelectLang_Code="pt";
                break;
            case 15:
                SelectLang_Code="vi";
                break;
            case 16:
                SelectLang_Code="tl";
                break;
            case 17:
                SelectLang_Code="it";
                break;
            default:
                break;
        }








    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        if (requestCode==880)
        {
            cancleDialog();
            edit_text.setText(response);
            Translation_View.setVisibility(View.GONE);
        }
    }
}
