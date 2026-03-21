package com.example.mid1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class detail_card extends AppCompatActivity {

    ImageView iv_card , iv_cardrec;

    TextView tv_name,tv_price,tv_desc,tv_detail;
    TextView tv_namerec,tv_pricerec,tv_descrec,tv_detailrec;

    Button btn_buy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        btn_buy.setOnClickListener((v)->{
            checkSmsPermission();
        });

    }


    void init(){
        iv_card = findViewById(R.id.img_card);
        tv_name = findViewById(R.id.tv_name);
        tv_price = findViewById(R.id.tv_price);
        tv_desc = findViewById(R.id.tv_desc);
        tv_detail = findViewById(R.id.tv_detail);
        btn_buy = findViewById(R.id.btn_card1buy);


        iv_cardrec = getIntent().getParcelableExtra("image");
        iv_card.setImageResource(getIntent().getIntExtra("image",0));
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_price.setText(getIntent().getStringExtra("price"));
        tv_desc.setText(getIntent().getStringExtra("desc"));
        tv_detail.setText(getIntent().getStringExtra("detail"));
    }
    private void checkSmsPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            sendSms();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},2);
        }
    }

    private void sendSms() {
        View v = LayoutInflater.from(this).inflate(R.layout.activity_confirm,null);
        String message = "Your order for "+tv_name.getText().toString()+ " "+tv_desc.getText().toString()+ " with the price of " + tv_price.getText().toString()+" has been confirmed";
        String number = "03004817066";
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton("Send", (a,b)->{
                    SmsManager.getDefault().sendTextMessage(number
                            ,null
                            ,message
                            ,null
                            ,null);
                }).setNegativeButton("Cancel",(a,b)->{});

        builder.create().show();
    }
}