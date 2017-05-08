package com.example.zw.volleytest;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by wierZ on 2017/5/4 0004.
 * Email:  wier_zheng@163.com
 */

public class MyApplication extends Application {

    public static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue= Volley.newRequestQueue(getApplicationContext());

    }

    public  static RequestQueue getRequestQueue(){
        return  mRequestQueue;
    }

}
