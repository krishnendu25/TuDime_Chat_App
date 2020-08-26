package obj.quickblox.sample.chat.java.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_TABLE_NAME = "users";
    public static final String DB_COLUMN_ID = "ID";
    public static final String DB_COLUMN_USER_FULL_NAME = "userFullName";
    public static final String DB_COLUMN_USER_LOGIN = "userLogin";
    public static final String DB_COLUMN_USER_ID = "userID";
    public static final String DB_COLUMN_USER_PASSWORD = "userPass";
    public static final String DB_COLUMN_USER_TAG = "userTag";
    public static final String DB_QB_TABLE = "ScheduleTask";
    public static final String DB_QB_USER_ID = "DB_QB_USER_ID";
    public static final String DB_EVENT_NAME = "DB_EVENT_NAME";
    public static final String DB_EVENT_DESC = "DB_EVENT_DESC";
    public static final String DB_EVENT_TIME = "DB_EVENT_TIME";
    public static final String DB_EVENT_DATE = "DB_EVENT_DATE";
    public static final String DB_EVENT_NOTIFIATION = "DB_EVENT_NOTIFIATION";
    /* RecipientId*/
    public static final String DB_QBChat_TABLE = "QBChatDialog_Archive";
    public static final String DB_QBChat_USER_ID = "DB_QBChat_USER_ID";
    public static final String DB_QBChatDialog = "DB_QBChatDialog";
    public static final String CHAT_DIALOG_ALL = "chat_dialog_id";
    public static final String DB_CALL_TABLE = "DB_CALL_TABLE";
    /* RecipientId*/
    public static final String SERECT_CHAT_TABLE = "SERECT_CHAT_TABLE";
    public static final String SERECT_CHAT_QBChat_USER_ID = "SERECT_CHAT_QBChat_USER_ID";
    public static final String SERECT_CHAT_QBChatDialog = "SERECT_CHAT_QBChatDialog";
    public static final String SERECT_CHAT_CHAT_DIALOG_ALL = "SERECT_CHAT_CHAT_DIALOG_ALL";


    /* Chat_Dialog*/
    public static final String QB_LOCAL_TABLE = "QBlocaltable";
    public static final String QB_LOCAL_USER_ID = "QBlocaluserid";
    public static final String QB_LOCAL_ChatDialog = "QBlocalchatdialog";
    public static final String QB_LOCAL_DIALOG_ALL = "QBlocalchatdialogall";


    public static final String DB_CALL_QBUSER = "DB_CALL_QBUSER";
    public static final String DB_CALL_ID = "DB_CALL_ID";
    public static final String DB_CALL_RECIPIENTID = "DB_CALL_RECIPIENTID";
    public static final String DB_CALL_RECIPIENTNAME = "DB_CALL_RECIPIENTNAME";
    public static final String DB_CALL_TYPE = "DB_CALL_TYPE";
    public static final String DB_CALL_COUNT = "DB_CALL_COUNT";
    public static final String DB_CALL_START_TIME = "DB_CALL_START_TIME";
    public static final String CALL_STATUS="CALL_STATUS";



    public static final String DB_QB_UPDATE_CONTACT = "DB_QB_UPDATE_CONTACT";
    public static final String DB_FULLNAME = "DB_FULLNAME";
    public static final String DB_EMAIL = "DB_EMAIL";
    public static final String DB_LOGIN = "DB_LOGIN";
    public static final String DB_PHONE = "DB_PHONE";
    public static final String DB_WEBSITE = "DB_WEBSITE";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    public static final String DB_IS_QBUSER = "DB_IS_QBUSER";


    private static final String DB_NAME = "groupchatwebrtcDB";
    private String TAG = DbHelper.class.getSimpleName();


    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DB_TABLE_NAME + " ("
                + DB_COLUMN_ID + " integer primary key autoincrement,"
                + DB_COLUMN_USER_ID + " integer,"
                + DB_COLUMN_USER_LOGIN + " text,"
                + DB_COLUMN_USER_PASSWORD + " text,"
                + DB_COLUMN_USER_FULL_NAME + " text,"
                + DB_COLUMN_USER_TAG + " text"
                + ");");
        db.execSQL("create table " + DB_QB_TABLE + " ("
                + DB_COLUMN_ID + " integer primary key autoincrement,"
                + DB_QB_USER_ID + " text ,"
                + DB_EVENT_NAME + " integer,"
                + DB_EVENT_DESC + " text,"
                + DB_EVENT_TIME + " text,"
                + DB_EVENT_DATE + " text,"
                + DB_EVENT_NOTIFIATION + " text"
                + ");");
        db.execSQL("create table " + DB_QBChat_TABLE + " ("
                + DB_QBChat_USER_ID + " text ,"
                + DB_QBChatDialog + " text ,"
                + CHAT_DIALOG_ALL + " text"
                + ");");

        db.execSQL("create table " + QB_LOCAL_TABLE + " ("
                + QB_LOCAL_USER_ID + " text ,"
                + QB_LOCAL_ChatDialog + " text ,"
                + QB_LOCAL_DIALOG_ALL + " text"
                + ");");

        db.execSQL("create table " + SERECT_CHAT_TABLE + " ("
                + SERECT_CHAT_QBChat_USER_ID + " text ,"
                + SERECT_CHAT_QBChatDialog + " text ,"
                + SERECT_CHAT_CHAT_DIALOG_ALL + " text"
                + ");");
        db.execSQL("create table " + DB_CALL_TABLE + " ("
                + DB_CALL_ID + " integer primary key autoincrement,"
                + DB_CALL_QBUSER + " text ,"
                + DB_CALL_RECIPIENTNAME + " text,"
                + DB_CALL_RECIPIENTID + " text,"
                + DB_CALL_COUNT + " text,"
                + CALL_STATUS + " text,"
                + DB_CALL_START_TIME + " text,"
                + DB_CALL_TYPE + " text"
                + ");");
        db.execSQL("create table " + DB_QB_UPDATE_CONTACT + " ("
                + DB_FULLNAME + " text,"
                + DB_EMAIL + " text ,"
                + DB_LOGIN + " text,"
                + DB_PHONE + " text,"
                + DB_WEBSITE + " text,"
                + DB_PASSWORD + " text,"
                + DB_IS_QBUSER + " text"
                + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + DB_QB_UPDATE_CONTACT);
        db.execSQL(" DROP TABLE IF EXISTS " + DB_CALL_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + DB_CALL_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + DB_QBChat_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + DB_QB_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }
}