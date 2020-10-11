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
                if(newValue == null || String.valueOf(newValue).isEmpty())
                    mUsernameEditText.setSummary(R.string.settings_username_empty_summary);
                else
                    mUsernameEditText.setSummary(getString(R.string.settings_username_summary, String.valueOf(newValue)));
                return true;
            }
        });
        String username = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(mUsernameEditText.getKey(), null);
        if (mUsernameEditText != null) {
            if (username != null && !username.isEmpty())
                mUsernameEditText.setSummary(getString(R.string.settings_username_summary, username));
            else
                mUsernameEditText.setSummary(R.string.settings_username_empty_summary);
        }

        mVerificationCodeEditText = findPreference(getString(R.string.preferences_key_verification_code));
        mVerificationCodeEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue == null || String.valueOf(newValue).isEmpty())
                    mVerificationCodeEditText.setSummary(R.string.settings_verification_code_summary_empty);
                else
                    mVerificationCodeEditText.setSummary(R.string.settings_verification_code_summary_set);
                return true;
            }
        });
        String vc = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(mVerificationCodeEditText.getKey(), null);
        if (mVerificationCodeEditText != null) {
            if (vc != null && !vc.isEmpty())
                mVerificationCodeEditText.setSummary(R.string.settings_verification_code_summary_set);
            else
                mVerificationCodeEditText.setSummary(R.string.settings_verification_code_summary_empty);
        }
    }

}
