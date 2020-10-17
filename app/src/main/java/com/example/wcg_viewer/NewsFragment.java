package com.example.wcg_viewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.List;

public class NewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private SourceDataLab mSourceDataLab;

    private static final String TAG = "NewsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSourceDataLab = new SourceDataLab(getActivity());
        mSourceDataLab.setOnNewsFetched(new SourceDataLab.NewsFetched() {
            @Override
            public void onNewsFetched(@NonNull NewsRssFeed feed) {
                loadNews(feed.getNewsItems());
            }

            @Override
            public void postProgressNotification(int stringID) {
                Snackbar.make(getView(), stringID, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void postErrorNotification(String string) {
                Snackbar.make(getView(), string, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void postErrorNotification(int stringID) {
                postErrorNotification(getString(stringID));
            }

            @Override
            public void postCompleteNotification(int stringID) {
                Snackbar.make(getView(), stringID, Snackbar.LENGTH_SHORT).show();
            }
        });
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
//            fetchLatestNews(getView());
            mSourceDataLab.fetchLatestNews();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void loadNews(@Nullable List<NewsItem> items) {
        if (items == null) {
            items = mSourceDataLab.getNewsItems();
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
