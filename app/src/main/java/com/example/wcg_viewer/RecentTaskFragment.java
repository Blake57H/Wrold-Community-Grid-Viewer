package com.example.wcg_viewer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class RecentTaskFragment extends Fragment {
    public static RecentTaskFragment newInstance() {

        Bundle args = new Bundle();

        RecentTaskFragment fragment = new RecentTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = "RecentTaskFragment";

    RecyclerView mRecyclerView;
    RecentTaskAdapter mAdapter;
    private ResultDataRaw mResultDataRaw;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void lastUpdateDateChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);

        mRecyclerView = v.findViewById(R.id.tasks_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadResults();

        return v;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recent_task_fragment_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_recent_task:
                updateResults();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    //Load results from database
    private void loadResults() {
        if (mAdapter == null) {
            Log.d(TAG, "Creating Adapter");
            mAdapter = new RecentTaskAdapter(RecentTasksDataLab.getInstance(getActivity()).getResultItems());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Resetting ResultItems");
            mAdapter.setResultItems(RecentTasksDataLab.getInstance(getActivity()).getResultItems());
            mAdapter.notifyDataSetChanged();
        }

    }

    private void updateResults() {
        final String userName, VC;
        //userName = "Tech57";
        //VC = "38c3387252d40e9f21dd50325bf51d15";
        userName = SettingsDataLab.getInstance(getActivity()).getUserName();
        if (userName.isEmpty()) {
            Snackbar.make(getView(), R.string.notify_username_not_set, Snackbar.LENGTH_LONG)
                    .setAction(R.string.notify_username_not_set_action_title, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                        }
                    }).show();
            return;
        }

        VC = SettingsDataLab.getInstance(getActivity()).getVerificationCode();
        if (VC.isEmpty()) {
            Snackbar.make(getView(), R.string.notify_verification_code_not_set, Snackbar.LENGTH_LONG)
                    .setAction(R.string.notify_verification_code_not_set_action_title, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                        }
                    }).show();
            return;
        }

        if (getView() != null)
            Snackbar.make(getView(), getString(R.string.notify_update_in_progress), Snackbar.LENGTH_SHORT).show();
        else{
            Toast.makeText(getActivity(), R.string.notify_update_in_progress, Toast.LENGTH_SHORT).show();
        }
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        Log.d(TAG, "updateResults: info is null = " + (info == null ? "true" : "false"));
        if (info != null)
            Log.d(TAG, "updateResults: info isConnected = " + (info.isConnected() ? "true" : "false"));
        if (info != null && !info.isConnected()) {
            Snackbar.make(getView(), R.string.notify_network_unavailable, Snackbar.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET, new UrlBuilder().buildResultString(userName, VC), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Got Json Response:" + response.toString());
                                Gson gson = new Gson();

                                ErrorResultDataRaw errorResultDataRaw = gson.fromJson(response.toString(), ErrorResultDataRaw.class);
                                if (errorResultDataRaw.mErrorItems != null) {
                                    Log.d(TAG, "Received Error Code:" + errorResultDataRaw.mErrorItems.get(0).mCode + ", " + errorResultDataRaw.mErrorItems.get(0).mMessage);
                                    Toast.makeText(getActivity(), getString(R.string.notify_update_recent_task_received_error_code, errorResultDataRaw.mErrorItems.get(0).mMessage), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                mResultDataRaw = gson.fromJson(response.toString(), ResultDataRaw.class);
                                if (mResultDataRaw.mResultData == null) {
                                    Log.e(TAG, "mResultData = null");
                                    Toast.makeText(getActivity(), getString(R.string.notify_update_recent_task_received_unknown), Toast.LENGTH_SHORT).show();
                                    String url = new UrlBuilder().buildResultString(userName, VC);
                                    Intent openWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(openWebPage);
                                    return;
                                }

                                RecentTasksDataLab.getInstance(getActivity()).updateRecentTasks(mResultDataRaw.mResultData.mResults);
                                SettingsDataLab.getInstance(getActivity()).setLastUpdateDate(new Date());
                                loadResults();
                                mCallbacks.lastUpdateDateChanged();
                                if (getView() != null)
                                    Snackbar.make(getView(), R.string.notify_update_complete, Snackbar.LENGTH_SHORT).show();
                                else{
                                    Toast.makeText(getActivity(), R.string.notify_update_complete, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getString(R.string.notify_volley_response_error, error.getLocalizedMessage()), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "updateResults().onErrorResponse: " + error.getMessage());
                        String url = new UrlBuilder().buildResultString(userName, VC);
                        Intent openWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(openWebPage);
                    }
                });

        SingletonVolley.get(getActivity()).addRequest(request);
    }

    private class RecentTaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ResultItem mResultItem;
        TextView mTitleTextView, mDeviceNameTextView, mResultStatusTextView, mCpuElapsedTimeTextView,
                mSentTimeTextView, mSecondTimeTextView, mCreditTextView;

        public RecentTaskHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.item_task_title);
            mDeviceNameTextView = itemView.findViewById(R.id.item_device_name);
            mResultStatusTextView = itemView.findViewById(R.id.item_status);
            mCpuElapsedTimeTextView = itemView.findViewById(R.id.item_cpu_elapsed_time);
            mSentTimeTextView = itemView.findViewById(R.id.item_sent_time);
            mSecondTimeTextView = itemView.findViewById(R.id.item_second_time);
            mCreditTextView = itemView.findViewById(R.id.item_credit);
        }


        @Override
        public void onClick(View view) {

        }

        public void bind(final ResultItem item) throws ParseException {
            Log.d(TAG, "Binding Recent Task " + item.getResultName());

            mResultItem = item;
            mTitleTextView.setText(mResultItem.getResultName());
            mDeviceNameTextView.setText(mResultItem.getDeviceName());
            mCpuElapsedTimeTextView.setText(getString(R.string.item_cpu_elapsed_time_string, mResultItem.getCpuTime(), mResultItem.getElapsedTime()));
            mCreditTextView.setText(getString(R.string.item_credit_string, mResultItem.getClaimedCredit(), mResultItem.getGrantedCredit()));

            String[] statusArray = getResources().getStringArray(R.array.item_result_status);
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date;
            String secondTimeString = "N/A", status = "undefined status: " + mResultItem.getResultServerState() + "," + mResultItem.getResultStatusOutcome() + "," + mResultItem.getResultValidateState();
            if (mResultItem.getResultServerState() == 4) {
                date = simpleDateFormat.parse(mResultItem.getReportDeadline());
                secondTimeString = getString(R.string.item_time_due_string, dateFormat.format(date));
                status = statusArray[0];
            } else if (mResultItem.getResultServerState() == 5) {
                date = simpleDateFormat.parse(mResultItem.getReceivedTime());
                secondTimeString = getString(R.string.item_received_time_string, dateFormat.format(date));
                if (mResultItem.getResultValidateState() == 0)
                    status = statusArray[1];
                else if (mResultItem.getResultStatusOutcome() == 1 && mResultItem.getResultValidateState() == 1)
                    status = statusArray[2];
                else if (mResultItem.getResultStatusOutcome() == 7)
                    status = statusArray[5];
                else if (mResultItem.getResultStatusOutcome() == 4)
                    status = statusArray[3];
                else if (mResultItem.getResultStatusOutcome() == 6)
                    status = statusArray[4];
                else if (mResultItem.getResultStatusOutcome() == 3)
                    status = statusArray[6];
            }
            //Log.d(TAG, "bind: ResultServerState=" + mResultItem.getResultServerState());
            mSentTimeTextView.setText(getString(R.string.item_sent_time_string, dateFormat.format(simpleDateFormat.parse(mResultItem.getSentTime()))));
            mSecondTimeTextView.setText(secondTimeString);
            Log.d(TAG, "status=" + status);
            mResultStatusTextView.setText(status);
        }
    }

    private class RecentTaskAdapter extends RecyclerView.Adapter {
        private List<ResultItem> mResultItems;

        public RecentTaskAdapter(List<ResultItem> items) {
            mResultItems = items;
        }

        public void setResultItems(List<ResultItem> resultItems) {
            mResultItems = resultItems;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new RecentTaskHolder(layoutInflater.inflate(R.layout.listitem_tasks, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ResultItem item = mResultItems.get(position);
            Log.d(TAG, "onBindViewHolder: " + position + ", item=" + item.getResultID());
            try {
                ((RecentTaskHolder) holder).bind(item);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            //Log.d(TAG, "RecentTaskAdapter.getItemCount = " + (mResultItems == null ? "null" : mResultItems.size()));
            if (mResultItems == null) return 0;
            return mResultItems.size();
        }
    }
}
