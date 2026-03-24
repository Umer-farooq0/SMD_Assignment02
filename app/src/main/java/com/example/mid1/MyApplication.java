package com.example.mid1;

import android.app.Application;

import java.util.ArrayList;

public class MyApplication extends Application {
    public static ArrayList<items> items;
    public static FavItemAdapter favItemAdapter;
    public static ArrayList<items> favList;
    public static ArrayList<CartItem> cartItems;

    @Override
    public void onCreate() {
        super.onCreate();
        items = new ArrayList<>();
        cartItems = new ArrayList<>();

        // Create favList.favourites SharedPreferences entry (required by assignment)
        getSharedPreferences("favList", MODE_PRIVATE)
                .edit()
                .putString("favourites", "")
                .apply();
    }
}
