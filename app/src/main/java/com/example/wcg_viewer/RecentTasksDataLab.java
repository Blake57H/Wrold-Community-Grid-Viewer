package com.example.wcg_viewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wcg_viewer.RecentTaskDatabase.RecentTaskDbHelper;
import com.example.wcg_viewer.RecentTaskDatabase.RecentTaskDbSchema.*;

import java.util.ArrayList;
import java.util.List;

public class RecentTasksDataLab {
    private static RecentTasksDataLab sRecentTasksDataLab;
    private static final String TAG = "RecentTasksDataLab";
    private final SQLiteDatabase mDatabase;

    private RecentTasksDataLab(Context context) {
        Log.d(TAG, "RecentTasksDataLab: new instance");
        Context context1 = context.getApplicationContext();
        mDatabase = new RecentTaskDbHelper(context1).getWritableDatabase();
    }

    public static RecentTasksDataLab getInstance(Context context) {
        if (sRecentTasksDataLab == null) sRecentTasksDataLab = new RecentTasksDataLab(context);
        return sRecentTasksDataLab;
    }

    private DbCursorWrapper queryRecentTasks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RecentTaskTable.NAME,
                null, whereClause, whereArgs, null, null,
                RecentTaskTable.Columns.SENT_TIME + " desc");
        return new DbCursorWrapper(cursor);
    }

    public List<ResultItem> getResultItems() {
        List<ResultItem> items = new ArrayList<>();

        try (DbCursorWrapper cursor = queryRecentTasks(null, null)) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                items.add(cursor.getResultItem());
                cursor.moveToNext();
            }
        }
        return items;
    }

    public void clearRecentTasks() {
        mDatabase.delete(RecentTaskTable.NAME, null, null);
    }

    /* replace the old data set with the newly downloaded data*/
    public void replaceRecentTasks(List<ResultItem> items) {
        clearRecentTasks();
        addRecentTasks(items);
    }

    /* add new data to the database (if the "keep old result status" is checked and if I want to build this function) */
    public void addRecentTasks(List<ResultItem> items) {
        for (ResultItem item : items) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(RecentTaskTable.Columns.RESULT_ID, item.getResultID());
            contentValues.put(RecentTaskTable.Columns.RESULT_NAME, item.getResultName());
            contentValues.put(RecentTaskTable.Columns.APP_NAME, item.getAppName());
            contentValues.put(RecentTaskTable.Columns.DEVICE_ID, item.getDeviceID());
            contentValues.put(RecentTaskTable.Columns.DEVICE_NAME, item.getDeviceName());
            contentValues.put(RecentTaskTable.Columns.SENT_TIME, item.getSentTime());
            contentValues.put(RecentTaskTable.Columns.REPORT_DEADLINE, item.getReportDeadline());
            contentValues.put(RecentTaskTable.Columns.RECEIVED_TIME, item.getReceivedTime());
            contentValues.put(RecentTaskTable.Columns.CPU_TIME, item.getCpuTime());
            contentValues.put(RecentTaskTable.Columns.ELAPSED_TIME, item.getElapsedTime());
            contentValues.put(RecentTaskTable.Columns.RESULT_STATUS_OUTCOME, item.getResultStatusOutcome());
            contentValues.put(RecentTaskTable.Columns.RESULT_SERVER_STATE, item.getResultServerState());
            contentValues.put(RecentTaskTable.Columns.RESULT_VALIDATE_STATE, item.getResultValidateState());
            contentValues.put(RecentTaskTable.Columns.CLAIMED_CREDIT, item.getClaimedCredit());
            contentValues.put(RecentTaskTable.Columns.GRANTED_CREDIT, item.getGrantedCredit());

            mDatabase.insert(RecentTaskTable.NAME, null, contentValues);
        }
    }
}
