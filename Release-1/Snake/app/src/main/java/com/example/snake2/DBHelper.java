package com.example.snake2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "myDataBase";

    public static final String COL_ID = "_id";

    public static final String TABLE_OF_RECORDS = "records_table";

    public static final String COL_LABYRINTH = "labyrinth";
    public static final String COL_SCORE = "score";

    public static final String WITHOUT_LAB = "without";
    public static final String BOX_LAB = "box";
    public static final String TUNNEL_LAB = "tunnel";
    public static final String MILL_LAB = "mill";
    public static final String ROOM_LAB = "room";
    public static final String CHANGE_LAB = "change";

    public static final String TABLE_OF_SELECTED_PARAMETERS = "param_table";

    public static final String COL_TYPE = "type";
    public static final String COL_SPEED = "speed";
    public static final String COL_CONTROL = "control";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_OF_RECORDS + "(" +
                COL_ID + " integer primary key, " +
                COL_LABYRINTH + " text, " +
                COL_SCORE + " integer"
                + ")");
        db.execSQL("create table " + TABLE_OF_SELECTED_PARAMETERS + "(" +
                COL_ID + " integer primary key, " +
                COL_TYPE + " integer, " +
                COL_SPEED + " integer, " +
                COL_CONTROL + " integer" +
                ")");
        Cursor cursor = db.query(TABLE_OF_SELECTED_PARAMETERS, null, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TYPE, 0);
            contentValues.put(COL_SPEED, 0);
            contentValues.put(COL_CONTROL, 0);
            db.insert(TABLE_OF_SELECTED_PARAMETERS, null, contentValues);
        } else {
            cursor.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_OF_RECORDS);
        db.execSQL("drop table if exists " + TABLE_OF_SELECTED_PARAMETERS);
        onCreate(db);
    }
}
