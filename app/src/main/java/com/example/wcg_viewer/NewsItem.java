package com.example.wcg_viewer;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsItem {
    private String mTitle;
    private String mDescription;
    private Date mPubDate;
    private String mLink;
    private String mGuid;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Date getPubDate() {
        return mPubDate;
    }

    public String getPubDateString(){
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.format(mPubDate);
    }

    public void setPubDate(Date pubDate) {
        mPubDate = pubDate;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }
}

class NewsRssFeed {
    private String mPubDate;
    private List<NewsItem> mNewsItems;

    public NewsRssFeed(){
        mPubDate = null;
        mNewsItems = new ArrayList<>();
    }

    public String getPubDate() {
        return mPubDate;
    }

    public void setPubDate(String pubDate) {
        mPubDate = pubDate;
    }

    public List<NewsItem> getNewsItems() {
        return mNewsItems;
    }

    public void setNewsItems(@NonNull List<NewsItem> newsItems) {
        mNewsItems = newsItems;
    }
}