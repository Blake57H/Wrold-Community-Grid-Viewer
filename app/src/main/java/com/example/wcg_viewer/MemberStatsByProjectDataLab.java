package com.example.wcg_viewer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbHelper;
import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.MemberStatsByProjectTable.Columns;
import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.*;

import java.util.ArrayList;
import java.util.List;

public class MemberStatsByProjectDataLab {
    private static MemberStatsByProjectDataLab sMemberStatsByProjectDataLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private MemberStatsByProjectDataLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MemberStatsByProjectDbHelper(context).getWritableDatabase();
    }

    public static MemberStatsByProjectDataLab getInstance(Context context) {
        if (sMemberStatsByProjectDataLab == null)
            sMemberStatsByProjectDataLab = new MemberStatsByProjectDataLab(context);
        return sMemberStatsByProjectDataLab;
    }

    private DbCursorWrapper queryMemberStatsByProject(String[] columns, String whereClause, String[] whereArgs, String limit) {
        Cursor cursor = mDatabase.query(false, MemberStatsByProjectTable.NAME, columns, whereClause, whereArgs, null, null, null, limit);
        return new DbCursorWrapper(cursor);
    }

    public String getBadgeUrlByShortName(String shortName) {
        DbCursorWrapper cursor = queryMemberStatsByProject(new String[]{Columns.BADGE_URL}, Columns.PROJECT_SHORT_NAME + " = ? ", new String[]{shortName}, null);
        cursor.moveToFirst();
        return cursor.getBadgeUrl();
    }

    public List<ProjectItem> getProjects() {
        DbCursorWrapper cursor = queryMemberStatsByProject(null, null, null, null);
        cursor.moveToFirst();
        List<ProjectItem> projectItems = new ArrayList<>();
        while (cursor.isAfterLast()) {
            projectItems.add(cursor.getProjectItem());
            cursor.moveToNext();
        }
        return projectItems;
    }
}
