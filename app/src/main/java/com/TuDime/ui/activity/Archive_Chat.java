package com.TuDime.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.android.volley.VolleyError;
import com.quickblox.chat.model.QBChatDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import com.TuDime.R;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.ui.Callback.Popup_click_adapter;
import com.TuDime.ui.Model.Archive_Model;
import com.TuDime.ui.adapter.Archive_Adapter;

public class Archive_Chat extends BaseActivity implements Popup_click_adapter {
    RecyclerView Archive_Chat;
    private QbUsersDbManager dbManager ;
    ArrayList<Archive_Model> Arc_list = new ArrayList<>();
    Archive_Adapter archive_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_chat);
        Instantiation();
        actionBar.setTitle(getResources().getString(R.string.Archived_Chat));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void Instantiation()
    {
        dbManager = QbUsersDbManager.getInstance(this);
        Archive_Chat =findViewById(R.id.Archive_Chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Archive_Chat.this,LinearLayoutManager.VERTICAL,false);
        Archive_Chat.setLayoutManager(layoutManager);
        List_Populate();



    }

    void List_Populate(){
        Cursor cursor = dbManager.get_QBChat_TABLE();
        if (cursor.getCount()!=0)
        {Arc_list.clear();
            while (cursor.moveToNext())
            {
                Archive_Model archive_model = new Archive_Model();
                archive_model.setCHAT_DIALOG_ALL(cursor.getString(2));
                archive_model.setDB_QBChat_USER_ID(cursor.getString(0));
                archive_model.setDB_QBChatDialog(cursor.getString(1));
                Arc_list.add(archive_model);
            }
            archive_adapter = new Archive_Adapter(this,Arc_list);
            Archive_Chat.setAdapter(archive_adapter);
        }
    }

    @Override
    public void onLong_Click(View view, int Position, QBChatDialog selectedDialog, View fview)
    {
        final PopupMenu popup = new PopupMenu(this, fview);
        popup.getMenuInflater().inflate(R.menu.archive_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();

                if (i == R.id.item1) {
                 dbManager.Delete_QBChat_TABLE(selectedDialog.getDialogId());
                    Arc_list.remove(Position);
                    archive_adapter.notifyDataSetChanged();
                    List_Populate();
                }
                else {
                    return onMenuItemClick(item);
                }
                return true;
            }
        });

        popup.show();

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
