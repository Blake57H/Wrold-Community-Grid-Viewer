package com.example.wcg_viewer;

import android.net.Uri;


public class DataFetcher {
    public String buildResultString(String userName, String verificationCode) {
        return buildResultString(userName, verificationCode, 0);
    }

    public String buildResultString(String userName, String verificationCode, int offset) {
        return Uri.parse("https://www.worldcommunitygrid.org/api/members/"+userName+"/results?")
                .buildUpon()
                .appendQueryParameter("code", verificationCode)
                .appendQueryParameter("offset", offset+"")
                .build().toString();
    }
}
