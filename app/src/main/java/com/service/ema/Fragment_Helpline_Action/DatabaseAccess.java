package com.service.ema.Fragment_Helpline_Action;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccess {
    private static final String TABLE_NAME ="Helpline_Table" ;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

   private DatabaseAccess(Context context) {
        this.openHelper = new Helpline_Database(context);
    }
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    //Get all data from contact
    public Cursor getAllData() {
        Cursor res = database.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
}
