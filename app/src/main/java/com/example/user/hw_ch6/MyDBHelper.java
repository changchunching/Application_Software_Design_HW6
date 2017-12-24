package com.example.user.hw_ch6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 2017/12/14.
 */

public class MyDBHelper extends SQLiteOpenHelper{
    private static final String database = "hw6.db";
    private static final int version = 16;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    public MyDBHelper(Context context){
        this(context,database,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE shopTable(_id integer primary key autoincrement,"+
                "shopname text no null,"+
                "phone text no null,"+
                "address text no null)");
        db.execSQL("CREATE TABLE productTable(_id integer primary key autoincrement,"+
                "judgename text no null,"+
                "pos text no null,"+
                "productname text no null,"+
                "description text no null,"+
                "price text no null,"+
                "time text no null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS shopTable");
        db.execSQL("DROP TABLE IF EXISTS productTable");
        onCreate(db);
    }
}
