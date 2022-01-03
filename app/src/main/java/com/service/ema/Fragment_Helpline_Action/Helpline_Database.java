package com.service.ema.Fragment_Helpline_Action;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Helpline_Database extends SQLiteAssetHelper {

    private static final String DATABASE_NAME="Helpline_Database.db";
    private static final int DATABASE_VERSION=1;

    public Helpline_Database(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
}


