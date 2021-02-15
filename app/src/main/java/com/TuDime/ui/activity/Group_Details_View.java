package com.TuDime.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.NetworkOperation.IJSONParseListener;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.managers.DialogsManager;
import com.TuDime.ui.adapter.Groupe_Participants_Adapter;
import com.TuDime.ui.dialog.ProgressDialogFragment;
import com.TuDime.utils.Constant;
import com.TuDime.utils.ToastUtils;
import com.TuDime.utils.chat.ChatHelper;
import com.TuDime.utils.qb.QbDialogHolder;

import static com.TuDime.constants.ApiConstants.UPLOAD_IMAGES;

public class Group_Details_View extends BaseActivity implements IJSONParseListener {

    public static QBChatDialog Group_Dialog;
    @BindView(R.id.profile_picture_iv)
    ImageView profilePictureIv;
    @BindView(R.id.User_Name_tv)
    TextView UserNameTv;
    @BindView(R.id.Perticipants_count)
    TextView PerticipantsCount;
    @BindView(R.id.add_participants)
    TextView addParticipants;
    @BindView(R.id.participants_list)
    ListView participantsList;
    @BindView(R.id.exit_group)
    TextView exitGroup;
    @BindView(R.id.Change_icon_groupe)
    TextView ChangeIconGroupe;
    private DialogsManager dialogsManager;
    private QBSystemMessagesManager systemMessagesManager;
    Groupe_Participants_Adapter groupe_participants_adapter;
    protected static final int PICK_IMAGE_CAMERA = 889, PICK_IMAGE_GALLERY = 441;
    Bitmap bitmap;
    public static String USER_ID_ADD_GROUP ="";
    public  static QBUser qbUser_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__details__view);
        ButterKnife.bind(this);
        hideActionbar();
        Instantiation();
        Set_Details();

    }

    private void Instantiation() {
        dialogsManager = new DialogsManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
    }

    private void Set_Details() {
        PerticipantsCount.setText(String.valueOf(Group_Dialog.getOccupants().size()) + "  Perticipants");
        UserNameTv.setText(Group_Dialog.getName());
        try {
            Picasso.get().load(Group_Dialog.getPhoto()).into(profilePictureIv);
        } catch (Exception e) {
            Picasso.get().load(Group_Dialog.getPhoto()).into(profilePictureIv);
        }

        groupe_participants_adapter = new Groupe_Participants_Adapter(this, Group_Dialog.getOccupants());
        participantsList.setAdapter(groupe_participants_adapter);

    }

    @OnClick({R.id.add_participants, R.id.exit_group,R.id.User_Name_tv, R.id.Change_icon_groupe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_participants:
                open_add_participants();
                break;
            case R.id.exit_group:
                leaveGroupChat();
                break;
            case R.id.User_Name_tv:
                UPDATE_NAME();
                break;
            case R.id.Change_icon_groupe:
                showPhotoOptionsDialog();
                break;
        }
    }

    private void UPDATE_NAME() {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = (this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.set_group_name, null);
        dialogBuilder.setView(dialogView);

        EditText enter_name = dialogView.findViewById(R.id.enter_name);
        Button save = dialogView.findViewById(R.id.save);
        enter_name.setText(Group_Dialog.getName());

        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_name.getText().toString().trim().equalsIgnoreCase(""))
                {
                    ToastUtils.shortToast(R.string.Group_Name);
                }else
                {
                    alertDialog.hide();
                    hit_update_name(enter_name.getText().toString().trim());
                }
            }
        });



        alertDialog.show();

    }

    private void hit_update_name(String trim) {
        showProgressDialog(R.string.load);
        QBChatDialog dialog = new QBChatDialog();
        dialog.setDialogId(Group_Dialog.getDialogId());
        dialog.setName(trim);
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        QBRestChatService.updateChatDialog(dialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog updatedDialog, Bundle bundle) {
                hideProgressDialog();
                Group_Dialog = updatedDialog;
                Set_Details();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });



    }
    private void Update_Dialog_Picture(String image_url) {
        showProgressDialog(R.string.load);
        QBChatDialog dialog = new QBChatDialog();
        dialog.setDialogId(Group_Dialog.getDialogId());
        dialog.setPhoto(image_url);
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        QBRestChatService.updateChatDialog(dialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog updatedDialog, Bundle bundle) {
                hideProgressDialog();
                Group_Dialog = updatedDialog;
                Set_Details();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
    private void open_add_participants()
    {
    Intent intent = new Intent(this,Add_Participants.class);
    startActivityForResult(intent,569);
    }

    private void leaveGroupChat() {
        ProgressDialogFragment.show(getSupportFragmentManager());
        dialogsManager.sendMessageLeftUser(Group_Dialog);

        dialogsManager.sendSystemMessageLeftUser(systemMessagesManager, Group_Dialog);
        Log.d("TAG", "Leaving Dialog");
        ChatHelper.getInstance().exitFromDialog(Group_Dialog, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {
                Log.d("TAG", "Leaving Dialog Successful: " + qbDialog.getDialogId());
                ProgressDialogFragment.hide(getSupportFragmentManager());
                QbDialogHolder.getInstance().deleteDialog(qbDialog);
                Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("TAG", "Leaving Dialog Error: " + e.getMessage());
                ProgressDialogFragment.hide(getSupportFragmentManager());
                showErrorSnackbar(R.string.error_leave_chat, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGroupChat();
                    }
                });
            }
        });
    }


    // Select image from camera and gallery
    private void showPhotoOptionsDialog() {
        androidx.appcompat.app.AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(Group_Details_View.this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, PICK_IMAGE_GALLERY);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        try {
                            Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera, PICK_IMAGE_CAMERA);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
        myAlertDialog.show();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case PICK_IMAGE_CAMERA:
                    if (resultCode == RESULT_OK) {
                        try {
                            Uri imageUri = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                            if (bitmap.getWidth() > 480 && bitmap.getHeight() > 640) {
                                bitmap = getResizedBitmap(bitmap);
                                profilePictureIv.setBackground(null);
                                profilePictureIv.setImageBitmap(getResizedBitmap(bitmap));
                                profilePictureIv.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                            } else {
                                profilePictureIv.setBackground(null);
                                profilePictureIv.setImageBitmap(bitmap);
                                profilePictureIv.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                            }

                            Upload_Group_ICON(bitmap);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case PICK_IMAGE_GALLERY:
                    if (resultCode == RESULT_OK) {
                        Uri imageUri = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        try {

                            if (bitmap.getWidth() > 480 && bitmap.getHeight() > 640) {
                                bitmap = getResizedBitmap(bitmap);
                                profilePictureIv.setBackground(null);
                                profilePictureIv.setImageBitmap(getResizedBitmap(bitmap));
                                profilePictureIv.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                            } else {
                                profilePictureIv.setBackground(null);
                                profilePictureIv.setImageBitmap(bitmap);
                                profilePictureIv.setBackground(getResources().getDrawable(R.drawable.pro_pic_default_icon));
                            }
                            Upload_Group_ICON(bitmap);

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 569:
                    if (resultCode == RESULT_OK) {

                   String USER_ID = data.getStringExtra("myResult");
                        Log.e("myResult",USER_ID);
                   /*     showProgressDialog(R.string.load);
                   QBUser user = QbUsersHolder.getInstance().getUserById(Integer.parseInt(USER_ID));

                        QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
                        qbRequestBuilder.addUsers(user);
                        QBRestChatService.updateChatDialog(Group_Dialog, qbRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            Group_Dialog=qbChatDialog;
                            hideProgressDialog();
                            finish();
                           startActivity(getIntent());
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                hideProgressDialog();
                            }
                        });*/

                    }
                    break;

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void Upload_Group_ICON(Bitmap bitmap) {
        showProgressDialog(R.string.load);
        final int min = 1;
        final int max = 96595966;
        final String random = String.valueOf(new Random().nextInt((max - min) + 1) + min);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task", "image_upload");
        parms.putString("custom_data", "");
        parms.putString("chatdialog_id", Group_Dialog.getDialogId().toString());
        MyVolley.init(this);
        mResponse.setFile("chat_dialog_picture", Constant.SaveImagetoSDcard(random, this.bitmap, this));
        mResponse.getResponse(Request.Method.POST, UPLOAD_IMAGES,
                875, this, parms, false, false, Params_Object);

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == 875) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    Hit_Load_Details(Group_Dialog.getDialogId());
                } else {
                    ToastUtils.shortToast(response.getString("error_message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (requestCode == 203) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    JSONObject data = response.getJSONObject("data");
                    String Image_URL = data.getString("img_link");
                    Update_Dialog_Picture(Image_URL);

                } else {
                    ToastUtils.shortToast(response.getString("error_message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void Hit_Load_Details(String dialogId) {
        showProgressDialog(R.string.load);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("task", "get_link");
        parms.putString("chatdialog_id", Group_Dialog.getDialogId().toString());
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, UPLOAD_IMAGES,
                203, this, parms, false, false, Params_Object);

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

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
    protected void onResume() {
        super.onResume();



                Runnable  runnable = new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        if (qbUser_add!=null)
                        { showProgressDialog(R.string.load);
                            QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
                            qbRequestBuilder.addUsers(qbUser_add);
                            QBRestChatService.updateChatDialog(Group_Dialog, qbRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                    Group_Dialog=qbChatDialog;
                                    hideProgressDialog();
                                    finish();
                                    startActivity(getIntent());
                                }
                                @Override
                                public void onError(QBResponseException e) {
                                    hideProgressDialog();
                                }
                            });
                        }


                    }
                };
                Handler  handler = new Handler();
                handler.postDelayed(runnable, 6000);


    }
}
