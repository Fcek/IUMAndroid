package com.example.iumandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "iumproject.db";

    public MyDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS = "CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "manufacturer TEXT, name TEXT, price REAL, amount INTEGER, " +
                "created INTEGER, updated INTEGER, serverid INTEGER, count INTEGER)";
        db.execSQL(CREATE_PRODUCTS);
        String CREATE_SSID = "CREATE TABLE ssid (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "ssid INTEGER)";
        db.execSQL(CREATE_SSID);
        db.execSQL("INSERT INTO ssid (ssid) VALUES (0)");
        String CREATE_ACCOUNT = "CREATE TABLE currentuser (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, role TEXT, logged INTEGER)";
        db.execSQL(CREATE_ACCOUNT);
        db.execSQL("INSERT INTO currentuser (id, role, logged) VALUES (1, 'client', 0)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

//    public void truncate(SQLiteDatabase db){
//        db.execSQL("DROP TABLE products");
//        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                "manufacturer TEXT, name TEXT, price REAL, amount INTEGER, " +
//                "created INTEGER, updated INTEGER, serverid INTEGER)");
//    }
}
