package obj.quickblox.sample.chat.java.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;
import obj.quickblox.sample.chat.java.ui.adapter.UsersAdapter;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;
import obj.quickblox.sample.chat.java.utils.qb.QbUsersHolder;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatInfoActivity extends BaseActivity {
    private static final String EXTRA_DIALOG = "dialog";

    private ListView usersListView;
    private QBChatDialog qbDialog;
    ArrayList<Contact_Model> contact_list;
    public static void start(Context context, QBChatDialog qbDialog) {
        Intent intent = new Intent(context, ChatInfoActivity.class);
        intent.putExtra(EXTRA_DIALOG, qbDialog);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        actionBar.setDisplayHomeAsUpEnabled(true);
        usersListView = findViewById(R.id.list_chat_info_users);
        qbDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG);
        contact_list = new ArrayList<>();
        getDialog();
        loadContactNo();
    }

    private void getDialog() {
        String dialogID = qbDialog.getDialogId();
        ChatHelper.getInstance().getDialogById(dialogID, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                qbDialog = qbChatDialog;

                loadContactNo();

            }

            @Override
            public void onError(QBResponseException e) {
              //  ToastUtils.shortToast(e.getMessage());
                finish();
            }
        });
    }

    private void buildUserList() {
        List<Integer> userIds = qbDialog.getOccupants();
        List<QBUser> users = QbUsersHolder.getInstance().getUsersByIds(userIds);



        UsersAdapter adapter = new UsersAdapter(this, users);
        usersListView.setAdapter(adapter);
    }


    private void loadContactNo()
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
            buildUserList();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
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