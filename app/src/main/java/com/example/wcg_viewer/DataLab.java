package com.example.wcg_viewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataLab {
    private static final String TAG = "DataLab";
    private static final String NEWS_FILENAME = "news.xml";

    private List<NewsItem> mNewsItems;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private NewsFetched mNewsFetched;

    public interface NewsFetched {
        void onNewsFetched(@NonNull NewsRssFeed feed);
        void postProgressNotification(int stringID);
        void postErrorNotification(String string);
        void postErrorNotification(int stringID);
        void postCompleteNotification(int stringID);
    }

    public void setOnNewsFetched(NewsFetched newsFetched) {
        mNewsFetched = newsFetched;
    }

    public DataLab(Context context) {
        mContext = context;
        mNewsItems = loadNewsFromXMLFile();
    }

    public List<NewsItem> getNewsItems() {
        return mNewsItems;
    }

    private NewsRssFeed parseXMLtoFeed(InputStream stream) {
        Log.d(TAG, "XMLtoFeedParser: parsing from stream");
        mNewsItems = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(stream, null);
            return actualXMLParser(parser);

        } catch (XmlPullParserException | IOException | ParseException e) {
            Log.e(TAG, "XMLtoFeedParser: " + e.getMessage());
            mNewsFetched.postErrorNotification("ERROR: " + e.getMessage());
            return null;
        }
    }

    private NewsRssFeed parseXMLtoFeed(String xmlString) {
        Log.d(TAG, "XMLtoFeedParser: parsing from string: " + xmlString);
        mNewsItems = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        Reader reader = new StringReader(xmlString);
        try {
            parser.setInput(reader);
            return actualXMLParser(parser);

        } catch (XmlPullParserException | IOException | ParseException e) {
            Log.e(TAG, "XMLtoFeedParser: " + e.getMessage());
            mNewsFetched.postErrorNotification("ERROR: " + e.getMessage());
            return null;
        }
    }

    private NewsRssFeed actualXMLParser(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        Log.d(TAG, "actualXMLParser: begin parse");
        parser.nextTag();
        String name;
        NewsRssFeed feed = new NewsRssFeed();
        List<NewsItem> items = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                Log.d(TAG, "actualXMLParser: in rss parserGetEventType != START_TAG: " + parser.getEventType());
                continue;
            }
            parser.require(XmlPullParser.START_TAG, null, "channel");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    Log.d(TAG, "actualXMLParser: in channel parserGetEventType != START_TAG: " + parser.getEventType());
                    continue;
                }
                name = parser.getName();
                if (name.equals("item")) {
                    items.add(parseXMLReadItem(parser));
                } else if (name.equals("pubDate")) {
                    feed.setPubDate(getXMLText(parser));
                    Log.d(TAG, "actualXMLParser: got pub date");
                } else {
                    Log.d(TAG, "actualXMLParser: skipping xml part");
                    skipXMLPart(parser);
                }

            }
        }
        Log.d(TAG, "actualXMLParser: parse ended: " + parser.getName());
        feed.setNewsItems(items);
        return feed;
    }

    private NewsItem parseXMLReadItem(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        Log.d(TAG, "actualXMLParser: read an item");
        String name;
        NewsItem newsItem = new NewsItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            name = parser.getName();
            switch (name) {
                case "title":
                    newsItem.setTitle(getXMLText(parser));
                    Log.d(TAG, "actualXMLParser: read item title: " + newsItem.getTitle());
                    break;
                case "link":
                    newsItem.setLink(getXMLText(parser));
                    Log.d(TAG, "actualXMLParser: read item link: " + newsItem.getLink());
                    break;
                case "description":
                    newsItem.setDescription(getXMLText(parser));
                    Log.d(TAG, "actualXMLParser: read item desc: " + newsItem.getDescription());
                    break;
                case "pubDate":
                    newsItem.setPubDate(XMLStringPubDateConverter(getXMLText(parser)));
                    break;
                case "guid ":
                    newsItem.setGuid(getXMLText(parser));
                    break;
                default:
                    skipXMLPart(parser);
                    break;
            }
        }
        Log.d(TAG, "parseXMLReadItem: an item is returning");
        return newsItem;
    }

    private String getXMLText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = null;

        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.next();
        }
        return text;
    }

    private void skipXMLPart(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public void fetchLatestNews() {
        Log.d(TAG, "fetchLatestNews: begin fetch");
        final StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://www.worldcommunitygrid.org/about_us/displayNews.do?rss=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "fetch news success");
                        NewsRssFeed feed = parseXMLtoFeed(response);

                        //decide if saving or reloading news is necessary (not saving or refreshing if news is already the latest)
                        if (feed != null) {
                            try {
                                String oldDateString = PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getString(R.string.preferences_key_news_pubdate), null);
                                if (oldDateString != null && !oldDateString.isEmpty()) {
                                    Date newDate = XMLStringPubDateConverter(feed.getPubDate());
                                    Date oldDate = XMLStringPubDateConverter(oldDateString);
                                    if (!newDate.after(oldDate)) {
                                        Log.d(TAG, "onResponse: newDate is not after oldDate");
                                        mNewsFetched.postProgressNotification(R.string.notify_update_news_already_latest);
                                        return;
                                    }
                                } else {
                                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(mContext.getString(R.string.preferences_key_news_pubdate), feed.getPubDate()).apply();
                                    Log.d(TAG, "onResponse: newDate is after oldDate");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            saveNewsToXMLFile(response);
                            mNewsFetched.onNewsFetched(feed);
                            mNewsFetched.postCompleteNotification(R.string.notify_update_complete);
                        } else {
                            mNewsFetched.postErrorNotification(R.string.notify_update_news_error);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "fetch news error" + error.getMessage());
                        mNewsFetched.postErrorNotification(mContext.getString(R.string.notify_volley_response_error, error.getLocalizedMessage()));
                    }
                });
        SingletonVolley.get(mContext).addRequest(request);
    }

    private void saveNewsToXMLFile(String news) {
        Log.d(TAG, "saveNewsToXMLFile: saving news");
        FileOutputStream outputStream = null;
        try {
            //saving it to cache dir, so it can be cleaned with cache
            File newsXML = new File(mContext.getCacheDir(), NEWS_FILENAME);
            outputStream = new FileOutputStream(newsXML);
            outputStream.write(news.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "saveNewsToXMLFile: error " + e.getMessage());
            e.printStackTrace();
            return;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "saveNewsToXMLFile: save completed");
    }

    private List<NewsItem> loadNewsFromXMLFile() {
        Log.d(TAG, "loadNewsFromXMLFile: load begin");
        FileInputStream inputStream = null;
        File newsXML = new File(mContext.getCacheDir(), NEWS_FILENAME);


        if (!newsXML.exists()) {
            Log.e(TAG, "loadNewsFromXMLFile: file not exist");
            return null;
        }

        try {
            inputStream = new FileInputStream(newsXML);
            NewsRssFeed feed = parseXMLtoFeed(inputStream);
            return feed == null ? null : feed.getNewsItems();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Date XMLStringPubDateConverter(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());
        return sdf.parse(date);
    }

}
