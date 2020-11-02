package com.example.wcg_viewer;

public class ProjectItem {
    private String mProjectName;
    private String mProjectShortName;
    private long mRunTimeInSecond;
    private long mPoints;
    private long mResults;
    private String mBadgeUrl;
    private String mBadgeDescription;

    public String getProjectName() {
        return mProjectName;
    }

    public void setProjectName(String projectName) {
        mProjectName = projectName;
    }

    public String getProjectShortName() {
        return mProjectShortName;
    }

    public void setProjectShortName(String projectShortName) {
        mProjectShortName = projectShortName;
    }

    public long getRunTimeInSecond() {
        return mRunTimeInSecond;
    }

    public void setRunTimeInSecond(long runTimeInSecond) {
        mRunTimeInSecond = runTimeInSecond;
    }

    public long[] getRunTime(){
        return RunTimeSecondToRunTimeConverter(mRunTimeInSecond);
    }

    public long getPoints() {
        return mPoints;
    }

    public void setPoints(long points) {
        mPoints = points;
    }

    public long getResults() {
        return mResults;
    }

    public void setResults(long results) {
        mResults = results;
    }

    public String getBadgeUrl() {
        return mBadgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        mBadgeUrl = badgeUrl;
    }

    public String getBadgeDescription() {
        return mBadgeDescription;
    }

    public void setBadgeDescription(String badgeDescription) {
        mBadgeDescription = badgeDescription;
    }

    public String getBadgeFileName() {
        String url = getBadgeUrl();
        if (url == null || url.isEmpty()) return null;
        String[] split = url.split("/");
        return split[split.length - 1];
    }


    private long[] RunTimeSecondToRunTimeConverter(long secondsInput) {
        long[] runTime = new long[5];
        /* is this way of parsing too brutal and stupid? Am i supposed to do it in a smarter way? */
        //get second
        runTime[4] = (secondsInput % 60);
        //get minute
        long minute = secondsInput / 60;
        runTime[3] = (minute % 60);
        //get hour
        long hour = minute / 60;
        runTime[2] = (hour % 24);
        //get day, assuming 365 days a year
        long day = hour / 24;
        runTime[1] = (day % 365);
        //get year
        runTime[0] = (day / 365);
        return runTime;
    }

}
