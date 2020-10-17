package com.example.wcg_viewer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemberStatistic {
    private List<Badge> mBadgeList;
    private MemberStats mMemberStats;
    private List<Project> mProjects;

    public MemberStatistic() {
        mMemberStats = new MemberStats();
    }

    public MemberStats getMemberStats() {
        return mMemberStats;
    }

    public void setMemberStats(MemberStats memberStats) {
        mMemberStats = memberStats;
    }

    public List<Badge> getBadgeList() {
        return mBadgeList;
    }

    public void setBadgeList(List<Badge> badgeList) {
        mBadgeList = badgeList;
    }

    public List<Project> getProjects() {
        return mProjects;
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
    }


    ///////////////////////

    public static class MemberStats {
        private long mTotalRunTimeInSecond;
        private long[] mTotalRunTime;
        private int mTotalRunTimeRank;
        private int mGeneratedPoints;
        private int mGeneratedPointsRank;
        private int mReturnedResults;
        private int mReturnedResultRank;
        private Date mLastReturnedResult;

        public MemberStats() {
            mTotalRunTime = new long[]{0, 0, 0, 0, 0};

        }

        public void setTotalRunTime(String totalRunTimeInSecond) {

            /* is this way of parsing too brutal and stupid? Am i supposed to do it in a smarter way? */
            long second = Long.parseLong(totalRunTimeInSecond);
            mTotalRunTimeInSecond = second;
            long[] runTime = new long[5];
            //get second
            runTime[4] = (second % 60);
            //get minute
            long minute = second / 60;
            runTime[3] = (minute % 60);
            //get hour
            long hour = minute / 60;
            runTime[2] = (hour % 24);
            //get day, assuming 365 days a year
            long day = hour / 24;
            runTime[1] = (day % 365);
            //get year
            runTime[0] = (day / 365);

            mTotalRunTime = runTime;
        }

        public long[] getTotalRunTime() {
            return mTotalRunTime;
        }

        public long getTotalRunTimeInSecond() {
            return mTotalRunTimeInSecond;
        }

        public int getTotalRunTimeRank() {
            return mTotalRunTimeRank;
        }

        public void setTotalRunTimeRank(int totalRunTimeRank) {
            mTotalRunTimeRank = totalRunTimeRank;
        }

        public int getGeneratedPoints() {
            return mGeneratedPoints;
        }

        public void setGeneratedPoints(int generatedPoints) {
            mGeneratedPoints = generatedPoints;
        }

        public int getGeneratedPointsRank() {
            return mGeneratedPointsRank;
        }

        public void setGeneratedPointsRank(int generatedPointsRank) {
            mGeneratedPointsRank = generatedPointsRank;
        }

        public int getReturnedResults() {
            return mReturnedResults;
        }

        public void setReturnedResults(int returnedResults) {
            mReturnedResults = returnedResults;
        }

        public int getReturnedResultRank() {
            return mReturnedResultRank;
        }

        public void setReturnedResultRank(int returnedResultRank) {
            mReturnedResultRank = returnedResultRank;
        }

        public Date getLastReturnedResult() {
            return mLastReturnedResult;
        }

        public String getLastReturnedResultString() {
            DateFormat df = DateFormat.getDateTimeInstance();
            return df.format(mLastReturnedResult);
        }

        public void setLastReturnedResult(Date lastReturnedResult) {
            mLastReturnedResult = lastReturnedResult;
        }

    }

    public static class Badge {
        private String mProjectName;
        private String mUrl;
        private String mDescription;

        public String getProjectName() {
            return mProjectName;
        }

        public void setProjectName(String projectName) {
            mProjectName = projectName;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }
    }

    public static class Project {
        private String mProjectName;
        private String mProjectShortName;
        private long mRunTimeInSecond;
        private long mPoints;
        private long mResults;

        public long[] getRunTime() {
            long[] runTime = new long[5];
            /* is this way of parsing too brutal and stupid? Am i supposed to do it in a smarter way? */
            long second = mRunTimeInSecond;
            //get second
            runTime[4] = (second % 60);
            //get minute
            long minute = second / 60;
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
    }


}


