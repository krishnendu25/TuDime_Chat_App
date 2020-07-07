package obj.quickblox.sample.chat.java.Internet_Calling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;


public class PaymentTable extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "Payment_Database";
    private final static String TABLE_NAME = "Payment_History";
    private final static int DATABASE_VERSION = 13;
    private final static String AMOUNT = "amount";
    private final static String DATE = "date";
    private final static String TIME = "time";
    private final static String CREDIT = "credit";
    private final static String RECEIPT = "receipt";
    private final static String TOTAL_CREDIT = "total_credit";
    private final static String ID = "id";

    public PaymentTable(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RECEIPT + " TEXT,"
                + AMOUNT + " TEXT,"
                + DATE + " TEXT,"
                + CREDIT + " TEXT,"
                + TIME + " TEXT,"
        + TOTAL_CREDIT + " TEXT )";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.e(DATABASE_NAME, "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        Log.e(DATABASE_NAME, "database created again");
        onCreate(sqLiteDatabase);
    }

    public long insertData(PAyment_model model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AMOUNT ,model.getAmount() );
        cv.put(DATE , model.getDate());
        cv.put(TIME , model.getTime());
        cv.put(CREDIT , model.getCredit());
        cv.put(RECEIPT , model.getReceipt());
        cv.put(TOTAL_CREDIT  ,model.getTotal_credit());

        Long isinserted = db.insert(TABLE_NAME,null,cv);
        Log.d(TABLE_NAME , "Data Inserted Successfully");
        return isinserted;
    }

    public ArrayList<PAyment_model> readData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        ArrayList<PAyment_model> list = new ArrayList<>();
        try {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++){
                PAyment_model model = new PAyment_model();
                model.setAmount(cursor.getString(cursor.getColumnIndex(AMOUNT)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
                model.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
                model.setCredit(cursor.getString(cursor.getColumnIndex(CREDIT)));
                model.setReceipt(cursor.getString(cursor.getColumnIndex(RECEIPT)));
                model.setTotal_credit(cursor.getString(cursor.getColumnIndex(TOTAL_CREDIT)));
                list.add(model);
                cursor.moveToNext();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            cursor.close();
            return list;
        }
    }

    public int sumOfCredit(){
        int amount = 0;
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select sum(" + CREDIT + ") from " + TABLE_NAME , null);

        if(cursor.moveToFirst()) {
            amount += cursor.getInt(0);
            cursor.moveToNext();
        }
        else {
            amount = -1;
        }
        cursor.close();
        return amount;
    }

    public String getTotal_credit(){
        int total_credit =0;
        String s ="500";
        /*SQLiteDatabase db =this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

       // String selectQuery = "SELECT " + TOTAL_CREDIT +" FROM " + TABLE_NAME + " ORDER BY " + ID + " DESC LIMIT 1";
        String selectQuery = "SELECT " + CREDIT +" FROM " + TABLE_NAME + " WHERE " + ID + " = '" + cursor.getCount() + "'";

        Cursor c = db.rawQuery(selectQuery , null);
        if (c != null && c.moveToFirst()) {
            total_credit = c.getInt(0);
            s = c.getString(c.getColumnIndex(AMOUNT));

        }
        else{
            s = "$ 0.00";
        }*/
      return s;
    }

    public String getTotal_amount(){
        int total_credit =0;
        String s ="";
        SQLiteDatabase db =this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        // String selectQuery = "SELECT " + TOTAL_CREDIT +" FROM " + TABLE_NAME + " ORDER BY " + ID + " DESC LIMIT 1";
        String selectQuery = "SELECT " + AMOUNT +" FROM " + TABLE_NAME + " WHERE " + ID + " = '" + cursor.getCount() + "'";

        Cursor c = db.rawQuery(selectQuery , null);
        if (c != null && c.moveToFirst()) {
            total_credit = c.getInt(0);
            s = c.getString(c.getColumnIndex(AMOUNT));

        }
        else{
            s = "$ 0.00";
        }
        return s;
    }

    public int updateTotalCredit(float ttl_credit){
        SQLiteDatabase db = this.getWritableDatabase();
        String s1 = getTotal_credit();
        String total = "";
        byte[] b = s1.getBytes();
        for(int j=0;j<b.length;j++){
            if(b[j]>45 && b[j]<58){
                char c = (char)b[j];
                total = total + c;
            }
        }
        float s = Float.parseFloat(total) - ttl_credit;
        String s2 = "$ " + String.format("%.2f" , s);

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        ContentValues values = new ContentValues();
        values.put(CREDIT, s2);

        int status = db.update(TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(cursor.getCount())});
        return status;


//        String mytable = "UPDATE " + TABLE_NAME + " SET " + TOTAL_CREDIT + " ='"
//                + s1 + "' ORDER BY " + ID + " DESC LIMIT 1 "  ;
//        db.execSQL(mytable);
    }

    public int updateTotalAmount(float ttl_credit){
        SQLiteDatabase db = this.getWritableDatabase();
        String s1 = getTotal_credit();
        String total = "";
        byte[] b = s1.getBytes();
        for(int j=0;j<b.length;j++){
            if(b[j]>45 && b[j]<58){
                char c = (char)b[j];
                total = total + c;
            }
        }
        float s = Float.parseFloat(total) - ttl_credit;
        String s2 = "$ " + String.format("%.2f" , s);

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        ContentValues values = new ContentValues();
        values.put(AMOUNT, s2);

        int status = db.update(TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(cursor.getCount())});
        return status;


//        String mytable = "UPDATE " + TABLE_NAME + " SET " + TOTAL_CREDIT + " ='"
//                + s1 + "' ORDER BY " + ID + " DESC LIMIT 1 "  ;
//        db.execSQL(mytable);
    }
}
