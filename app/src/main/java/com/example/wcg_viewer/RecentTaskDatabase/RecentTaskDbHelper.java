package com.example.wcg_viewer.RecentTaskDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wcg_viewer.RecentTaskDatabase.RecentTaskDbSchema.*;

import androidx.annotation.Nullable;

public class RecentTaskDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "recentTasks.db";


    public RecentTaskDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + RecentTaskTable.NAME +
                " (" + RecentTaskTable.Columns.RESULT_ID + " primary key, " +
                RecentTaskTable.Columns.RESULT_NAME + " not null," +
                RecentTaskTable.Columns.APP_NAME + " not null," +
                RecentTaskTable.Columns.DEVICE_ID + " not null," +
                RecentTaskTable.Columns.DEVICE_NAME + " not null," +
                RecentTaskTable.Columns.SENT_TIME + " not null," +
                RecentTaskTable.Columns.REPORT_DEADLINE + "," +
                RecentTaskTable.Columns.RECEIVED_TIME + "," +
                RecentTaskTable.Columns.CPU_TIME + " not null," +
                RecentTaskTable.Columns.ELAPSED_TIME + " not null," +
                RecentTaskTable.Columns.RESULT_STATUS_OUTCOME + " not null," +
                RecentTaskTable.Columns.RESULT_SERVER_STATE + " not null," +
                RecentTaskTable.Columns.RESULT_VALIDATE_STATE + " not null," +
                RecentTaskTable.Columns.CLAIMED_CREDIT + " not null," +
                RecentTaskTable.Columns.GRANTED_CREDIT + " not null" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+ RecentTaskTable.NAME);
        onCreate(sqLiteDatabase);
    }
}
