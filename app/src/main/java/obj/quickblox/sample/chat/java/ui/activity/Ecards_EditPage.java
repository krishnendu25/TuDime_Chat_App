package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.quickblox.core.request.QueryRule;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;

import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.Prefrences.CiaoPrefrences;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.ApiConstants;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.ui.Callback.ChatConstants;
import obj.quickblox.sample.chat.java.ui.Callback.OnAudioRecordedListener;
import obj.quickblox.sample.chat.java.ui.Callback.OnButtonClicked;
import obj.quickblox.sample.chat.java.ui.fragments.EditTextColor;
import obj.quickblox.sample.chat.java.util.FileUtils;
import obj.quickblox.sample.chat.java.util.ImageOnlyOptionsDialog;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.ExifUtil;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.VoiceMemoDialog;

import static obj.quickblox.sample.chat.java.constants.AppConstants.REQUEST_CODE_CAMERA;

public class Ecards_EditPage extends BaseActivity implements View.OnClickListener , IJSONParseListener {
    private Uri uriFilePath;
    private File file, file_imaage, file_voice;
    private int PIC_CROP = 1;
    private int TEXT_VALUE = 2;
    private ImageView image, image_frame;
    private TextView textView;
    private int SIGNATURE_CODE = 3;
    private Dialog dialog;
    private boolean isAddFrame = false;
    private int fontcolor;
    private String imageEncoded = "", fontStyle = "", signature_url = "", image_path = "", voice_url = "", frame_value = "0";
    private MediaPlayer mp;
    private File file1;
    private String value;
    private byte[] dataSign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecards__edit_page);
        hideActionbar();
        image = (ImageView) findViewById(R.id.image_photo);
        image_frame = (ImageView) findViewById(R.id.image_frame);
        textView = (TextView) findViewById(R.id.text_part);
        findViewById(R.id.photo_part).setOnClickListener(this);
        findViewById(R.id.text_part_layout).setOnClickListener(this);
        findViewById(R.id.signature_part).setOnClickListener(this);
        findViewById(R.id.preview).setOnClickListener(this);
        findViewById(R.id.go).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.add_frame).setOnClickListener(this);
        findViewById(R.id.linear_voice).setOnClickListener(this);
        findViewById(R.id.frame1).setOnClickListener(this);
        findViewById(R.id.frame2).setOnClickListener(this);
        findViewById(R.id.frame3).setOnClickListener(this);
        findViewById(R.id.frame4).setOnClickListener(this);
        findViewById(R.id.frame5).setOnClickListener(this);
        findViewById(R.id.play_button).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.pause_button).setOnClickListener(this);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.photo_part:
                showPhotoOptionsDialog();
                break;
            case R.id.text_part_layout:
                getSupportFragmentManager().beginTransaction().replace(R.id.conatiner, new EditTextColor()).addToBackStack("frag").commit();
                break;
            case R.id.signature_part:
                startActivityForResult(new Intent(Ecards_EditPage.this, AddSignatureEcards.class), SIGNATURE_CODE);
                break;
            case R.id.linear_voice:
                showVoiceRecorderPopup();
                break;
            case R.id.add_frame:
                if (!isAddFrame) {
                    findViewById(R.id.frames_change).setVisibility(View.VISIBLE);
                    animationSlideUp(R.anim.slide_up, findViewById(R.id.frames_change));
                    isAddFrame = true;
                } else {
                    findViewById(R.id.frames_change).setVisibility(View.GONE);
                    animationSlideUp(R.anim.slide_down, findViewById(R.id.frames_change));
                    isAddFrame = false;
                }
                break;
            case R.id.preview:

                break;
            case R.id.go:
                    showShareDialogBox();
                break;
            case R.id.back_button:
                finish();
                break;
            case R.id.frame1:
                image_frame.setImageResource(R.drawable.frame_1_ecards);
                frame_value = "0";
                break;
            case R.id.frame2:
                image_frame.setImageResource(R.drawable.frame_2);
                frame_value = "1";
                break;
            case R.id.frame3:
                image_frame.setImageResource(R.drawable.frame_3);
                frame_value = "2";
                break;
            case R.id.frame4:
                image_frame.setImageResource(R.drawable.frame_7);
                frame_value = "3";
                break;
            case R.id.frame5:
                image_frame.setImageResource(R.drawable.frame_5);
                frame_value = "4";
                break;
            case R.id.play_button:
                mp = new MediaPlayer();
                try {
                    findViewById(R.id.play_button).setVisibility(View.GONE);
                    findViewById(R.id.pause_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.play_txt).setVisibility(View.GONE);
                    findViewById(R.id.pause_txt).setVisibility(View.VISIBLE);
                    mp.setDataSource(file_voice.getAbsolutePath());
                    mp.prepare();
                    mp.start();
                    int i = mp.getDuration();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mp.release();
                                mp = null;
                                findViewById(R.id.play_button).setVisibility(View.VISIBLE);
                                findViewById(R.id.pause_button).setVisibility(View.GONE);
                                findViewById(R.id.play_txt).setVisibility(View.VISIBLE);
                                findViewById(R.id.pause_txt).setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, i);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pause_button:
                mp.release();
                mp = null;
                findViewById(R.id.play_button).setVisibility(View.VISIBLE);
                findViewById(R.id.pause_button).setVisibility(View.GONE);
                findViewById(R.id.play_txt).setVisibility(View.VISIBLE);
                findViewById(R.id.pause_txt).setVisibility(View.GONE);
                break;
            case R.id.delete:
                File f = new File(file_voice.getAbsolutePath());
                f.delete();
                findViewById(R.id.linear_play).setVisibility(View.GONE);
                findViewById(R.id.linear_voice).setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }





    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject jsonObject, int requestCode)
    {
        if (requestCode==969)
        {
            hideProgressDialog();
            try {

                if (jsonObject.getString("status").equals("1")) {
                    voice_url = jsonObject.getString("message");
                    hitApi(file_imaage, value);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }

        }

        if (requestCode==951)
        {
            try {
                hideProgressDialog();
                if (!jsonObject.getString("status").equals("1")) {
                    return;
                }
                if (value.equals("go")) {
                        Intent share = new Intent("android.intent.action.SEND");
                        share.setType(HTTP.PLAIN_TEXT_TYPE);
                        share.putExtra("android.intent.extra.TEXT", jsonObject.getString("msg"));
                        Ecards_EditPage.this.startActivity(Intent.createChooser(share, Ecards_EditPage.this.getString(R.string.share_with)));
                        return;
                } else if (value.equals("pre")) {
                    Intent i = new Intent(Ecards_EditPage.this, EcardsWebView.class);
                    i.putExtra("type", "custom");
                    i.putExtra("url", jsonObject.getString("msg"));
                    Ecards_EditPage.this.startActivity(i);
                    return;
                } else if (value.equals("tudime")) {
                    SharedPrefsHelper.getInstance().set_E_CARD_URL(jsonObject.getString("msg"));
                  Intent intent = new Intent(this, DashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(intent);
                    return;
                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (requestCode==753)
        {
            hideProgressDialog();
            try {
                if (jsonObject.getString("status").equals("1")) {
                    Ecards_EditPage.this.signature_url = jsonObject.getString("message");
                    if (Ecards_EditPage.this.file_voice != null) {
                        Ecards_EditPage.this.hitUploadFileApiRequest(Ecards_EditPage.this.file_voice, value);
                        return;
                    } else {
                        Ecards_EditPage.this.hitApi(Ecards_EditPage.this.file_imaage, value);
                        return;
                    }
                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }



    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
    } private void animationSlideUp(int animation, View view) {
        Animation animateIn = AnimationUtils.loadAnimation(this, animation);
        view.startAnimation(animateIn);
    }

    private void showShareDialogBox() {
        dialog = new Dialog(Ecards_EditPage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share_ecards);
        dialog.show();

        dialog.findViewById(R.id.share_anywhere).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!imageEncoded.equals("")) {
                        value="go";
                        hitSignatureApi(file, "go");
                    } else if (file_voice != null) {
                        value="go";
                        hitUploadFileApiRequest(file_voice, "go");

                    } else {
                        value="go";
                        hitApi(file_imaage, "go");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    value="go";
                    hitApi(file_imaage, "go");
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.share_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!imageEncoded.equals("")) {
                        value="tudime";
                        hitSignatureApi(file, "tudime");
                    } else if (file_voice != null) {
                        value="tudime";
                        hitUploadFileApiRequest(file_voice, "tudime");
                    } else {
                        value="tudime";
                        hitApi(file_imaage, "tudime");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    value="tudime";
                    hitApi(file_imaage, "tudime");
                }
                dialog.dismiss();
            }
        });

    }



    public void showText(String str, Typeface myTypeFace1, int mColorPickerViewColor, int fontSize, String textfontStyle, int alignment) {
        findViewById(R.id.removing_part1).setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.text_part_layout).setBackground(null);
        } else {
            findViewById(R.id.text_part_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        textView.setText(str);
        textView.setTextColor(mColorPickerViewColor);
        textView.setTypeface(myTypeFace1);
        textView.setTextSize(fontSize);
        textView.setGravity(alignment);
        fontStyle = textfontStyle;
        fontcolor = mColorPickerViewColor;
    }

    private void showPhotoOptionsDialog() {
        ImageOnlyOptionsDialog dialog = new ImageOnlyOptionsDialog();
        dialog.setonButtonClickListener(new OnButtonClicked() {
            @Override
            public void onButtonCLick(int buttonId) {
                switch (buttonId) {
                    case R.id.btnCamera:
                        startCamera();
                        break;
                    case R.id.btnGallery:
                        pickFromGallery();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show(getSupportFragmentManager(), ImageOnlyOptionsDialog.class.getSimpleName());
    }

    public void startCamera() {
        try{
           Intent intent1 =  new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent1, REQUEST_CODE_CAMERA);


        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, AppConstants.REQUEST_PICK_IMAGE_MULTIPLE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            cropIntent.setClassName("com.android.camera", "com.android.camera.CropImage");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.putExtra("return-data", true);
            File f = new File(Environment.getExternalStorageDirectory(), "/TuDimeCroppedImages");
            if (!f.isDirectory()) {
                f.mkdir();
            }
            File pathToImage = new File(f, "cropped.png");
            try {
                pathToImage.createNewFile();
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            Uri uri1 = Uri.fromFile(pathToImage);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, anfe.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap  bitmap = (Bitmap) data.getExtras().get("data");
                    this.image_path  = Constant.SaveImagetoSDcard("IMG_" + Calendar.getInstance().getTimeInMillis(), bitmap, Ecards_EditPage.this);
                    image.setImageBitmap(bitmap);
                    this.file_imaage = new File(new URI("file://" + this.image_path.replace(" ", "%20")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == AppConstants.REQUEST_PICK_IMAGE_MULTIPLE) {
            if (resultCode == RESULT_OK) {
                try {
                    findViewById(R.id.removing_part).setVisibility(View.GONE);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    image_path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    Glide.with(Ecards_EditPage.this).load(image_path).into(image);
                    file_imaage = new File(new URI("file://" + image_path.replace(" ", "%20")));
//                    performCrop(data.getData());
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PIC_CROP) {
            if (resultCode == RESULT_OK) {
                try {
                    findViewById(R.id.removing_part).setVisibility(View.GONE);
                    String filePath = Environment.getExternalStorageDirectory() + "/TuDimeCroppedImages/cropped.png";
                    Bitmap thumbnail = BitmapFactory.decodeFile(filePath);
                    String file = saveBitmap(thumbnail);
                    Bitmap orientedBitmap = ExifUtil.rotateBitmap(file, thumbnail);
                    String file1 = saveBitmap(orientedBitmap);
                    image.setImageURI(Uri.parse(file1));
                    image_path = String.valueOf(file1);
                    file_imaage = new File(new URI("file://" + image_path.replace(" ", "%20")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SIGNATURE_CODE) {
            if (resultCode == RESULT_OK) {
                findViewById(R.id.removing_part2).setVisibility(View.GONE);
                findViewById(R.id.signature_part_relative).setBackgroundColor(getResources().getColor(R.color.transparent));
                Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("byteArray"), 0, data.getByteArrayExtra("byteArray").length);
                ((ImageView) findViewById(R.id.signature_part)).setImageBitmap(b);
                String file1 = saveBitmap(b);
                imageEncoded = String.valueOf(file1);
                try {
                    file = new File(new URI("file://" + imageEncoded.replace(" ", "%20")));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String saveBitmap(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_signature");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = "signature.jpeg";
        String path3 = "";
        File file = new File(myDir, fname);
//
        if (bitmap != null) {
            try {
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    int imageWidth = bitmap.getWidth();
                    int imageHeight = bitmap.getHeight();


                    int newWidth = getResources().getDisplayMetrics().widthPixels;
                    float scaleFactor = (float) newWidth / (float) imageWidth;
                    int newHeight = (int) (imageHeight * scaleFactor);

                    Bitmap b2 = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                    b2.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    String path2 = MediaStore.Images.Media.insertImage(this.getApplicationContext().getContentResolver(), b2, "title", null);
                    Uri uri1 = Uri.parse(path2);
                    path3 = getRealPathFromURI(this.getApplicationContext(), uri1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path3;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);

            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }




    private void showVoiceRecorderPopup() {
        VoiceMemoDialog dialog = new VoiceMemoDialog();
        dialog.setOnAudioRecordedListener(new OnAudioRecordedListener() {
            @Override
            public void onAudioRecorded(File file) {
                if (file != null && file.length() > 0) {
                    copyFileToAppMediaAndhitApi(file, ChatConstants.MESSAGE_TYPE_AUDIO, "", "");
                }
            }
        });
        dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
    }

    private void copyFileToAppMediaAndhitApi(File file, String fileType, String fileCaption, String thumbBase64) {
        String saveLocation = FileUtils.SENT_DIRECTORY_IMAGE;
        if (file != null) {
            saveLocation = FileUtils.SENT_DIRECTORY_VOICE;
        }
        String copiedFileLocation = Environment.getExternalStorageDirectory(
) + saveLocation;
        boolean isCopied = FileUtils.copyFile(file.getAbsolutePath(), copiedFileLocation);
        if (!isCopied) {
            return;
        }
        file_voice = file;

        if (file_voice != null) {
            findViewById(R.id.linear_voice).setVisibility(View.GONE);
            findViewById(R.id.linear_play).setVisibility(View.VISIBLE);
        }
    }






    private void hitUploadFileApiRequest(final File file, final String value)
    {
        showProgressDialog(R.string.load);
        String url = ApiConstants.BASE_URL1;
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("rule", "upload_image");
        parms.putString("fileType", "3");
        parms.putString(ChatConstants.PARAM_POST_THUMB, "");
        MyVolley.init(this);
        mResponse.setFile("file",file.getAbsolutePath());
        mResponse.getResponse(Request.Method.POST, url,
                969, this, parms, false,false,Agent_Array_Object);




    }
    private void hitSignatureApi(File file, String go)
    {
        showProgressDialog(R.string.load);
        String url = ApiConstants.BASE_URL1;
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("rule", "card_sig");
        MyVolley.init(this);
        mResponse.setFile("file",file.getAbsolutePath());
        mResponse.getResponse(Request.Method.POST, url,
                753, this, parms, false,false,Agent_Array_Object);

    }
    private void hitApi(File file_imaage, String value)
    {
        showProgressDialog(R.string.load);


        String url = ApiConstants.BASE_URL1;
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("rule", "card_edit");
        parms.putString("text", this.textView.getText().toString());
        parms.putString("fontsize", String.valueOf(this.textView.getTextSize()));
        parms.putString("fontcolor", String.format("#%06X", new Object[]{Integer.valueOf(this.fontcolor & ViewCompat.MEASURED_SIZE_MASK)}));
        parms.putString("fontstyle", this.fontStyle);
        parms.putString("file2", this.signature_url);
        parms.putString("frame", this.frame_value);
        parms.putString("category", getIntent().getStringExtra("category"));
        parms.putString("card_id", getIntent().getStringExtra("card_id"));
        parms.putString("voice_msg", this.voice_url);
        if (this.textView.getGravity() == 3) {
            parms.putString("fontalignment","left");
        } else if (this.textView.getGravity() == 17) {
            parms.putString("fontalignment","center");
        } else if (this.textView.getGravity() == 5) {
            parms.putString("fontalignment","right");
        }
        MyVolley.init(this);
        mResponse.setFile("file",file_imaage.getAbsolutePath());
        mResponse.getResponse(Request.Method.POST, url,
                951, this, parms, false,false,Agent_Array_Object);




    }
}
