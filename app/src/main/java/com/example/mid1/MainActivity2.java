package com.example.mid1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity2 extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabLayoutMediator mediator;
    myAdapter2 adapter2;
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
    private void init(){
        tabLayout = findViewById(R.id.tablayout);
        viewPager2 = findViewById(R.id.viewpager2);
        adapter2 = new myAdapter2(this);
        viewPager2.setAdapter(adapter2);
        mediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                switch (i){
                    case 0:
                        tab.setText("Home");
                        tab.setIcon(R.drawable.home);
                        break;
                    case 1:
                        tab.setText("Search");
                        tab.setIcon(R.drawable.search);
                        break;
                    case 2:
                        tab.setText("Saved");
                        tab.setIcon(R.drawable.favorite);
                        break;
                    case 3:
                        tab.setText("Cart");
                        tab.setIcon(R.drawable.cart);
                        break;
                    case 4:
                        tab.setText("Account");
                        tab.setIcon(R.drawable.account);
                        break;
                }
            }

        });
        mediator.attach();
    }
}
