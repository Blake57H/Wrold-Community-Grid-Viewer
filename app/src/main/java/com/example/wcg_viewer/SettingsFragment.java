package com.example.wcg_viewer;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    EditTextPreference mUsernameEditText, mVerificationCodeEditText;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mUsernameEditText = findPreference(getString(R.string.preferences_key_username));
        mUsernameEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                clearLastUpdateTime();
                String username = newValue == null ? null : String.valueOf(newValue);
                setUsernameEditTextSummary(username);
                return true;
            }
        });
        String username = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(mUsernameEditText.getKey(), null);
        setUsernameEditTextSummary(username);

        mVerificationCodeEditText = findPreference(getString(R.string.preferences_key_verification_code));
        mVerificationCodeEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                clearLastUpdateTime();
                String vc = newValue == null ? null : String.valueOf(newValue);
                setVerificationCodeEditTextSummary(vc);
                return true;
            }
        });
        String vc = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(mVerificationCodeEditText.getKey(), null);
        setVerificationCodeEditTextSummary(vc);
    }

    private void clearLastUpdateTime(){
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove(getString(R.string.preferences_key_last_updated)).apply();
    }

    private void setUsernameEditTextSummary(String username){
        if (mUsernameEditText != null) {
            if (username != null && !username.isEmpty())
                mUsernameEditText.setSummary(getString(R.string.settings_username_summary, username));
            else
                mUsernameEditText.setSummary(R.string.settings_username_empty_summary);
        }
    }

    private void setVerificationCodeEditTextSummary(String vc){
        if (mVerificationCodeEditText != null) {
            if (vc != null && !vc.isEmpty())
                mVerificationCodeEditText.setSummary(R.string.settings_verification_code_summary_set);
            else
                mVerificationCodeEditText.setSummary(R.string.settings_verification_code_summary_empty);
        }
    }

}
