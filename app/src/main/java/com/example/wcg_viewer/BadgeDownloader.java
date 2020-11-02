package com.example.wcg_viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BadgeDownloader {

    private static final String TAG = "BadgeDownloader";
    public static final String BADGE_ICON_DIR = "/badges";
    private final Context mContext;
    private BadgeDownloaded mBadgeDownloaded;

    public interface BadgeDownloaded {
        void onBadgeDownloaded(int index, Bitmap bitmap);
    }

    public void setBadgeDownloaded(BadgeDownloaded badgeDownloaded) {
        mBadgeDownloaded = badgeDownloaded;
    }

    public BadgeDownloader(Context context) {
        mContext = context;
    }

    public void downloadBadge(String url) {
        Log.d(TAG, "BadgeIconDownloader: downloading a badge: " + url);
        downloadBadge(null, url);
    }

    public void downloadBadge(final Integer index, String url) {
        String[] split = url.split("/");
        final String fileName = split[split.length - 1];
        String filePath = mContext.getCacheDir() + BADGE_ICON_DIR;
        final File badgeIconFile = new File(filePath, fileName);

        if (badgeIconFile.exists()) {
            Log.d(TAG, "downloadBadge: found downloaded badge icon in cache directory: " + fileName);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath + "/" + fileName);
            SingletonIconLruCache.get(mContext).putBitmapToCache(fileName, bitmap);
            return;
        }

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.d(TAG, "onResponse: downloaded a badge: " + fileName);
                SingletonIconLruCache.get(mContext).putBitmapToCache(fileName, response);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                response.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                FileOutputStream outputStream = null;
                try {
                    //file save location is decided by 'file'
                    outputStream = new FileOutputStream(badgeIconFile);
                    outputStream.write(stream.toByteArray());
                } catch (IOException e) {
                    Log.e(TAG, "SaveFile: error " + e.getMessage());
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

                if (index != null && mBadgeDownloaded != null) {
                    Log.d(TAG, "onResponse: calling BadgeDownloaded: index=" + index);
                    mBadgeDownloaded.onBadgeDownloaded(index, response);
                }
            }
        }, 0, 0, null, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: volley error: " + error.getMessage());
                    }
                });
        SingletonVolley.get(mContext).addRequest(request);

    }

}
