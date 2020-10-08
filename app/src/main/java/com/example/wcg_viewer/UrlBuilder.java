package com.example.wcg_viewer;

import android.net.Uri;


public class UrlBuilder {
    public String buildResultString(String userName, String verificationCode) {
        return buildResultString(userName, verificationCode, 0);
    }

    public String buildResultString(String userName, String verificationCode, int offset) {
        return buildResultString(userName, verificationCode, offset, 250);
    }

    public String buildResultString(String userName, String verificationCode, int offset, int limit) {
        return Uri.parse("https://www.worldcommunitygrid.org/api/members/"+userName+"/results?")
                .buildUpon()
                .appendQueryParameter("code", verificationCode)
                .appendQueryParameter("offset", offset+"")
                .appendQueryParameter("limit", limit+"")
                .build().toString();
    }

}
