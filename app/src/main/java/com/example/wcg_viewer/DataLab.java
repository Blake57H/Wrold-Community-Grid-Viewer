package com.example.wcg_viewer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.wcg_viewer.StatisticItems.*;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class DataLab {
    private static final String TAG = "DataLab";
    private final Context mContext;

    public DataLab(Context context) {
        mContext = context;
        mNewsItems = loadNewsFromXMLFile();
        mMemberStatistic = loadMemberStatisticFromXMLFile();
    }


    //================================
    /* methods that parts are shard */
    private boolean SaveFile(File file, byte[] data) {
        FileOutputStream outputStream = null;
        try {
            //file save location is decided by 'file'
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
        } catch (IOException e) {
            Log.e(TAG, "saveNewsToXMLFile: error " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private String getXMLText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = null;
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.next();
        }
        if (text == null)
            Log.e(TAG, "getXMLText: text is null, parser.getEventType()=" + parser.getEventType());
        else if (text.isEmpty())
            Log.e(TAG, "getXMLText: text is empty, parser.getEventType()=" + parser.getEventType());
        else
            Log.d(TAG, "getXMLText: text=" + text);
        return text;
    }

    private void saveStringToXMLFile(String content, String fileName) {
        Log.d(TAG, "saveStringToXMLFile: saving " + fileName);
        boolean saveStatus = SaveFile(new File(mContext.getCacheDir(), fileName), content.getBytes());
        if (saveStatus) Log.d(TAG, "saveStringToXMLFile: save completed");
        else Log.e(TAG, "saveStringToXMLFile: save file returned error");
    }

    public boolean getNetworkAvailability(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        Log.d(TAG, "getNetworkAvailability: info is null = " + (info == null ? "true" : "false"));
        if (info != null)
            Log.d(TAG, "getNetworkAvailability: info isConnected = " + (info.isConnected() ? "true" : "false"));
        if (info == null || !info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //================================
    /* the news rss feed part */
    private static final String NEWS_FILENAME = "news.xml";
    private List<NewsItem> mNewsItems;
    private NewsFetched mNewsFetched = new NewsFetched() {
        @Override
        public void onNewsFetched(@NonNull NewsRssFeed feed) {

        }

        @Override
        public void postProgressNotification(int stringID) {

        }

        @Override
        public void postErrorNotification(String string) {

        }

        @Override
        public void postErrorNotification(int stringID) {

        }

        @Override
        public void postCompleteNotification(int stringID) {

        }
    };

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


    public List<NewsItem> getNewsItems() {
        return mNewsItems;
    }

    private void setNewsItems(List<NewsItem> newsItems) {
        mNewsItems = newsItems;
    }

    public NewsItem getLatestNewsItem() {
        return mNewsItems == null || mNewsItems.size() == 0 ? null : mNewsItems.get(0);
    }

    //input the xml data, and return a NewsFeed. generate a XmlPullParser and pass it to the parse method
    private NewsRssFeed parseXMLtoFeed(InputStream xmlStream, String xmlString) {
        Log.d(TAG, "XMLtoFeedParser: parsing from stream");
        XmlPullParser parser = Xml.newPullParser();
        try {
            if (xmlStream != null)
                parser.setInput(xmlStream, null);
            else if (xmlString != null) {
                StringReader reader = new StringReader(xmlString);
                parser.setInput(reader);
            } else
                return null;

            return actualNewsXMLParser(parser);

        } catch (XmlPullParserException | IOException | ParseException e) {
            Log.e(TAG, "XMLtoFeedParser: " + e.getMessage());
            mNewsFetched.postErrorNotification("ERROR: " + e.getMessage());
            return null;
        }
    }

    //the parse method
    private NewsRssFeed actualNewsXMLParser(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
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
                    //I wanted to put the whole parse item method here. but it is tiring to read. then i wrap that part into a method hoping it won't be too tired to read
                    //the same idea applies to other methods below....
                    items.add(parseXMLReadNewsItem(parser));
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

    private NewsItem parseXMLReadNewsItem(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
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
                    Log.d(TAG, "actualXMLParser: read item desc: " + newsItem.getPubDateString());
                    break;
                case "guid ":
                    newsItem.setGuid(getXMLText(parser));
                    Log.d(TAG, "parseXMLReadNewsItem: read item guid: " + newsItem.getGuid());
                    break;
                default:
                    skipXMLPart(parser);
                    break;
            }
        }
        Log.d(TAG, "parseXMLReadItem: an item is returning");
        return newsItem;
    }


    private void skipXMLPart(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        // a copy-paste from google's document (the one teaching how to parse xml)
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
        if (getNetworkAvailability(mContext)) {
            mNewsFetched.postErrorNotification(mContext.getString(R.string.notify_network_unavailable));
            return;
        }
        final StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://www.worldcommunitygrid.org/about_us/displayNews.do?rss=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "fetch news success");
                        NewsRssFeed feed = parseXMLtoFeed(null, response);

                        if (feed != null) {
                            //decide if saving or reloading news is necessary (not saving or refreshing if news is already the latest)
                            try {
                                String oldDateString = PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getString(R.string.preferences_key_news_pubdate), null);
                                if (oldDateString != null && !oldDateString.isEmpty()) {
                                    Date newDate = XMLStringPubDateConverter(feed.getPubDate());
                                    Date oldDate = XMLStringPubDateConverter(oldDateString);
                                    if (!newDate.after(oldDate)) {
                                        Log.d(TAG, "onResponse: newDate is not after oldDate");
                                        mNewsFetched.postCompleteNotification(R.string.notify_update_news_already_latest);
                                        return;
                                    }
                                } else {
                                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(mContext.getString(R.string.preferences_key_news_pubdate), feed.getPubDate()).apply();
                                    Log.d(TAG, "onResponse: newDate is after oldDate");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            saveStringToXMLFile(response, NEWS_FILENAME);
                            setNewsItems(feed.getNewsItems());
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
            NewsRssFeed feed = parseXMLtoFeed(inputStream, null);
            return feed == null ? null : feed.getNewsItems();
        } catch (IOException e) {
            Log.e(TAG, "loadNewsFromXMLFile: load news from file error: " + e.getMessage());
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
        Log.d(TAG, "XMLStringPubDateConverter: parsing date: " + date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        return sdf.parse(date);
    }


    //================================
    /* Member Statistic here */
    private static final String BADGE_ICON_DIR = "/badges";
    private static final String STATISTIC_XML_FILE_NAME = "MemberStatsWithTeamHistory.xml";
    private MemberStatistic mMemberStatistic;
    private StatisticFetched mStatisticFetched = new StatisticFetched() {
        @Override
        public void onMemberStatisticFetched(MemberStatistic statistic) {

        }

        @Override
        public void postErrorMessage(String message) {

        }

        @Override
        public void postAlertDialogErrorMessage(String title, String message, String url) {

        }

        @Override
        public void postProgressMessage(String message) {

        }

        @Override
        public void postCompleteMessage(String message) {

        }
    };

    public void setStatisticFetched(StatisticFetched statisticFetched) {
        mStatisticFetched = statisticFetched;
    }

    public interface StatisticFetched {
        void onMemberStatisticFetched(MemberStatistic statistic);

        void postErrorMessage(String message);

        public void postAlertDialogErrorMessage(String title, String message, String url);
        void postProgressMessage(String message);

        void postCompleteMessage(String message);
    }


    public MemberStatistic getMemberStatistic() {
        return mMemberStatistic;
    }

    public void setMemberStatistic(MemberStatistic memberStatistic) {
        mMemberStatistic = memberStatistic;
    }

    public void fetchLatestStatistic(String userName, String verificationCode) {
        Log.d(TAG, "fetchLatestStatistic: begin fetch statistic");
        mStatisticFetched.postProgressMessage(mContext.getString(R.string.notify_update_in_progress));
        if (getNetworkAvailability(mContext)) {
            mNewsFetched.postErrorNotification(mContext.getString(R.string.notify_network_unavailable));
            return;
        }
        StringRequest request = new StringRequest(
                Request.Method.GET,
                new UrlBuilder().buildMemberStatsWithTeamHistoryString(userName, verificationCode),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: Recceived statistic XML: " + response);
                        File file = new File(mContext.getCacheDir(), STATISTIC_XML_FILE_NAME);
                        SaveFile(file, response.getBytes());
                        setMemberStatistic(ParseXMLtoMemberStatistic(null, response));
                        mStatisticFetched.onMemberStatisticFetched(mMemberStatistic);
                        mStatisticFetched.postCompleteMessage(
                                mContext.getString(
                                        R.string.notify_update_complete_with_text,
                                        mContext.getString(R.string.unclassified_text_member_statistic)));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: update member statistic error" + error.getMessage());
                        mStatisticFetched.postErrorMessage(
                                mContext.getString(
                                        R.string.notify_update_error_with_message,
                                        mContext.getString(R.string.unclassified_text_member_statistic),
                                        error.getLocalizedMessage()));
                    }
                }
        );
        SingletonVolley.get(mContext).addRequest(request);
    }

    private MemberStatistic loadMemberStatisticFromXMLFile() {
        File file = new File(mContext.getCacheDir(), STATISTIC_XML_FILE_NAME);
        if (!file.exists()) {
            Log.d(TAG, "loadMemberStatisticFromXMLFile: file not exist");
            return null;
        }
        try {
            InputStream stream = new FileInputStream(file);
            return ParseXMLtoMemberStatistic(stream, null);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "loadMemberStatisticFromXMLFile: error" + e.getMessage());
        }
        return null;
    }

    private MemberStatistic ParseXMLtoMemberStatistic(InputStream xmlStream, String xmlString) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            if (xmlString != null) {
                StringReader reader = new StringReader(xmlString);
                parser.setInput(reader);
            } else if (xmlStream != null) {
                parser.setInput(xmlStream, null);
            } else return null;
            return actualStatisticXMLParser(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "ParseXMLtoMemberStatistic: Error: " + e.getMessage());
            mStatisticFetched.postErrorMessage(
                    mContext.getString(
                            R.string.notify_update_error_with_message,
                            mContext.getString(R.string.unclassified_text_member_statistic),
                            e.getMessage()));
            return null;
        }
    }

    private MemberStatistic actualStatisticXMLParser(XmlPullParser parser) {
        Log.d(TAG, "actualXMLStatisticParser: begin parse");
        try {
            parser.nextTag();
            String name;
            MemberStatistic statistic = new MemberStatistic();

            if (parser.getName().equals("Error")) {
                name = getXMLText(parser);
                mStatisticFetched.postErrorMessage(mContext.getString(R.string.notify_update_error_with_message, mContext.getString(R.string.unclassified_text_member_statistic), name));
                Log.i(TAG, "Decoded an error message: " + name);
                return null;
            }

            parser.require(XmlPullParser.START_TAG, null, "MemberStatsWithTeamHistory");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    Log.d(TAG, "actualXMLStatisticParser: parser in MemberStatsWithTeamHistory did not got start tag: " + parser.getEventType());
                    continue;
                }

                name = parser.getName();
                if (name.equals("MemberStats")) {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            Log.d(TAG, "actualXMLStatisticParser: in MemberStats  did not got start tag: " + parser.getEventType());
                            continue;
                        }

                        parser.require(XmlPullParser.START_TAG, null, "MemberStat");
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                Log.d(TAG, "actualXMLStatisticParser: in MemberStat did not got start tag: " + parser.getEventType());
                                continue;
                            }

                            name = parser.getName();
                            Log.d(TAG, "actualStatisticXMLParser: name=" + name);
                            if (name.equals("LastResult")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                statistic.setLastReturnedResult(sdf.parse(getXMLText(parser)));
                            } else if (name.equals("StatisticsTotals")) {
                                parseStatisticsTotals(parser, statistic);
                            } else {
                                Log.d(TAG, "actualXMLStatisticParser: skipping xml part");
                                skipXMLPart(parser);
                            }
                        }


                    }
                } else {
                    skipXMLPart(parser);
                }
            }
            Log.d(TAG, "actualXMLStatisticParser: parse ended: " + parser.getName());
            return statistic;

        } catch (IOException | XmlPullParserException | ParseException e) {
            e.printStackTrace();
            mStatisticFetched.postErrorMessage(mContext.getString(R.string.notify_unknown_load_error_to_visit_website_with_message, mContext.getString(R.string.unclassified_text_member_statistic), e.getLocalizedMessage()));
            return null;
        }

    }

    private void parseStatisticsTotals(XmlPullParser parser, MemberStatistic statistic) throws IOException, XmlPullParserException {
        Log.d(TAG, "parseStatisticsTotals: reading StatisticsTotals");
        parser.require(XmlPullParser.START_TAG, null, "StatisticsTotals");
        String name, text;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            name = parser.getName();
            Log.d(TAG, "parseStatisticsTotals: name=" + name);
            switch (name) {
                case "RunTime":
                    text = getXMLText(parser);
                    statistic.setTotalRunTime(text);
                    Log.d(TAG, "parseStatisticsTotals: read total runtime: " + text);
                    break;
                case "RunTimeRank":
                    text = getXMLText(parser);
                    statistic.setTotalRunTimeRank(Integer.parseInt(text));
                    Log.d(TAG, "parseStatisticsTotals: RunTimeRank=" + text);
                    break;
                case "Points":
                    text = getXMLText(parser);
                    statistic.setGeneratedPoints(Integer.parseInt(text));
                    Log.d(TAG, "parseStatisticsTotals: Points=" + text);
                    break;
                case "PointsRank":
                    text = getXMLText(parser);
                    statistic.setGeneratedPointsRank(Integer.parseInt(text));
                    Log.d(TAG, "parseStatisticsTotals: PointsRank=" + text);
                    break;
                case "Results":
                    text = getXMLText(parser);
                    statistic.setReturnedResults(Integer.parseInt(text));
                    Log.d(TAG, "parseStatisticsTotals: Results=" + text);
                    break;
                case "ResultsRank":
                    text = getXMLText(parser);
                    statistic.setReturnedResultRank(Integer.parseInt(text));
                    Log.d(TAG, "parseStatisticsTotals: ResultRank=" + text);
                    break;
                default:
                    skipXMLPart(parser);
                    break;
            }
        }
        Log.d(TAG, "parseStatisticsTotals: read finished");
    }

}
