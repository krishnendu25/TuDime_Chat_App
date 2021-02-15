package com.TuDime.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.TuDime.R;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.services.CallService;
import com.TuDime.ui.Model.Contact_Model;
import com.TuDime.ui.adapter.CheckboxUsersAdapter;
import com.TuDime.constants.Consts;
import com.TuDime.utils.PermissionsChecker;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;
import com.TuDime.utils.chat.ChatHelper;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class SelectUsersActivity extends BaseActivity {
    public static final String EXTRA_QB_USERS = "qb_users";
    public static final String EXTRA_CHAT_NAME = "chat_name";
    public static final int MINIMUM_CHAT_OCCUPANTS_SIZE = 2;
    public static final int PRIVATE_CHAT_OCCUPANTS_SIZE = 2;
    ArrayList<Contact_Model> contact_list;
    private static final int PER_PAGE_SIZE = 100;

    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc string updated_at";

    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);

    private static final String EXTRA_QB_DIALOG = "qb_dialog";
    private List<Contact_Model> Local_Contact=null;
    private ListView usersListView;
    private ProgressBar progressBar;
    private CheckboxUsersAdapter usersAdapter;
    private List<QBUser> users;
    private long lastClickTime = 0l;
    private QBChatDialog qbChatDialog;
    private String chatName;
    private QbUsersDbManager dbManager;
    private PermissionsChecker checker;
    public static void start(Context context) {
        Intent intent = new Intent(context, SelectUsersActivity.class);
        context.startActivity(intent);
    }

    /**
     * Start activity for picking users
     *
     * @param activity activity to return result
     * @param code     request code for onActivityResult() method
     *                 <p>
     *                 in onActivityResult there will be 'ArrayList<QBUser>' in the intent extras
     *                 which can be obtained with SelectPeopleActivity.EXTRA_QB_USERS key
     */
    public static void startForResult(Activity activity, int code) {
        startForResult(activity, code, null);
    }

    public static void startForResult(Activity activity, int code, QBChatDialog dialog) {
        Intent intent = new Intent(activity, SelectUsersActivity.class);
        intent.putExtra(EXTRA_QB_DIALOG, dialog);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_select_users);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_QB_DIALOG);
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        checker = new PermissionsChecker(getApplicationContext());
        initUi();

        loadContactNo();
        loadUsersFromQb();


    }

    private ArrayList loadContactNo()
    {
        contact_list.clear();

        Cursor cursor = null;
        try {
            cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            cursor.moveToFirst();
            do {
                String idContact = cursor.getString(contactIdIdx);
                String name = cursor.getString(nameIdx);
                String phoneNumber = cursor.getString(phoneNumberIdx);
                Contact_Model model = new Contact_Model(name,phoneNumber,false,"");
                contact_list.add(model);
                //...
            } while (cursor.moveToNext());
           return contact_list;
        } catch (Exception e) {
            e.printStackTrace();
            return contact_list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    private void initUi() {

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0e87bb")));
        progressBar = findViewById(R.id.progress_select_users);
        usersListView = findViewById(R.id.list_select_users);
        Local_Contact = new ArrayList<>();
        TextView listHeader = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.include_list_hint_header, usersListView, false);
        listHeader.setText(R.string.select_users_list_hint);
        usersListView.addHeaderView(listHeader, null, false);

        if (isEditingChat()) {
            setActionBarTitle(getString(R.string.select_users_edit_chat));

        } else {
            setActionBarTitle(getString(R.string.select_users_create_chat));
        }
        actionBar.setDisplayHomeAsUpEnabled(true);


        contact_list = new ArrayList<>();

    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return super.onOptionsItemSelected(item);
        }
        lastClickTime = SystemClock.uptimeMillis();

        switch (item.getItemId()) {
            case R.id.menu_select_people_action_done:
                if (usersAdapter != null) {
                    List<QBUser> users = new ArrayList<>(usersAdapter.getSelectedUsers());
                    if (users.size() < MINIMUM_CHAT_OCCUPANTS_SIZE) {
                        ToastUtils.shortToast(R.string.select_users_choose_users);
                    } else {
                        if (qbChatDialog == null && users.size() > PRIVATE_CHAT_OCCUPANTS_SIZE) {
                            showChatNameDialog();
                        } else {
                            passResultToCallerActivity();
                        }
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showChatNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_chat_name, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextGroupName = dialogView.findViewById(R.id.edittext_dialog_name);

        dialogBuilder.setTitle(R.string.dialog_enter_chat_name);
        dialogBuilder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editTextGroupName.getText())) {
                    ToastUtils.shortToast(R.string.dialog_enter_chat_name);
                } else {
                    chatName = editTextGroupName.getText().toString();
                    passResultToCallerActivity();
                    dialog.dismiss();
                }
            }
        });

        dialogBuilder.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.create().show();
    }

    private void passResultToCallerActivity() {
        Intent result = new Intent();
        ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());
        result.putExtra(EXTRA_QB_USERS, selectedUsers);
        chatName=selectedUsers.get(0).getFullName();
        if (!TextUtils.isEmpty(chatName)) {
            result.putExtra(EXTRA_CHAT_NAME, chatName);
        }
        setResult(RESULT_OK, result);
        finish();
    }

    private void loadUsersFromQb() {
        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_VALUE));

        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);
        qbPagedRequestBuilder.setPerPage(PER_PAGE_SIZE);

        progressBar.setVisibility(View.VISIBLE);
        QBUsers.getUsers(qbPagedRequestBuilder, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                SelectUsersActivity.this.users = users;
                if (qbChatDialog != null) {
                    // update occupants list form server
                    getDialog();
                    dbManager.saveAllUsers(Set_Name_ToQb_User(users), true);
                } else {
                    dbManager.saveAllUsers(Set_Name_ToQb_User(users), true);

                    usersAdapter = new CheckboxUsersAdapter(SelectUsersActivity.this, Set_Name_ToQb_User(users));
                    updateUsersAdapter();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadUsersFromQb();
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDialog() {
        String dialogID = qbChatDialog.getDialogId();
        ChatHelper.getInstance().getDialogById(dialogID, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                SelectUsersActivity.this.qbChatDialog = qbChatDialog;
                loadUsersFromDialog(qbChatDialog.getOccupants());
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_dialog_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadUsersFromQb();
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateUsersAdapter() {
        if (qbChatDialog != null) {
            usersAdapter.addSelectedUsers(qbChatDialog.getOccupants());
        }
        usersListView.setAdapter(usersAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private boolean isEditingChat() {
        return getIntent().getSerializableExtra(EXTRA_QB_DIALOG) != null;
    }

    private void loadUsersFromDialog(List<Integer> userIdsList) {
        QBUsers.getUsersByIDs(userIdsList, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                usersAdapter = new CheckboxUsersAdapter(SelectUsersActivity.this, Set_Name_ToQb_User(qbUsers));
                for (QBUser user : qbUsers) {
                    usersAdapter.addUserToUserList(user);
                }
                updateUsersAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_users_dialog_error, e, null);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        boolean isIncomingCall = SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_INCOMING_CALL, false);
        if (isCallServiceRunning(CallService.class)) {
            Log.d("", "CallService is running now");
            CallActivity.start(this, isIncomingCall);
        }
        clearAppNotifications();

    }

    private boolean isCallServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void clearAppNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
    private ArrayList<QBUser>  Set_Name_ToQb_User(ArrayList<QBUser> users)
    {
        ArrayList<QBUser> temp_list = new ArrayList<>();


        Local_Contact = getContactsForm_Phone(getApplicationContext());

        for (int i=0 ; i<Local_Contact.size() ; i++)
        {
            boolean flag=false;
            int position=0;
            for(int j=0 ; j<users.size() ; j++)
            {
                if (users.get(j).getLogin().equals(Local_Contact.get(i).getContact_Number()))
                {
                    position=j;
                    flag=true;
                    break;
                }
            }
            if (flag)
            {
                users.get(position).setFullName(Local_Contact.get(i).getContact_Name());
                temp_list.add(users.get(position));
                Local_Contact.remove(i);
            }


        }


        return temp_list;

    }
    //Get_Phone_Number_Form_Ph
    public List<Contact_Model> getContactsForm_Phone(Context ctx) {
        List<Contact_Model> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));
                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    if (cursorInfo != null) {
                        while (cursorInfo.moveToNext()) {
                            Contact_Model info = new Contact_Model();

                            info.Contact_Name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            info.Contact_Number = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                           /* info.photo = photo;
                            info.photoURI = pURI;*/
                            info.QB_user_id = "   ";
                            list.add(info);
                        }
                    }
                    if (cursorInfo != null) {
                        cursorInfo.close();
                    }
                }
            }
            cursor.close();
        }


        return list;
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