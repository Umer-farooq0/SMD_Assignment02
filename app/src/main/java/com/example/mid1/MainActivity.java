package com.example.mid1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    ImageView img_rod,img_card1,img_card2;
    TextView tv_price1,tv_price2,tv_price3,tv_name1,tv_name2,tv_name3,tv_desc1,tv_desc2,tv_desc3;
    LinearLayout deals,card1,card2;
    Button btn_logout;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;


    String detail_deal = "The RØDE PodMic is a professional dynamic microphone designed for podcasting, streaming, and voice recording. It delivers rich, clear sound with excellent background noise rejection, making it ideal for studio and home setups. Built with a durable metal body and internal pop filter, it ensures reliable performance and clean vocals.";
    String detail_card = "Sony Premium Wireless Headphones are high-quality over-ear headphones that deliver immersive sound with rich bass and clear highs. Featuring wireless Bluetooth connectivity, long battery life, and comfortable ear cushions, they’re great for music, calls, and travel. Built-in noise cancellation keeps distractions out so you can enjoy audio in style.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        btn_logout.setOnClickListener(v->{
            startActivity(new Intent(this,splash.class));
            editor.putBoolean("user.isLogin",false);
            editor.commit();
            finish();
        });
        deals.setOnClickListener((v)->{
            Intent i = new Intent(this,detail_card.class);
            i.putExtra("image", R.drawable.rod_podmic);
            i.putExtra("name", tv_name1.getText().toString());
            i.putExtra("price", tv_price1.getText().toString());
            i.putExtra("desc",tv_desc1.getText().toString());
            i.putExtra("detail",detail_deal);

            startActivity(i);
        });

        card1.setOnClickListener((v)->{
            Intent i = new Intent(this,detail_card.class);
            i.putExtra("image", R.drawable.sony_premium_1);
            i.putExtra("name", tv_name2.getText().toString());
            i.putExtra("price", tv_price2.getText().toString());
            i.putExtra("desc",tv_desc2.getText().toString());
            i.putExtra("detail",detail_card);
            startActivity(i);
        });

        card2.setOnClickListener((v)->{
            Intent i = new Intent(this,detail_card.class);
            i.putExtra("image", R.drawable.sony_premium_2);
            i.putExtra("name", tv_name3.getText().toString());
            i.putExtra("price", tv_price3.getText().toString());
            i.putExtra("desc",tv_desc3.getText().toString());
            i.putExtra("detail",detail_card);
            startActivity(i);
        });

    }


    void init(){
        deals = findViewById(R.id.deals);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        img_rod = findViewById(R.id.img_rod);
        img_card1 = findViewById(R.id.img_card1);
        img_card2 = findViewById(R.id.img_card2);
        tv_price1 = findViewById(R.id.tv_price1);
        tv_price2 = findViewById(R.id.tv_price2);
        tv_price3 = findViewById(R.id.tv_price3);
        tv_name1 = findViewById(R.id.tv_name1);
        tv_name2 = findViewById(R.id.tv_name2);
        tv_name3 = findViewById(R.id.tv_name3);
        tv_desc1 = findViewById(R.id.tv_desc1);
        tv_desc2 = findViewById(R.id.tv_desc2);
        tv_desc3 = findViewById(R.id.tv_desc3);
        btn_logout = findViewById(R.id.btn_logout);
        sPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = sPref.edit();
    }


}

