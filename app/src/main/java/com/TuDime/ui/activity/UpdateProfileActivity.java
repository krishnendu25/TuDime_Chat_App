package com.TuDime.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.NetworkOperation.IJSONParseListener;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.utils.Constant;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

import static com.TuDime.constants.ApiConstants.update_user_profile;

public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener, IJSONParseListener {
    @BindView(R.id.Profile_BACK_PIC)
    ImageView ProfileBACKPIC;
    @BindView(R.id.Profile_BACK_PIC_BTN)
    TextView ProfileBACKPICBTN;
    private File photo;
    private Bitmap mImageBitmap;
    private File mImageFileToUpload;
    private TextView txvPhoneNum,profilePhotoGallery;
    private File file1, file;
    private ImageView image;
    private EditText check_password;
    private Bitmap bitmap;
    private Uri mImageUri;
    private TextView tvProfile;
    protected static final int CAMERA_REQUEST = 201, GALLERY_PICTURE = 215;
    String Pic_toggole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
        hideActionbar();
        insi();
    }


    private void insi() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        image = (ImageView) findViewById(R.id.imgProfileUpdate);
        txvPhoneNum = (TextView) findViewById(R.id.txvPhoneNum);
        findViewById(R.id.back_image).setOnClickListener(this);
        txvPhoneNum.setOnClickListener(this);
        profilePhotoGallery  = findViewById(R.id.profilePhotoGallery);
        profilePhotoGallery.setOnClickListener(this);
        findViewById(R.id.imgCall).setOnClickListener(this);
        findViewById(R.id.imgPhotoEdit).setOnClickListener(this);
        findViewById(R.id.imgProfileUpdate).setOnClickListener(this);
        findViewById(R.id.imgNameEdit).setOnClickListener(this);
        findViewById(R.id.imgStatusEdit).setOnClickListener(this);
        findViewById(R.id.complete_profile).setOnClickListener(this);
        findViewById(R.id.pripub_photo).setOnClickListener(this);
        findViewById(R.id.notification).setOnClickListener(this);
        tvProfile = findViewById(R.id.tvProfile);
        if ( SharedPrefsHelper.getInstance().getQbUser().getLogin().contains("@"))
        {
            ((TextView) findViewById(R.id.txvPhoneNum)).setText(SharedPrefsHelper.getInstance().getQbUser().getLogin());

        }else
        {
            ((TextView) findViewById(R.id.txvPhoneNum)).setText(SharedPrefsHelper.getInstance().getCountryCode() + SharedPrefsHelper.getInstance().getQbUser().getLogin());

        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPhotoEdit:
                Pic_toggole = "0";
                showPhotoOptionsDialog();
                break;

            case R.id.imgProfileUpdate:


                break;

            case R.id.imgNameEdit:
                startActivity(new Intent(UpdateProfileActivity.this, UpdateProfileNameActivity.class));
                break;

            case R.id.imgStatusEdit:
                startActivity(new Intent(UpdateProfileActivity.this, StatusChangeActivity.class));
                break;

            case R.id.complete_profile:

                break;

            case R.id.pripub_photo:

                showPhotoSettingChooser();
                break;
            case R.id.notification:

                break;
            case R.id.profilePhotoGallery:
                startActivity(new Intent(this,MyPhotoLibery.class));
                break;
            case R.id.back_image:
                finish();
                break;

        }
    }

    private void showPhotoSettingChooser() {
        final AlertDialog dialog;
        final String[] array = new String[]{getString(R.string.public_profile), getString(R.string.private_profile)};
        final AlertDialog.Builder ab = new AlertDialog.Builder(UpdateProfileActivity.this);
        int selectProfile=-1;
        if(tvProfile.getText().toString().equalsIgnoreCase(getString(R.string.public_profile))){
            selectProfile=0;
        }else{
            selectProfile=1;
        }
        ab.setTitle(getString(R.string.profile1));
        ab.setSingleChoiceItems(array, selectProfile, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        SharedPrefsHelper.getInstance().setProfilePhotostatus(array[0]);
                        tvProfile.setText(array[i]);
                        hitProfileGetApi(getString(R.string.public_profile));
                        //((TextView) findViewById(R.id.tvNickName)).
                        break;
                    case 1:
                        SharedPrefsHelper.getInstance().setProfilePhotostatus(array[1]);
                        tvProfile.setText(array[i]);
                        hitProfileGetApi("private");
                        break;
                }
                dialogInterface.dismiss();
            }
        });
        ab.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog = ab.create();
        dialog.show();
    }

    private void hitProfileGetApi(String aPublic) {
        showProgressDialog(R.string.load);
        final int min = 1;
        final int max = 96595966;
        final String random = String.valueOf(new Random().nextInt((max - min) + 1) + min);
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("userid", SharedPrefsHelper.getInstance().getUSERID());
        parms.putString("privacy_status", aPublic);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, update_user_profile,
                983, this, parms, false, false, Agent_Array_Object);
    }


    private void showPhotoOptionsDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(UpdateProfileActivity.this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, GALLERY_PICTURE);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        try {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera, CAMERA_REQUEST);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
        myAlertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    if (resultCode == RESULT_OK) {
                        try {
                            Uri imageUri = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                            if (bitmap.getWidth() > 480 && bitmap.getHeight() > 640) {
                                bitmap = getResizedBitmap(bitmap);
                                image.setBackground(null);
                                image.setImageBitmap(getResizedBitmap(bitmap));
                                image.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                            } else {
                                image.setBackground(null);
                                image.setImageBitmap(bitmap);
                                image.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                            }

                            hitProfilePicApi(bitmap, Pic_toggole);
                            hitQuickblox(getPath(this, data.getData()));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                case GALLERY_PICTURE:
                    if (resultCode == RESULT_OK) {
                        Uri imageUri = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        try {

                            if (bitmap.getWidth() > 480 && bitmap.getHeight() > 640) {
                                bitmap = getResizedBitmap(bitmap);
                                image.setBackground(null);
                                image.setImageBitmap(getResizedBitmap(bitmap));
                                image.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                            } else {
                                image.setBackground(null);
                                image.setImageBitmap(bitmap);
                                image.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                            }
                            hitProfilePicApi(bitmap, Pic_toggole);
                            hitQuickblox(getPath(this, mImageUri));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void hitQuickblox(String s) {

        File file = new File(s);
        QBContent.uploadFileTask(file, true, null, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int i) {

            }
        }).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                int uploadedFileID = qbFile.getId();
                QBUser user = sharedPrefsHelper.getQbUser();
                user.setId(Integer.parseInt(SharedPrefsHelper.getInstance().getUserId()));
                user.setFileId(uploadedFileID);
                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        // SharedPrefsHelper.getInstance().saveQbUser(qbUser);

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void hitProfilePicApi(Bitmap file, String pic_toggole) {
        showProgressDialog(R.string.load);
        final int min = 1;
        final int max = 96595966;
        final String random = String.valueOf(new Random().nextInt((max - min) + 1) + min);
        JSONObject Agent_Array_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("userid", SharedPrefsHelper.getInstance().getUSERID());
        MyVolley.init(this);
        if (Pic_toggole.equalsIgnoreCase("0")) {
            mResponse.setFile("Cover_pic", Constant.SaveImagetoSDcard(random, file, UpdateProfileActivity.this));


        } else {
            mResponse.setFile("pic1", Constant.SaveImagetoSDcard(random, file, UpdateProfileActivity.this));

        }

        mResponse.getResponse(Request.Method.POST, update_user_profile,
                248, this, parms, false, false, Agent_Array_Object);


    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) 480) / width;
        float scaleHeight = ((float) 640) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject jsonObject, int requestCode) {

        if (requestCode == 248) {
            hideProgressDialog();
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    ToastUtils.shortToast("Picture Changed Successfully");
                    Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }

        }
        if (requestCode == 983) {
            hideProgressDialog();
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    ToastUtils.shortToast("Privacy Status Changed");
                    Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }

        }


        if (requestCode == 447) {
            hideProgressDialog();
            hideProgressDialog();
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    if (jsonObject.getJSONArray("data").getJSONObject(0).length()==0)
                    {
                        SharedPrefsHelper.getInstance().setUserName(SharedPrefsHelper.getInstance().getQbUser().getFullName());
                        SharedPrefsHelper.getInstance().setProfilePhotostatus(getString(R.string.public_profile));
                        SharedPrefsHelper.getInstance().setCurrentStatus(getResources().getString(R.string.status_default_ciaom));
                        ((TextView) findViewById(R.id.tvNickName)).setText(SharedPrefsHelper.getInstance().getUserName());
                        tvProfile.setText(SharedPrefsHelper.getInstance().getProfilePhotostatus());
                    }else
                    {
                        if (!jsonObject.getJSONArray("data").getJSONObject(0).getString("name").equalsIgnoreCase(""))
                        { ((TextView) findViewById(R.id.tvNickName)).setText(jsonObject.getJSONArray("data").getJSONObject(0).getString("name"));
                            SharedPrefsHelper.getInstance().setUserName(jsonObject.getJSONArray("data").getJSONObject(0).getString("name"));
                        }else
                        {   SharedPrefsHelper.getInstance().setUserName(SharedPrefsHelper.getInstance().getQbUser().getFullName());
                            ((TextView) findViewById(R.id.tvNickName)).setText(SharedPrefsHelper.getInstance().getUserName()); }
                        SharedPrefsHelper.getInstance().setProfilePhotostatus(jsonObject.getJSONArray("data").getJSONObject(0).getString("privacy_status"));
                        SharedPrefsHelper.getInstance().setCurrentStatus(jsonObject.getJSONArray("data").getJSONObject(0).getString("Bio"));
                        ((TextView) findViewById(R.id.tvNickName)).setText(SharedPrefsHelper.getInstance().getUserName());
                        tvProfile.setText(SharedPrefsHelper.getInstance().getProfilePhotostatus());
                        if (SharedPrefsHelper.getInstance().getProfilePhotostatus().equals("")) {
                            tvProfile.setText(getString(R.string.public_profile));
                        } else {
                            tvProfile.setText(SharedPrefsHelper.getInstance().getProfilePhotostatus());
                        }
                        if (SharedPrefsHelper.getInstance().getCurrentStatus().equals("")) {
                            ((TextView) findViewById(R.id.txvStatus)).setText(getString(R.string.status_default_ciaom));
                        } else {
                            ((TextView) findViewById(R.id.txvStatus)).setText(SharedPrefsHelper.getInstance().getCurrentStatus());
                        }
                        try {
                            Picasso.get().load(jsonObject.getJSONArray("data").getJSONObject(0).getString("Cover_pic_url")).placeholder(R.drawable.navigation_drawer_pro_pic)
                                    .into(image);
                            Picasso.get().load(jsonObject.getJSONArray("data").getJSONObject(0).getString("pic1_url")).placeholder(R.drawable.navigation_drawer_cover_bg)
                                    .into(ProfileBACKPIC);
                            hit_quickblox(jsonObject.getJSONArray("data").getJSONObject(0).getString("Cover_pic_url"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }






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


    protected void onResume() {
        super.onResume();
        Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT;
        Log.i("URI", uri + "");
        String result = uri + "";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {

            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length - 1];
            final String[] dat = imgary.split("%3A");

            final String docId = dat[1];
            final String type = dat[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {

            } else if ("audio".equals(type)) {
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    dat[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    private void hit_quickblox(String s) {
        QBUser qbUser = new QBUser();
        qbUser.setId(Integer.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
        qbUser.setWebsite(s);
        QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                SharedPrefsHelper.getInstance().saveQbUser(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @OnClick(R.id.Profile_BACK_PIC_BTN)
    public void onViewClicked() {
        Pic_toggole = "1";
        showPhotoOptionsDialog();
    }
}




