package com.example.wcg_viewer;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Locale;

public class ResultItem {
    @SerializedName("ResultId")
    private long mResultID;
    @SerializedName("Name")
    private String mResultName;
    @SerializedName("AppName")
    private String mAppName;
    @SerializedName("DeviceId")
    private long mDeviceID;
    @SerializedName("DeviceName")
    private String mDeviceName;
    @SerializedName("SentTime")
    private String mSentTime;
    @SerializedName("ReportDeadline")
    private String mReportDeadline;
    @SerializedName("ReceivedTime")
    private String mReceivedTime;
    @SerializedName("CpuTime")
    private double mCpuTime;
    @SerializedName("ElapsedTime")
    private double mElapsedTime;
    @SerializedName("Outcome")
    private int mResultStatusOutcome;
    @SerializedName("ServerState")
    private int mResultServerState;
    @SerializedName("ValidateState")
    private int mResultValidateState;
    @SerializedName("ClaimedCredit")
    private double mClaimedCredit;
    @SerializedName("GrantedCredit")
    private double mGrantedCredit;


    public String getAppName() {
        return mAppName;
    }

    public long getResultID() {
        return mResultID;
    }

    public String getResultName() {
        return mResultName;
    }

    public long getDeviceID() {
        return mDeviceID;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public String getSentTime() {
        return mSentTime;
    }

    public String getReportDeadline() {
        return mReportDeadline;
    }

    public String getReceivedTime() {
        return mReceivedTime;
    }

    public String getCpuTime() {
        return String.format(Locale.getDefault(), "%.2f", mCpuTime);
    }

    public String getElapsedTime() {
        return String.format(Locale.getDefault(), "%.2f", mElapsedTime);
    }

    public int getResultStatusOutcome() {
        return mResultStatusOutcome;
    }

    public String getClaimedCredit() {
        return String.format(Locale.getDefault(), "%.2f", mClaimedCredit);
    }

    public String getGrantedCredit() {
        return String.format(Locale.getDefault(), "%.2f", mGrantedCredit);
    }

    public int getResultServerState() {
        return mResultServerState;
    }

    public int getResultValidateState() {
        return mResultValidateState;
    }

    public void setResultID(long resultID) {
        mResultID = resultID;
    }

    public void setResultName(String resultName) {
        mResultName = resultName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public void setDeviceID(long deviceID) {
        mDeviceID = deviceID;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public void setSentTime(String sentTime) {
        mSentTime = sentTime;
    }

    public void setReportDeadline(String reportDeadline) {
        mReportDeadline = reportDeadline;
    }

    public void setReceivedTime(String receivedTime) {
        mReceivedTime = receivedTime;
    }

    public void setCpuTime(double cpuTime) {
        mCpuTime = cpuTime;
    }

    public void setElapsedTime(double elapsedTime) {
        mElapsedTime = elapsedTime;
    }

    public void setResultStatusOutcome(int resultStatusOutcome) {
        mResultStatusOutcome = resultStatusOutcome;
    }

    public void setResultServerState(int resultServerState) {
        mResultServerState = resultServerState;
    }

    public void setResultValidateState(int resultValidateState) {
        mResultValidateState = resultValidateState;
    }

    public void setClaimedCredit(double claimedCredit) {
        mClaimedCredit = claimedCredit;
    }

    public void setGrantedCredit(double grantedCredit) {
        mGrantedCredit = grantedCredit;
    }

    public ResultItem() {}
}

class ResultData {
    @SerializedName("ResultsAvailable")
    private String mResultsAvailable;
    @SerializedName("ResultsReturned")
    private String mResultsReturned;
    @SerializedName("Offset")
    private String mOffset;
    @SerializedName("Results")
    List<ResultItem> mResults;

    public int getResultsAvailable() {
        return Integer.parseInt(mResultsAvailable);
    }

    public int getResultsReturned() {
        return Integer.parseInt(mResultsReturned);
    }

    public int getOffset() {
        return Integer.parseInt(mOffset);
    }
}

class ResultDataRaw {
    @SerializedName("ResultsStatus")
    ResultData mResultData;
}

class ErrorResultDataRaw{
    @SerializedName("errors")
    List<ErrorItem> mErrorItems;

    static class ErrorItem {
        @SerializedName("code")
        int mCode;
        @SerializedName("message")
        String mMessage;
    }
}