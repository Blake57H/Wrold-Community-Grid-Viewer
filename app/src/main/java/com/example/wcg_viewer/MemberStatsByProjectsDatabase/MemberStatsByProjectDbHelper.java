package com.example.wcg_viewer.MemberStatsByProjectsDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.MemberStatsByProjectTable.*;

import static com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.MemberStatsByProjectTable.NAME;

public class MemberStatsByProjectDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "memberStatsByProject.db";
    private static final int VERSION = 1;


    public MemberStatsByProjectDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NAME +
                " (" + Columns.PROJECT_SHORT_NAME + " primary key, " +
                Columns.PROJECT_NAME + " not null, " +
                Columns.RUNTIME + " not null," +
                Columns.POINTS + " not null," +
                Columns.RESULTS + " not null," +
                Columns.BADGE_URL + "," +
                Columns.BADGE_DESCRIPTION +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + NAME);
        onCreate(db);
    }
}
