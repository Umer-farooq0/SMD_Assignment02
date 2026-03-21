
package com.example.mid1;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class activity_credentials extends AppCompatActivity {

    TabLayout tablayout;
    ViewPager2 viewpager2;
    TabLayoutMediator mediator; //to connect the tabs with the viewpager
    ViewPagerAdapter adapter;

    SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_credentials);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        checkAlreadyLoggedIn();
    }

    private void checkAlreadyLoggedIn() {
        if(sPref.getBoolean("isLogin" , false)){
            startActivity(new Intent(this, MainActivity2.class)) ;
            finish();
        }
    }

    private void init(){
        tablayout = findViewById(R.id.tablayout);
        viewpager2 = findViewById(R.id.viewpager2);
        adapter = new ViewPagerAdapter(this);
        viewpager2.setAdapter(adapter);
        sPref = getSharedPreferences("user",MODE_PRIVATE);
        mediator = new TabLayoutMediator(tablayout, viewpager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                switch (i){
                    case 0:
                        tab.setText("Login");
                        break;
                    case 1:
                        tab.setText("Signup");
                        break;
                }
            }
        });
        mediator.attach();

    }
}