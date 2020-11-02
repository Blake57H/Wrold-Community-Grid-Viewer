package com.example.wcg_viewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    private SourceDataLab mSourceDataLab;

    private CardView mLatestNewsCardView;
    private TextView mLatestNewsTitleTextView, mLatestNewsTimeTextView, mLatestNewsDescriptionTextView;
    private Button mLatestNewsLinkButton;

    private CardView mStatisticCardView;
    private TextView mStatisticTotalRuntimeTitleTextView, mStatisticTotalRuntimeContentTextView,
            mStatisticGeneratedPointTitleTextView, mStatisticGeneratedPointContentTextView,
            mStatisticReturnedResultTitleTextView, mStatisticReturnedResultContentTextView,
            mStatisticLastReturnedResultContentTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSourceDataLab = new SourceDataLab(getActivity());
        mSourceDataLab.setOnNewsFetched(new SourceDataLab.NewsFetched() {
            @Override
            public void onNewsFetched(@NonNull NewsRssFeed feed) {
                setNewsCardViewContent(mSourceDataLab.getLatestNewsItem());
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
        });
        mSourceDataLab.setStatisticFetched(new SourceDataLab.StatisticFetched() {
            @Override
            public void onMemberStatisticFetched(MemberStatistic statistic) {
                setMemberStatisticCardViewContent(statistic);
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
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overview, container, false);

        mLatestNewsCardView = v.findViewById(R.id.overview_news_cardview);
        mLatestNewsTitleTextView = v.findViewById(R.id.overview_news_headline_textview);
        mLatestNewsTimeTextView = v.findViewById(R.id.overview_news_time_textview);
        mLatestNewsDescriptionTextView = v.findViewById(R.id.overview_news_description_textview);
        mLatestNewsLinkButton = v.findViewById(R.id.overview_news_open_link_button);
        setNewsCardViewContent(mSourceDataLab.getLatestNewsItem());

        FloatingActionButton floatingActionButton = v.findViewById(R.id.overview_refresh_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSourceDataLab.fetchLatestNews();

                String userName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.preferences_key_username), null);
                if (userName == null || userName.isEmpty()) {
                    Snackbar.make(view, R.string.notify_username_not_set, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.notify_username_not_set_action_title, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Navigation.findNavController(getView()).navigate(R.id.navigation_item_settings);
                                }
                            }).show();
                    return;
                }
                String VC = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.preferences_key_verification_code), null);
                if (VC == null || VC.isEmpty()) {
                    Snackbar.make(view, R.string.notify_verification_code_not_set, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.notify_verification_code_not_set_action_title, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Navigation.findNavController(getView()).navigate(R.id.navigation_item_settings);
                                }
                            }).show();
                    return;
                }
                mSourceDataLab.fetchLatestStatistic(userName, VC);
            }
        });

        mStatisticCardView = v.findViewById(R.id.member_statistic_cardview);
        mStatisticTotalRuntimeContentTextView = v.findViewById(R.id.member_stat_total_runtime_and_rank_content_textview);
        mStatisticGeneratedPointContentTextView = v.findViewById(R.id.overview_member_stat_points_generated_content_textview);
        mStatisticReturnedResultContentTextView = v.findViewById(R.id.overview_member_stat_resultreturned_content_textview);
        mStatisticLastReturnedResultContentTextView = v.findViewById(R.id.overview_member_stat_last_returned_result_content);
        setMemberStatisticCardViewContent(mSourceDataLab.getMemberStatistic());

        return v;
    }


    private void setNewsCardViewContent(final NewsItem item) {
        //show cardview only if there is a news to show
        if (item == null) {
            mLatestNewsCardView.setVisibility(View.GONE);
        } else {
            if (mLatestNewsCardView.getVisibility() != View.VISIBLE)
                mLatestNewsCardView.setVisibility(View.VISIBLE);
            mLatestNewsTitleTextView.setText(getString(R.string.overview_news_title, item.getTitle()));
            mLatestNewsTimeTextView.setText(item.getPubDateString());
            mLatestNewsDescriptionTextView.setText(item.getDescription());
            mLatestNewsLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                    startActivity(intent);
                }
            });
        }
    }

    private void setMemberStatisticCardViewContent(MemberStatistic statistic) {
        Log.d(TAG, "setMemberStatisticCardViewContent: setting statistic cardview");
        if (statistic == null) {
            Log.d(TAG, "setMemberStatisticCardViewContent: hide cardview");
            mStatisticCardView.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "setMemberStatisticCardViewContent: show cardview");
            if (mStatisticCardView.getVisibility() != View.VISIBLE)
                mStatisticCardView.setVisibility(View.VISIBLE);
            long[] runtime = statistic.getMemberStats().getTotalRunTime();
            mStatisticTotalRuntimeContentTextView.setText(getString(R.string.overview_member_stat_runtime_and_rank_content, runtime[0], runtime[1], runtime[2], runtime[3], runtime[4], statistic.getMemberStats().getTotalRunTimeRank() + ""));
            mStatisticGeneratedPointContentTextView.setText(getString(R.string.overview_member_stat_point_generated_result_returned_content, statistic.getMemberStats().getGeneratedPoints() + "", statistic.getMemberStats().getGeneratedPointsRank() + ""));
            mStatisticReturnedResultContentTextView.setText(getString(R.string.overview_member_stat_point_generated_result_returned_content, statistic.getMemberStats().getReturnedResults() + "", statistic.getMemberStats().getReturnedResultRank() + ""));
            Date dateNow = new Date(), dateLastReturnedResult = statistic.getMemberStats().getLastReturnedResult();
            String[] contentTexts = getResources().getStringArray(R.array.overview_member_stat_time_diff_texts);
            int contentTextsSelector = 0; // this int value is used to decide which contentTexts to use (contentTexts[contentTextSelector])

            int[] divideValue = new int[]{60, 60, 24, 365}; //60 seconds in minute, 60 minutes in an hour, 24 hours in a day, 365 days in a year
            long time = (dateNow.getTime() - dateLastReturnedResult.getTime()) / 1000; //time is second this moment
            while (contentTextsSelector < 4 && time / divideValue[contentTextsSelector] > 0) {
                /*What I want to do here (in this while loop) is:
                before entering the first loop, {divideValue[contentTextsSelector]} means 60 seconds in a minute, {time} represents time in seconds
                if time / divideValue[contentTextsSelector] < 0, then {time} would be less than 60
                which means time diff is less than a minute
                then the textview would show something like "5 seconds ago".

                in the first loop, {time/divideValue[contentTextsSelector]=time/60}, after which time would be representing time in minutes (the second part of the time would be missing but it doesn't matter here)
                and divideValue[contentTextsSelector] moves to its next value, which is 60 minutes in an hour
                if {time / divideValue[contentTextsSelector] > 0}, then {time} is greater than 60, meaning the loop will carry on to find out how many days there is
                if {time / divideValue[contentTextsSelector] < 0}, then {time} is less than 60, meaning Textview can show something like "45 minutes ago"

                and so on... if time is greater than 365 days, then it would show "2 years ago".... or "5 days ago" if time is less than 365 days

                *hope this comment section is useful
                * */
                time /= divideValue[contentTextsSelector];
                contentTextsSelector += 1;
            }
            /*
            if time happens to be {1}, then instead of showing things like "1 days ago" or "1 minutes ago", it would show "1 day ago"...
            that's how I wrote the string-array...
            */
            contentTextsSelector *= 2;
            if (time != 1) contentTextsSelector += 1;
            mStatisticLastReturnedResultContentTextView.setText(String.format(contentTexts[contentTextsSelector], time));
        }
    }
}
