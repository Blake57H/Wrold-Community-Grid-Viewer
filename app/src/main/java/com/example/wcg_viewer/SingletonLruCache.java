package com.example.wcg_viewer;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class SingletonLruCache {
    private static SingletonLruCache sSingletonLruCache;
    private final LruCache<String, Bitmap> mBitmapLruCache;
    private static final String TAG = "SingletonLruCache";


    private SingletonLruCache(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        int cacheSize = am.getMemoryClass() * 1024 * 1024 / 8;
        mBitmapLruCache = new LruCache<>(cacheSize);
    }

    public static SingletonLruCache get(Context context) {
        if (sSingletonLruCache == null) sSingletonLruCache = new SingletonLruCache(context);
        return sSingletonLruCache;
    }

    public void putBitmapToCache(String fileName, Bitmap bitmap) {
        Log.d(TAG, "putBitmapToCache: putting " + fileName);
        mBitmapLruCache.put(fileName, bitmap);
    }

    public Bitmap getBitmapFromCache(String fileName) {
        Log.d(TAG, "getBitmapFromCache: getting " + fileName);
        return mBitmapLruCache.get(fileName);
    }
}
