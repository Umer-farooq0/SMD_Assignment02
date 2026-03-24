package com.example.mid1;

import android.app.Application;

import java.util.ArrayList;

public class MyApplication extends Application {
    public static ArrayList<items> items;
    public static FavItemAdapter favItemAdapter; // Add this
    public static ArrayList<items> favList;

    @Override
    public void onCreate() {
        super.onCreate();
        items =  new ArrayList<>();

    }
}
