package com.example.wcg_viewer;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class SingletonIconLruCache {
    private static SingletonIconLruCache sSingletonIconLruCache;
    private final LruCache<String, Bitmap> mBitmapLruCache;
    private static final String TAG = "SingletonLruCache";


    private SingletonIconLruCache(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        int cacheSize = am.getMemoryClass() * 1024 * 1024 / 8;
        mBitmapLruCache = new LruCache<>(cacheSize);
    }

    public static SingletonIconLruCache get(Context context) {
        if (sSingletonIconLruCache == null)
            sSingletonIconLruCache = new SingletonIconLruCache(context);
        return sSingletonIconLruCache;
    }

    public void putBitmapToCache(String fileName, Bitmap bitmap) {
        Log.d(TAG, "putBitmapToCache: putting " + fileName);
        mBitmapLruCache.put(fileName, bitmap);
    }

    public Bitmap getBitmapFromCache(String fileName) {
        Log.d(TAG, "getBitmapFromCache: getting " + fileName);
        Log.d(TAG, "getBitmapFromCache: return null = " + (mBitmapLruCache.get(fileName) == null));
        return mBitmapLruCache.get(fileName);
    }

    public Bitmap getBitmapFromCacheWithUrl(String url) {
        Log.d(TAG, "getBitmapFromCacheWithUrl: get url: " + url);
        String[] fileName = url.split("/");
        return getBitmapFromCache(fileName[fileName.length - 1]);
    }
}
