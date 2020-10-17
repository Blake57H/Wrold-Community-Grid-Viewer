package com.example.wcg_viewer;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.wcg_viewer.MemberStatsByProjectsDatabase.MemberStatsByProjectDbSchema.*;
import com.example.wcg_viewer.RecentTaskDatabase.RecentTaskDbSchema.*;


public class DbCursorWrapper extends CursorWrapper {

    public DbCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ResultItem getResultItem() {
        ResultItem item = new ResultItem();

        item.setResultID(getLong(getColumnIndex(RecentTaskTable.Columns.RESULT_ID)));
        item.setResultName(getString(getColumnIndex(RecentTaskTable.Columns.RESULT_NAME)));
        item.setAppName(getString(getColumnIndex(RecentTaskTable.Columns.APP_NAME)));
        item.setDeviceID(getLong(getColumnIndex(RecentTaskTable.Columns.DEVICE_ID)));
        item.setDeviceName(getString(getColumnIndex(RecentTaskTable.Columns.DEVICE_NAME)));
        item.setSentTime(getString(getColumnIndex(RecentTaskTable.Columns.SENT_TIME)));
        item.setReportDeadline(getString(getColumnIndex(RecentTaskTable.Columns.REPORT_DEADLINE)));
        item.setReceivedTime(getString(getColumnIndex(RecentTaskTable.Columns.RECEIVED_TIME)));
        item.setCpuTime(getDouble(getColumnIndex(RecentTaskTable.Columns.CPU_TIME)));
        item.setElapsedTime(getDouble(getColumnIndex(RecentTaskTable.Columns.ELAPSED_TIME)));
        item.setResultStatusOutcome(getInt(getColumnIndex(RecentTaskTable.Columns.RESULT_STATUS_OUTCOME)));
        item.setResultServerState(getInt(getColumnIndex(RecentTaskTable.Columns.RESULT_SERVER_STATE)));
        item.setResultValidateState(getInt(getColumnIndex(RecentTaskTable.Columns.RESULT_VALIDATE_STATE)));
        item.setClaimedCredit(getDouble(getColumnIndex(RecentTaskTable.Columns.CLAIMED_CREDIT)));
        item.setGrantedCredit(getDouble(getColumnIndex(RecentTaskTable.Columns.GRANTED_CREDIT)));

        return item;
    }

    public String getBadgeUrl() {
        return getString(getColumnIndex(MemberStatsByProjectTable.Columns.BADGE_URL));
    }

    public ProjectItem getProjectItem() {
        ProjectItem item = new ProjectItem();

        item.setProjectShortName(getString(getColumnIndex(MemberStatsByProjectTable.Columns.PROJECT_SHORT_NAME)));
        item.setProjectName(getString(getColumnIndex(MemberStatsByProjectTable.Columns.PROJECT_NAME)));
        item.setRunTimeInSecond(getLong(getColumnIndex(MemberStatsByProjectTable.Columns.RUNTIME)));
        item.setPoints(getLong(getColumnIndex(MemberStatsByProjectTable.Columns.POINTS)));
        item.setResults(getLong(getColumnIndex(MemberStatsByProjectTable.Columns.RESULTS)));
        item.setBadgeUrl(getString(getColumnIndex(MemberStatsByProjectTable.Columns.BADGE_URL)));
        item.setBadgeDescription(getString(getColumnIndex(MemberStatsByProjectTable.Columns.BADGE_DESCRIPTION)));

        return item;
    }
}
