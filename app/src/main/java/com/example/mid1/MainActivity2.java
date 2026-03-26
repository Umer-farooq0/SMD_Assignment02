package com.example.mid1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;
    myAdapter2 adapter2;
    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.viewpager2);
        adapter2 = new myAdapter2(this);
        viewPager2.setAdapter(adapter2);
        viewPager2.setUserInputEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isNavigating) return true;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                viewPager2.setCurrentItem(0, false);
            } else if (id == R.id.nav_search) {
                viewPager2.setCurrentItem(1, false);
            } else if (id == R.id.nav_saved) {
                viewPager2.setCurrentItem(2, false);
            } else if (id == R.id.nav_cart) {
                viewPager2.setCurrentItem(3, false);
            } else if (id == R.id.nav_account) {
                viewPager2.setCurrentItem(4, false);
            }
            return true;
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                isNavigating = true;
                switch (position) {
                    case 0: bottomNavigationView.setSelectedItemId(R.id.nav_home); break;
                    case 1: bottomNavigationView.setSelectedItemId(R.id.nav_search); break;
                    case 2: bottomNavigationView.setSelectedItemId(R.id.nav_saved); break;
                    case 3: bottomNavigationView.setSelectedItemId(R.id.nav_cart); break;
                    case 4: bottomNavigationView.setSelectedItemId(R.id.nav_account); break;
                }
                isNavigating = false;
            }
        });
    }
}
