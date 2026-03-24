package com.example.mid1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Product Description page.
 * Displays product image, name, description, price, and details.
 * "Buy Now" shows a confirmation AlertDialog; on confirmation the product
 * is added to the cart (or its quantity is incremented if already present).
 */
public class detail_card extends AppCompatActivity {

    ImageView iv_card;

    TextView tv_name, tv_price, tv_desc, tv_detail;

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

        btn_buy.setOnClickListener(v -> showBuyConfirmationDialog());
    }

    /** Binds Intent extras to the UI views. */
    void init() {
        iv_card = findViewById(R.id.img_card);
        tv_name = findViewById(R.id.tv_name);
        tv_price = findViewById(R.id.tv_price);
        tv_desc = findViewById(R.id.tv_desc);
        tv_detail = findViewById(R.id.tv_detail);
        btn_buy = findViewById(R.id.btn_card1buy);

        iv_card.setImageResource(getIntent().getIntExtra("image", 0));
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_price.setText(getIntent().getStringExtra("price"));
        tv_desc.setText(getIntent().getStringExtra("desc"));
        tv_detail.setText(getIntent().getStringExtra("detail"));
    }

    /**
     * Shows an AlertDialog asking the user to confirm the purchase.
     * On confirmation the product is added to the global cart list.
     */
    private void showBuyConfirmationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_confirm, null);
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.yes, (dialog, which) -> addToCart())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Adds this product to the cart.
     * If the same product (matched by name + price) is already in the cart,
     * its quantity is incremented; otherwise a new CartItem is created.
     */
    private void addToCart() {
        String productName = tv_name.getText().toString();
        String productPrice = tv_price.getText().toString();
        int productImage = getIntent().getIntExtra("image", 0);
        String productDesc = tv_desc.getText().toString();
        String productDetail = tv_detail.getText().toString();

        // Check if the product is already in the cart
        for (CartItem ci : MyApplication.cartItems) {
            if (ci.getProduct().getName().equals(productName)
                    && ci.getProduct().getPrice().equals(productPrice)) {
                ci.setQuantity(ci.getQuantity() + 1);
                Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Product not yet in cart – add it with quantity 1
        items product = new items(productName, productPrice, productImage, productDesc, productDetail, false);
        MyApplication.cartItems.add(new CartItem(product, 1));
        Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
    }
}