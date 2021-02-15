package com.TuDime.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import com.TuDime.ui.Model.Calender_Model;
import com.TuDime.ui.Model.Call_model;
import com.TuDime.ui.Model.QB_UPDATE_CONTACT_MODEL;

import static com.TuDime.db.DbHelper.CALL_STATUS;
import static com.TuDime.db.DbHelper.CHAT_DIALOG_ALL;
import static com.TuDime.db.DbHelper.DB_CALL_COUNT;
import static com.TuDime.db.DbHelper.DB_CALL_QBUSER;
import static com.TuDime.db.DbHelper.DB_CALL_RECIPIENTID;
import static com.TuDime.db.DbHelper.DB_CALL_RECIPIENTNAME;
import static com.TuDime.db.DbHelper.DB_CALL_START_TIME;
import static com.TuDime.db.DbHelper.DB_CALL_TABLE;
import static com.TuDime.db.DbHelper.DB_CALL_TYPE;
import static com.TuDime.db.DbHelper.DB_EMAIL;
import static com.TuDime.db.DbHelper.DB_EVENT_DATE;
import static com.TuDime.db.DbHelper.DB_EVENT_DESC;
import static com.TuDime.db.DbHelper.DB_EVENT_NAME;
import static com.TuDime.db.DbHelper.DB_EVENT_NOTIFIATION;
import static com.TuDime.db.DbHelper.DB_EVENT_TIME;
import static com.TuDime.db.DbHelper.DB_FULLNAME;
import static com.TuDime.db.DbHelper.DB_IS_QBUSER;
import static com.TuDime.db.DbHelper.DB_LOGIN;
import static com.TuDime.db.DbHelper.DB_PASSWORD;
import static com.TuDime.db.DbHelper.DB_PHONE;
import static com.TuDime.db.DbHelper.DB_QBChatDialog;
import static com.TuDime.db.DbHelper.DB_QBChat_USER_ID;
import static com.TuDime.db.DbHelper.DB_QB_USER_ID;
import static com.TuDime.db.DbHelper.DB_WEBSITE;
import static com.TuDime.db.DbHelper.QB_LOCAL_ChatDialog;
import static com.TuDime.db.DbHelper.QB_LOCAL_DIALOG_ALL;
import static com.TuDime.db.DbHelper.QB_LOCAL_USER_ID;
import static com.TuDime.db.DbHelper.SERECT_CHAT_CHAT_DIALOG_ALL;
import static com.TuDime.db.DbHelper.SERECT_CHAT_QBChatDialog;
import static com.TuDime.db.DbHelper.SERECT_CHAT_QBChat_USER_ID;


public class QbUsersDbManager {
    private static String TAG = QbUsersDbManager.class.getSimpleName();

    private static QbUsersDbManager instance;
    private Context mContext;

    private QbUsersDbManager(Context context) {
        this.mContext = context;
    }

    public static QbUsersDbManager getInstance(Context context) {
        if (instance == null) {
            instance = new QbUsersDbManager(context);
        }

        return instance;
    }

    public ArrayList<QBUser> getAllUsers() {
        ArrayList<QBUser> allUsers = new ArrayList<>();
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DbHelper.DB_TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int userIdColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_ID);
            int userLoginColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_LOGIN);
            int userPassColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_PASSWORD);
            int userFullNameColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_FULL_NAME);
            int userTagColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_TAG);
            do {
                QBUser qbUser = new QBUser();
                qbUser.setFullName(c.getString(userFullNameColIndex));
                qbUser.setLogin(c.getString(userLoginColIndex));
                qbUser.setId(c.getInt(userIdColIndex));
                qbUser.setPassword(c.getString(userPassColIndex));
                StringifyArrayList<String> tags = new StringifyArrayList<>();
                tags.add(c.getString(userTagColIndex));
                qbUser.setTags(tags);
                allUsers.add(qbUser);
            } while (c.moveToNext());
        }
        c.close();
        dbHelper.close();

        return allUsers;
    }

    public QBUser getUserById(Integer userId) {
        QBUser qbUser = null;
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DbHelper.DB_TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int userIdColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_ID);
            int userLoginColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_LOGIN);
            int userPassColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_PASSWORD);
            int userFullNameColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_FULL_NAME);
            int userTagColIndex = c.getColumnIndex(DbHelper.DB_COLUMN_USER_TAG);

            do {
                if (c.getInt(userIdColIndex) == userId) {
                    qbUser = new QBUser();
                    qbUser.setFullName(c.getString(userFullNameColIndex));
                    qbUser.setLogin(c.getString(userLoginColIndex));
                    qbUser.setId(c.getInt(userIdColIndex));
                    qbUser.setPassword(c.getString(userPassColIndex));
                    StringifyArrayList<String> tags = new StringifyArrayList<>();
                    tags.add(c.getString(userTagColIndex).split(","));
                    qbUser.setTags(tags);
                    break;
                }
            } while (c.moveToNext());
        }

        c.close();
        dbHelper.close();

        return qbUser;
    }

    public void saveAllUsers(ArrayList<QBUser> allUsers, boolean needRemoveOldData) {
        if (needRemoveOldData) {
            clearDB();
        }

        for (QBUser qbUser : allUsers) {
            saveUser(qbUser);
        }
        Log.d(TAG, "saveAllUsers");
    }

    public void saveUser(QBUser qbUser) {
        ContentValues cv = new ContentValues();
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put(DbHelper.DB_COLUMN_USER_FULL_NAME, qbUser.getFullName());
        cv.put(DbHelper.DB_COLUMN_USER_LOGIN, qbUser.getLogin());
        cv.put(DbHelper.DB_COLUMN_USER_ID, qbUser.getId());
        cv.put(DbHelper.DB_COLUMN_USER_PASSWORD, qbUser.getPassword());
        cv.put(DbHelper.DB_COLUMN_USER_TAG, qbUser.getTags().getItemsAsString());

        db.insert(DbHelper.DB_TABLE_NAME, null, cv);
        dbHelper.close();
    }

    public void clearDB() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.DB_TABLE_NAME, null, null);
        dbHelper.close();
    }

    public ArrayList<QBUser> getUsersByIds(List<Integer> usersIds) {
        ArrayList<QBUser> qbUsers = new ArrayList<>();

        for (Integer userId : usersIds) {
            if (getUserById(userId) != null) {
                qbUsers.add(getUserById(userId));
            }
        }

        return qbUsers;
    }

    //insert data in CAL Table
    public boolean insertDataCAL(Calender_Model calender_model) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_QB_USER_ID, calender_model.getDB_QB_USER_ID());
        contentValues.put(DB_EVENT_TIME, calender_model.getDB_EVENT_TIME());
        contentValues.put(DB_EVENT_NAME, calender_model.getDB_EVENT_NAME());
        contentValues.put(DB_EVENT_DESC, calender_model.getDB_EVENT_DESC());
        contentValues.put(DB_EVENT_DATE, calender_model.getDB_EVENT_DATE());
        contentValues.put(DB_EVENT_NOTIFIATION, calender_model.getDB_EVENT_NOTIFIATION());

      long  result = db.insert("ScheduleTask", null, contentValues);

        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }

    //GET get_event_by_user
    public Cursor get_event_by_user(String userId) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ScheduleTask WHERE DB_QB_USER_ID = '" + userId + "'", null);
        return c;
    }

    //GET get_event_by_date
    public Cursor get_event_by_date(String date,String user_id) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from ScheduleTask" + " WHERE " + "DB_EVENT_DATE='" + date + "'" + " AND " + "DB_QB_USER_ID='" + user_id + "'",null);
        return res;
    }

    public Integer Delete_Event(int id) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int i = db.delete("ScheduleTask", "ID" + "=?", new String[]{Integer.toString(id)});
        return i;
    }

    //SERCET CHAT
    //##################INSERT##################
    public boolean inser_Dialog_Sercet_Chat( String selectedDialog,String s,String Chat_di) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SERECT_CHAT_QBChat_USER_ID,s);
        contentValues.put(SERECT_CHAT_QBChatDialog,selectedDialog.toString());
        contentValues.put(SERECT_CHAT_CHAT_DIALOG_ALL,Chat_di);
        long  result = db.insert("SERECT_CHAT_TABLE", null, contentValues);
        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }

    //Insert
    //Insert Data in QB_LOCAL_TABLE Table

    public boolean insertQB_LOCAL_TABLE( String selectedDialog,String s,String Chat_di) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QB_LOCAL_USER_ID,s);
        contentValues.put(QB_LOCAL_ChatDialog,selectedDialog.toString());
        contentValues.put(QB_LOCAL_DIALOG_ALL,Chat_di);
        long  result = db.insert("QBlocaltable", null, contentValues);
        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }
    public Integer DeleteQB_LOCAL_TABLE(String selectedDialog) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int i = db.delete("QBlocaltable", "QBlocalchatdialog" + "=?", new String[]{selectedDialog.toString()});
        return i;
    }
    public Cursor getQB_LOCAL_TABLE() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT * FROM QBlocaltable", null);
        return c;
    }
    public Cursor get_QB_LOCAL_TABLE(String USER_ID) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM QBlocaltable  WHERE QB_LOCAL_USER_ID = '" + USER_ID  + "'", null);
        return c;
    }

    //Insert
//insert data in QBChatDialog Table
    public boolean insertDB_QBChat_TABLE( String selectedDialog,String s,String Chat_di) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_QBChat_USER_ID,s);
        contentValues.put(DB_QBChatDialog,selectedDialog.toString());
        contentValues.put(CHAT_DIALOG_ALL,Chat_di);
        long  result = db.insert("QBChatDialog_Archive", null, contentValues);
        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }

    public Integer Delete_QBChat_TABLE(String selectedDialog) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int i = db.delete("QBChatDialog_Archive", "DB_QBChatDialog" + "=?", new String[]{selectedDialog.toString()});
        return i;
    }

    public Cursor get_QBChat_TABLE() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT * FROM QBChatDialog_Archive ", null);
        return c;
    }
    public Cursor get_QBChat(String dd) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM QBChatDialog_Archive  WHERE DB_QBChat_USER_ID = '" + dd + "'", null);
        return c;
    }




    //CALLING OPERATION

    //GET CALL_BY_DB_CALL_RECIPIENTID
    public Cursor get_CALL_BY_RECIPIENTID(String RECIPIENTID) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM DB_CALL_TABLE WHERE DB_CALL_RECIPIENTID = '" + RECIPIENTID + "'", null);
        return c;
    }


    /*GET ALL CALL BY QBUSERID*/
    public Cursor get_all_call(String QBUSERID) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM DB_CALL_TABLE WHERE DB_CALL_QBUSER = '" + QBUSERID + "'", null);
        return c;
    }
    /*DELETE ALL CALL BY QBUSERID*/
    public Integer Delete_all_call(String QBUSERID) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int i = db.delete("DB_CALL_TABLE", "DB_CALL_QBUSER" + "=?", new String[]{QBUSERID});
        return i;
    }
    /*INSERT CALL*/

    public boolean insertcall(Call_model call_model) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_CALL_RECIPIENTID, call_model.getDB_CALL_RECIPIENTID());
        contentValues.put(DB_CALL_RECIPIENTNAME, call_model.getDB_CALL_RECIPIENTNAME());
        contentValues.put(DB_CALL_TYPE, call_model.getDB_CALL_TYPE());
        contentValues.put(DB_CALL_COUNT, call_model.getDB_CALL_COUNT());
        contentValues.put(DB_CALL_START_TIME, call_model.getDB_CALL_START_TIME());
        contentValues.put(DB_CALL_QBUSER, call_model.getDB_CALL_QBUSER());
        contentValues.put(CALL_STATUS, call_model.getCall_status());

        long  result = db.insert("DB_CALL_TABLE", null, contentValues);

        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updatedetails(String count, String RECIPIENTID,String Call_status)
    {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(DB_CALL_COUNT, count);
        args.put(DB_CALL_TYPE, Call_status);
        return db.update(DB_CALL_TABLE, args, DB_CALL_RECIPIENTID + "=" + RECIPIENTID, null)>0;
    }


    //DB_QB_UPDATE_CONTACT

    public boolean UPDATE_CONTACT(QB_UPDATE_CONTACT_MODEL qb) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_FULLNAME, qb.getFullName());
        contentValues.put(DB_EMAIL, qb.getEmail());
        contentValues.put(DB_LOGIN, qb.getLogin());
        contentValues.put(DB_PHONE, qb.getPhone());
        contentValues.put(DB_WEBSITE, qb.getWebsite());
        contentValues.put(DB_PASSWORD, qb.getPassword());
        contentValues.put(DB_IS_QBUSER, qb.get_Is_QBuser());

        long  result = db.insert("DB_QB_UPDATE_CONTACT", null, contentValues);

        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor get_CONTACT_MODEL() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM DB_QB_UPDATE_CONTACT", null);
        return c;
    }

    public void deleteall()
    {
        ArrayList<String> table = new ArrayList<>();
        table.add("QBChatDialog_Archive");
        table.add("DB_CALL_TABLE");

        for (int i=0 ; i<table.size();i++)
        {
            DbHelper dbHelper = new DbHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("delete from "+ table.get(i).toString().trim());
            db.close();
        }


    }



}