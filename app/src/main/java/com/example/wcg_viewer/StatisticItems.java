package com.example.wcg_viewer;

import java.text.DateFormat;
import java.util.Date;

public class StatisticItems {

    public static class MemberStatistic {
        private long mTotalRunTimeInSecond;
        private long[] mTotalRunTime;
        private int mTotalRunTimeRank;
        private int mGeneratedPoints;
        private int mGeneratedPointsRank;
        private int mReturnedResults;
        private int mReturnedResultRank;
        private Date mLastReturnedResult;

        public MemberStatistic() {
            mTotalRunTime = new long[]{0, 0, 0, 0, 0};
        }

        public void setTotalRunTime(String totalRunTimeInSecond) {

            /* is this way of parsing too brutal and stupid? Am i supposed to do it in a smarter way? */
            long second = Long.parseLong(totalRunTimeInSecond);
            mTotalRunTimeInSecond = second;
            //get second
            mTotalRunTime[4] = (second % 60);
            //get minute
            long minute = second / 60;
            mTotalRunTime[3] = (minute % 60);
            //get hour
            long hour = minute / 60;
            mTotalRunTime[2] = (hour % 24);
            //get day, assuming 365 days a year
            long day = hour / 24;
            mTotalRunTime[1] = (day % 365);
            //get year
            mTotalRunTime[0] = (day / 365);
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

        public String getLastReturnedResultString(){
            DateFormat df = DateFormat.getDateTimeInstance();
            return df.format(mLastReturnedResult);
        }

        public void setLastReturnedResult(Date lastReturnedResult) {
            mLastReturnedResult = lastReturnedResult;
        }


    }

}
