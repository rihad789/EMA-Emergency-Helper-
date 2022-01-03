package com.service.ema;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Location_DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "location.db";
    public static final String TABLE_NAME = "Location_Table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "LATITUDE";
    public static final String COL_3 = "LONGITUDE";


    public Location_DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,LATITUDE TEXT,LONGITUDE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String Latitude, String Longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, Latitude);
        contentValues.put(COL_3, Longitude);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    public boolean updateData(String ID,String Latitude,String Longitude)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,ID);
        contentValues.put(COL_2,Latitude);
        contentValues.put(COL_3,Longitude);

        db.update(TABLE_NAME,contentValues,"ID=?",new String[]{ID});
        return  true;
    }

}

