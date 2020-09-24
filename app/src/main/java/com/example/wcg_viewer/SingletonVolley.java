package com.example.wcg_viewer;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonVolley {
    private static SingletonVolley sVolley;
    private RequestQueue mQueue;
    private Context sContext;

    public static SingletonVolley get(Context context){
        if(sVolley == null){
            sVolley = new SingletonVolley(context);
        }
        return sVolley;
    }

    private SingletonVolley(Context context) {
        sContext = context.getApplicationContext();
        mQueue = getQueue();
    }

    public RequestQueue getQueue(){
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(sContext);
        }
        return mQueue;
    }

    public <T> void addRequest(Request<T> request){
        getQueue().add(request);
    }

    public void startQueue(){
        mQueue.start();
    }

    public void stopQueue(){
        mQueue.stop();
    }
}
