package com.example.wcg_viewer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wcg_viewer.databinding.FragmentProjectsBinding;
import com.example.wcg_viewer.databinding.ListitemProjectsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ProjectFragment extends Fragment {

    private FragmentProjectsBinding mBinding;
    private BadgeDownloader mDownloader;
    private RecyclerView.Adapter<ProjectItemHolder> mAdapter;
    private static final String TAG = "ProjectFragment";

    public ProjectFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDownloader = new BadgeDownloader(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentProjectsBinding.inflate(inflater, container, false);
        mBinding.projectsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDownloader.setBadgeDownloaded(new BadgeDownloader.BadgeDownloaded() {
            @Override
            public void onBadgeDownloaded(int index, Bitmap bitmap) {
                mBinding.projectsRecyclerView.getAdapter().notifyItemChanged(index);
            }
        });
        loadProject();
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_project_fragment_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.update_project) {
            updateProject();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private void loadProject() {
        if (mAdapter == null) {
            mBinding.projectsRecyclerView.setAdapter(new ProjectItemAdapter(ProjectDataLab.getInstance(getActivity()).getProjects()));
        } else {
            mBinding.projectsRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void updateProject() {
        final String userName, VC;
        //userName = "Tech57";
        //VC = "38c3387252d40e9f21dd50325bf51d15";
        userName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.preferences_key_username), null);
        if (userName == null || userName.isEmpty()) {
            Snackbar.make(getView(), R.string.notify_username_not_set, Snackbar.LENGTH_LONG)
                    .setAction(R.string.notify_username_not_set_action_title, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Navigation.findNavController(getView()).navigate(R.id.navigation_item_settings);
                        }
                    }).show();
            return;
        }

        VC = SettingsDataLab.getInstance(getActivity()).getVerificationCode();
        if (VC == null || VC.isEmpty()) {
            Snackbar.make(getView(), R.string.notify_verification_code_not_set, Snackbar.LENGTH_LONG)
                    .setAction(R.string.notify_verification_code_not_set_action_title, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Navigation.findNavController(getView()).navigate(R.id.navigation_item_settings);
                        }
                    }).show();
            return;
        }

        SourceDataLab sourceDataLab = new SourceDataLab(getActivity());
        sourceDataLab.setStatisticFetched(new SourceDataLab.StatisticFetched() {
            @Override
            public void onMemberStatisticFetched(MemberStatistic statistic) {
                loadProject();
            }

            @Override
            public void postErrorMessage(String message) {
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void postAlertDialogErrorMessage(String title, String message, String url) {

            }

            @Override
            public void postProgressMessage(String message) {
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void postCompleteMessage(String message) {
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
        sourceDataLab.fetchLatestStatistic(userName, VC);
    }

    private class ProjectItemHolder extends RecyclerView.ViewHolder {
        private final ListitemProjectsBinding mItemBinding;

        public ProjectItemHolder(ListitemProjectsBinding binding) {
            super(binding.getRoot());
            mItemBinding = binding;
        }

        public void bind(ProjectItem item, int index) {
            Log.d(TAG, "bind: binding item of index " + index);
            StringBuilder stringBuilder;
            mItemBinding.projectsItemCardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expand_Collapse_projectLayout();
                }
            });

            mItemBinding.itemProjectTitleTextview.setText(item.getProjectName());

            float runtimeToShow;
            int[] divider = new int[]{365, 24, 60, 60};
            long[] runTime = item.getRunTime();
            int chooser = 0;
            while (chooser < runTime.length && runTime[chooser] == 0) {
                chooser += 1;
            }
            Log.d(TAG, "bind: chooser=" + chooser + ", runtime=" + runTime[chooser]);
            /* I should get the non-zero value of the largest runtime unit.
            if the runtime is 0Y 0D 15H 30M 0S, then I would expect chooser=2
            */
            runtimeToShow = runTime[chooser];
            if (chooser < 4) {
                runtimeToShow += runTime[chooser + 1] / (float) divider[chooser];
            }
            chooser *= 2;
            if (runTime[chooser] != 1) chooser += 1;
            mItemBinding.itemProjectRuntimeSummaryTextview.setText(String.format(getResources().getStringArray(R.array.projects_rumtime_texts)[chooser], runtimeToShow));
            mItemBinding.itemProjectRuntimeTextview.setText(getString(R.string.projects_runtime_detailed_text, runTime[0], runTime[1], runTime[2], runTime[3], runTime[4]));

            //set points(textview)
            long points = item.getPoints();
            String pointString = String.valueOf(item.getPoints());
            stringBuilder = new StringBuilder(pointString);
            for (int pointer = pointString.length() - 3; pointer > 0; pointer -= 3) {
                stringBuilder.insert(pointer, ','); // insert comma to the string (1000000 will turn into 1,000,000)
            }
            String[] pointTexts = getResources().getStringArray(R.array.projects_points_texts);
            String textToShow;
            mItemBinding.itemProjectPointsTextview.setText(String.format(pointTexts[0], stringBuilder.toString()));
            chooser = 0;
            // basically the same idea as the making of the time chooser above
            while (chooser < pointTexts.length - 1 && points / 1000 > 0) {
                points /= 1000;
                chooser += 1;
            }
            if (chooser != pointTexts.length - 1)
                textToShow = String.format(pointTexts[chooser], points);
            else textToShow = pointTexts[chooser];
            mItemBinding.itemProjectPointsSummaryTextview.setText(textToShow);

            //set results (textview)
            stringBuilder = new StringBuilder(String.valueOf(item.getResults()));
            for (int pointer = String.valueOf(item.getResults()).length() - 3; pointer > 0; pointer -= 3) {
                stringBuilder.insert(pointer, ','); // insert comma to the string (1000000 will turn into 1,000,000)
            }
            mItemBinding.itemProjectResultsTextview.setText(getString(R.string.projects_result_detailed_text, stringBuilder.toString()));

            //set badge icon
            String badge = item.getBadgeUrl();
            if (badge != null && !badge.isEmpty()) {
                textToShow = item.getBadgeDescription();
                mItemBinding.itemProjectBadgeDescriptionTextview.setText(textToShow);
                textToShow = textToShow.split(" ")[0];
                mItemBinding.itemProjectBadgeSummaryTextview.setText(textToShow);
                Bitmap icon = SingletonIconLruCache.get(getActivity()).getBitmapFromCache(item.getBadgeFileName());
                Log.d(TAG, "bind: icon==null: " + (icon == null));
                if (icon != null) {
                    mItemBinding.itemProjectBadgeImageview.setVisibility(View.VISIBLE);
                    mItemBinding.itemProjectBadgeImageview.setImageBitmap(icon);
                } else {
                    mItemBinding.itemProjectBadgeImageview.setVisibility(View.GONE);
                    mDownloader.downloadBadge(index, item.getBadgeUrl());
                }
            } else {
                mItemBinding.itemProjectBadgeDescriptionTextview.setVisibility(View.GONE);
                mItemBinding.itemProjectBadgeSummaryTextview.setVisibility(View.GONE);
            }

            mItemBinding.expandProjectItemImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expand_Collapse_projectLayout();
                }
            });
        }

        private void Expand_Collapse_projectLayout(){
            if (mItemBinding.itemProjectDetailedLayout.getVisibility() == View.GONE) {
                mItemBinding.expandProjectItemImageview.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                mItemBinding.itemProjectSummaryLayout.setVisibility(View.GONE);
                mItemBinding.itemProjectDetailedLayout.setVisibility(View.VISIBLE);
            } else {
                mItemBinding.expandProjectItemImageview.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                mItemBinding.itemProjectSummaryLayout.setVisibility(View.VISIBLE);
                mItemBinding.itemProjectDetailedLayout.setVisibility(View.GONE);
            }
        }

    }


    private class ProjectItemAdapter extends RecyclerView.Adapter<ProjectItemHolder> {
        private List<ProjectItem> mProjectItems;

        public ProjectItemAdapter(List<ProjectItem> items) {
            mProjectItems = items;
        }

        public void setProjectItems(List<ProjectItem> projectItems) {
            mProjectItems = projectItems;
        }

        @NonNull
        @Override
        public ProjectItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProjectItemHolder(ListitemProjectsBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ProjectItemHolder holder, int position) {
            holder.bind(mProjectItems.get(position), position);
        }

        @Override
        public int getItemCount() {
            //Log.d(TAG, "getItemCount: size=" + mProjectItems.size());
            return mProjectItems.size();
        }
    }
}
