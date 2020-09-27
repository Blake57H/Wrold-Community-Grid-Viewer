package com.example.wcg_viewer;

import android.content.Context;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;

/* I plan to use a json file stored in application data folder to full fill this object */
/* Update: Seems that SharedPreference is the correct way to save settings. Redoing settings. */
public class SettingsDataLab {
    private static SettingsDataLab sLab;
    private static final String SETTINGS_FILENAME = "settings.json";
    private Context mApplicationContext;
    private SettingsItem mSettingsItem;

    private SettingsDataLab(Context context) {
        mApplicationContext = context.getApplicationContext();
        mSettingsItem = readSettingsFromJson();
    }

    public static SettingsDataLab getInstance(Context context) {
        if (sLab == null) sLab = new SettingsDataLab(context);
        return sLab;
    }


    public String getUserName() {
        return PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString(mApplicationContext.getString(R.string.preferences_key_username), null);
    }

    @Deprecated
    public void setUserName(String userName) {
        mSettingsItem.setUserName(userName);
        if (!saveSettingsToJson(mSettingsItem)) {
            Toast.makeText(mApplicationContext, "unable to save username", Toast.LENGTH_LONG).show();
        }
    }

    public String getVerificationCode() {
        return PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString(mApplicationContext.getString(R.string.preferences_key_verification_code), null);
    }

    @Deprecated
    public void setVerificationCode(String code) {
        mSettingsItem.setVerificationCode(code);
        if (!saveSettingsToJson(mSettingsItem)) {
            Toast.makeText(mApplicationContext, "unable to save verification code", Toast.LENGTH_LONG).show();
        }
    }

    public Date getLastUpdateDate() {
        return mSettingsItem.getLastUpdateDate();
    }

    public void setLastUpdateDate(Date date) {
        DateFormat df = DateFormat.getDateTimeInstance();
/*
        mSettingsItem.setLastUpdateDate(date);
        if (!saveSettingsToJson(mSettingsItem)) {
            Toast.makeText(mApplicationContext, "unable to save updated date", Toast.LENGTH_LONG).show();
        }
*/
        PreferenceManager
                .getDefaultSharedPreferences(mApplicationContext)
                .edit()
                .putString(mApplicationContext.getString(R.string.preferences_key_last_updated), df.format(date))
                .apply();
    }

    private boolean saveSettingsToJson(SettingsItem item) {
        boolean returnValue = true;
        FileOutputStream outputStream = null;
        Gson gson = new Gson();
        String settingJsonString = gson.toJson(item);

        try {
            outputStream = mApplicationContext.openFileOutput(SETTINGS_FILENAME, Context.MODE_PRIVATE);
            outputStream.write(settingJsonString.getBytes());
            //Toast.makeText(mApplicationContext, R.string.settings_updated, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            returnValue = false;
            Toast.makeText(mApplicationContext, "An error occurred:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnValue;
    }

    private SettingsItem readSettingsFromJson() {
        FileInputStream inputStream = null;
        Gson gson = new Gson();
        SettingsItem item = null;
        try {
            inputStream = mApplicationContext.openFileInput(SETTINGS_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String settingString;

            while ((settingString = bufferedReader.readLine()) != null) {
                builder.append(settingString).append("\n");
            }

            item = gson.fromJson(builder.toString(), SettingsItem.class);
        } catch (IOException e) {
            saveSettingsToJson(new SettingsItem());
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return item;
    }


    private static class SettingsItem {
        private String mUserName;
        private String mVerificationCode;
        private Date mLastUpdateDate;


        private SettingsItem() {
        }

        public String getUserName() {
            return mUserName;
        }

        public void setUserName(String userName) {
            mUserName = userName;
        }

        public String getVerificationCode() {
            return mVerificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            mVerificationCode = verificationCode;
        }

        public Date getLastUpdateDate() {
            return mLastUpdateDate;
        }

        public void setLastUpdateDate(Date lastUpdateDate) {
            mLastUpdateDate = lastUpdateDate;
        }
    }

}

