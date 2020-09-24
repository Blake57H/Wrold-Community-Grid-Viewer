package com.example.wcg_viewer.RecentTaskDatabase;

import com.google.gson.annotations.SerializedName;

public class RecentTaskDbSchema {
    public static final class RecentTaskTable{
        public static final String NAME = "recentTask";
        public static final class Columns{
            public static final String APP_NAME = "appName";
            public static final String RESULT_ID = "resultID";
            public static final String RESULT_NAME = "resultName";
            public static final String DEVICE_ID = "deviceID";
            public static final String DEVICE_NAME= "deviceName";
            public static final String SENT_TIME="sentTime";
            public static final String REPORT_DEADLINE="reportDeadline";
            public static final String RECEIVED_TIME="receivedTime";
            public static final String CPU_TIME="cpuTime";
            public static final String ELAPSED_TIME="elapsedTime";
            public static final String RESULT_STATUS_OUTCOME="resultStatusOutcome";
            public static final String RESULT_SERVER_STATE="resultServerState";
            public static final String RESULT_VALIDATE_STATE="resultValidateState";
            public static final String CLAIMED_CREDIT="claimedCredit";
            public static final String GRANTED_CREDIT="grantedCredit";
        }
    }
}
