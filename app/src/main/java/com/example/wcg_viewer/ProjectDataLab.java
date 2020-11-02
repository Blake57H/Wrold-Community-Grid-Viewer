package com.example.wcg_viewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbHelper;
import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.MemberStatsByProjectTable.Columns;
import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.*;
import com.example.wcg_viewer.RecentTaskDatabase.RecentTaskDbSchema;

import java.util.ArrayList;
import java.util.List;

public class ProjectDataLab {
    private static final String TAG = "ProjectDataLab";
    private static ProjectDataLab sProjectDataLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private ProjectDataLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MemberStatsByProjectDbHelper(context).getWritableDatabase();
    }

    public static ProjectDataLab getInstance(Context context) {
        if (sProjectDataLab == null)
            sProjectDataLab = new ProjectDataLab(context);
        return sProjectDataLab;
    }

    private DbCursorWrapper queryMemberStatsByProject(String[] columns, String whereClause, String[] whereArgs, String limit) {
        Cursor cursor = mDatabase.query(false, MemberStatsByProjectTable.NAME, columns, whereClause, whereArgs, null, null, null, limit);
        return new DbCursorWrapper(cursor);
    }

    private void clearProjectStats() {
        Log.d(TAG, "clearProjectStats: clearing");
        mDatabase.delete(MemberStatsByProjectTable.NAME, null, null);
    }

    private void addProjectStat(ProjectItem item) {
        Log.d(TAG, "addProjectStat: adding");

        ContentValues contentValues = new ContentValues();

        contentValues.put(Columns.PROJECT_SHORT_NAME, item.getProjectShortName());
        contentValues.put(Columns.PROJECT_NAME, item.getProjectName());
        contentValues.put(Columns.RUNTIME, item.getRunTimeInSecond());
        contentValues.put(Columns.POINTS, item.getPoints());
        contentValues.put(Columns.RESULTS, item.getResults());
        if (item.getBadgeFileName() != null) {
            contentValues.put(Columns.BADGE_URL, item.getBadgeUrl());
            contentValues.put(Columns.BADGE_DESCRIPTION, item.getBadgeDescription());
        }

        mDatabase.insert(MemberStatsByProjectTable.NAME, null, contentValues);
    }

    public void addProjectStat(List<ProjectItem> items) {
        Log.d(TAG, "addProjectStat: adding " + items.size() + " project(s)");
        for (ProjectItem item : items
        ) {
            addProjectStat(item);
        }
    }

    public void replaceProjectStats(List<ProjectItem> items) {
        Log.d(TAG, "replaceProjectStats");
        clearProjectStats();
        addProjectStat(items);
    }


    public String getBadgeUrlByShortName(String shortName) {
        DbCursorWrapper cursor = queryMemberStatsByProject(new String[]{Columns.BADGE_URL}, Columns.PROJECT_SHORT_NAME + " = ? ", new String[]{shortName}, null);
        cursor.moveToFirst();
        return cursor.getBadgeUrl();
    }

    public List<ProjectItem> getProjects() {
        Log.d(TAG, "getProjects: loading");
        DbCursorWrapper cursor = queryMemberStatsByProject(null, null, null, null);
        cursor.moveToFirst();
        List<ProjectItem> projectItems = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            projectItems.add(cursor.getProjectItem());
            cursor.moveToNext();
        }
        Log.d(TAG, "getProjects: loaded " + projectItems.size() + " items");
        return projectItems;
    }


}
