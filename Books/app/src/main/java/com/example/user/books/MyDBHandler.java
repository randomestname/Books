package com.example.user.books;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mydata.db";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_READ = "read";
    private Context context;

    MyDBHandler(Context context1, SQLiteDatabase.CursorFactory factory) {
        super(context1, DATABASE_NAME, factory, DATABASE_VERSION);
        context = context1;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NOTIFICATIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATA + " TEXT NOT NULL, " +
                COLUMN_READ + " INTEGER DEFAULT 0 " +
                ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS + ";");
        onCreate(sqLiteDatabase);
    }

    void deleteTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS + ";");
        db.close();
    }

    //Add a new notification to table
    int addNotification(NotiItemObject notiItemObject) {
        int notiid;
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, notiItemObject.getData());
        values.put(COLUMN_READ, notiItemObject.isRead());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTIFICATIONS, null, values);
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1;", null);
        c.moveToLast();
        notiid = c.getInt(c.getColumnIndex(COLUMN_ID));
        db.close();
        c.close();
        return notiid;
    }

    //Delete a notification from table
    void deleteNotification(int position) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_ID + " = " + position + ";");
        db.close();
    }

    //Update reads of a notification
    void updateNotification(int start, int end) {
        if(start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NOTIFICATIONS + " SET " + COLUMN_READ + " = 1 WHERE " +
            COLUMN_ID + " BETWEEN " + start + " AND " + end + ";");
        db.close();
    }

    //Load notifications
    List<NotiItemObject> getNotificationData() {
        List<NotiItemObject> notis = new ArrayList<>();
        NotiItemObject noti;
        String notidata;
        int notiid;
        boolean notiread;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_ID + " DESC;";

        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NOTIFICATIONS + "';");
        Cursor c = db.rawQuery(query, null);
        for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
            notidata = c.getString(c.getColumnIndex(COLUMN_DATA));
            //notidata = c.getString(c.getColumnIndex(COLUMN_DATA));
            notiid = c.getInt(c.getColumnIndex(COLUMN_ID));
            notiread = (c.getInt(c.getColumnIndex(COLUMN_READ)) == 1);
            noti = new NotiItemObject(notidata);
            noti.setId(notiid);
            if(notiread)
                noti.setRead();
            else
                ((Global) context.getApplicationContext()).incUnReadNoti();
            notis.add(0, noti);
        }
        db.close();
        c.close();
        return notis;
    }

}
