package com.example.wcg_viewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

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

public class NewsFragment extends Fragment {
    public static NewsFragment newInstance() {

        Bundle args = new Bundle();

        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private List<NewsItem> mNewsItems;

    private static final String NEWS_FILENAME = "news.xml";
    private static final String TAG = "NewsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        mRecyclerView = v.findViewById(R.id.news_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadNews(null);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_news_fragment_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.update_news) {
            fetchLatestNews(getView());
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void loadNews(@Nullable List<NewsItem> items) {
        if (items == null) {
            Log.d(TAG, "Trying to load news from local xml file");
            items = loadNewsFromXMLFile();
        }

        if (mAdapter == null) {
            Log.d(TAG, "Creating News Adapter");
            mAdapter = new NewsAdapter(items);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Update News Data Set");
            mAdapter.setNewsItems(items);
            mAdapter.notifyDataSetChanged();
        }
        DebugNewsData(items);
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
            Toast.makeText(getActivity(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    public void fetchLatestNews(final View view) {
        Log.d(TAG, "fetchLatestNews: begin fetch");
        final StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://www.worldcommunitygrid.org/about_us/displayNews.do?rss=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "fetch news success");
                        NewsRssFeed feed = parseXMLtoFeed(response);

                        if (feed != null) {
                            try {
                                String oldDateString = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.preferences_key_news_pubdate), null);
                                if (oldDateString != null && !oldDateString.isEmpty()) {
                                    Date newDate = XMLStringPubDateConverter(feed.getPubDate());
                                    Date oldDate = XMLStringPubDateConverter(oldDateString);
                                    if (!newDate.after(oldDate)) {
                                        Log.d(TAG, "onResponse: newDate is not after oldDate");
                                        if (view != null) {
                                            Snackbar.make(view, getString(R.string.notify_update_news_already_latest), Snackbar.LENGTH_SHORT).show();
                                        }
                                        return;
                                    }
                                } else {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(getString(R.string.preferences_key_news_pubdate), feed.getPubDate()).apply();
                                    Log.d(TAG, "onResponse: newDate is after oldDate");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (view != null)
                                Snackbar.make(view, getString(R.string.notify_update_complete), Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (view != null)
                                Snackbar.make(view, getString(R.string.notify_update_news_error), Snackbar.LENGTH_SHORT).show();
                        }
                        saveNewsToXMLFile(response);
                        loadNews(feed.getNewsItems());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "fetch news error" + error.getMessage());
                        Toast.makeText(getActivity(), getString(R.string.notify_volley_response_error, error.getLocalizedMessage()), Toast.LENGTH_LONG).show();
                    }
                });
        SingletonVolley.get(getActivity()).addRequest(request);
    }

    private void saveNewsToXMLFile(String news) {
        Log.d(TAG, "saveNewsToXMLFile: saving news");
        FileOutputStream outputStream = null;
        try {
            File newsXML = new File(getActivity().getCacheDir(), NEWS_FILENAME);
            outputStream = new FileOutputStream(newsXML);
            outputStream.write(news.getBytes());
            //Toast.makeText(mApplicationContext, R.string.settings_updated, Toast.LENGTH_SHORT).show();
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
        File newsXML = new File(getActivity().getCacheDir(), NEWS_FILENAME);


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

    private void DebugNewsData(List<NewsItem> items) {
        boolean switcher = false;
        if (switcher) {
            if (items == null) {
                Log.d(TAG, "DebugNewsData: item is null");
                return;
            }
            for (NewsItem item : items) {
                Log.d(TAG, "DebugNewsData: Title=" + item.getTitle());
            }
        }
    }

    private class NewsHolder extends RecyclerView.ViewHolder {
        private NewsItem mNewsItem;
        private TextView mTitleTextView, mDateTextView, mDescriptionTextView;
        private Button mLinkButton;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.item_news_title);
            mDateTextView = itemView.findViewById(R.id.item_news_pub_date);
            mDescriptionTextView = itemView.findViewById(R.id.item_news_description);
            mLinkButton = itemView.findViewById(R.id.item_news_open_link_button);
        }

        public void bind(final NewsItem item) throws ParseException {
            Log.d(TAG, "Binding News " + item.getTitle());

            mNewsItem = item;
            mTitleTextView.setText(mNewsItem.getTitle());
            mDateTextView.setText(mNewsItem.getPubDateString());
            mDescriptionTextView.setText(mNewsItem.getDescription());
            mLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Open rss link: " + mNewsItem.getLink());
                    Intent openWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(mNewsItem.getLink()));
                    startActivity(openWebPage);
                }
            });
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter {
        private List<NewsItem> mNewsItems;

        public NewsAdapter(List<NewsItem> items) {
            mNewsItems = items;
        }

        public void setNewsItems(List<NewsItem> newsItems) {
            mNewsItems = newsItems;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NewsHolder(layoutInflater.inflate(R.layout.listitem_news, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            NewsItem item = mNewsItems.get(position);
            Log.d(TAG, "onBindViewHolder: " + position + ", item=" + item.getGuid());
            try {
                ((NewsHolder) holder).bind(item);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setHolderExpandProperties((NewsHolder) holder, false);
            ((NewsHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isExpanded = ((NewsHolder) holder).mLinkButton.getVisibility() == View.VISIBLE;
                    setHolderExpandProperties(((NewsHolder) holder), !isExpanded);
                }
            });
        }

        private void setHolderExpandProperties(@NonNull NewsHolder holder, boolean shouldExpand) {
            if (!shouldExpand) {
                // shouldn't expand = should collapse
                holder.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                holder.mTitleTextView.setMaxLines(1);
                holder.mDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
                holder.mDescriptionTextView.setMaxLines(2);
                holder.mLinkButton.setVisibility(View.GONE);
            } else {
                // should expand
                holder.mTitleTextView.setEllipsize(null);
                holder.mTitleTextView.setMaxLines(Integer.MAX_VALUE);
                holder.mDescriptionTextView.setEllipsize(null);
                holder.mDescriptionTextView.setMaxLines(Integer.MAX_VALUE);
                holder.mLinkButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            //Log.d(TAG, "RecentTaskAdapter.getItemCount = " + (mResultItems == null ? "null" : mResultItems.size()));
            if (mNewsItems == null) return 0;
            return mNewsItems.size();
        }
    }

}
