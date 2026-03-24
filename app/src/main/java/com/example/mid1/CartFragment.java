package com.example.mid1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * Cart fragment displaying all products added to the cart.
 * Users can increase/decrease quantities, remove items with the three-dot icon,
 * and checkout by pressing the Checkout button (which sends an SMS with order details).
 */
public class CartFragment extends Fragment implements CartItemAdapter.OnCartChangedListener {

    private static final int SMS_PERMISSION_REQUEST = 3;

    RecyclerView rvCart;
    TextView tvCartTotal;
    TextView tvCartEmpty;
    MaterialButton btnCheckout;
    CartItemAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rv_cart);
        tvCartTotal = view.findViewById(R.id.tv_cart_total);
        tvCartEmpty = view.findViewById(R.id.tv_cart_empty);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        // Set up RecyclerView with cart items from global list
        cartAdapter = new CartItemAdapter(requireContext(), MyApplication.cartItems, this);
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);

        updateTotalAndVisibility();

        btnCheckout.setOnClickListener(v -> checkoutWithSms());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
            updateTotalAndVisibility();
        }
    }

    /** Called by CartItemAdapter whenever the cart changes (add, remove, quantity change). */
    @Override
    public void onCartChanged() {
        updateTotalAndVisibility();
    }

    /**
     * Recalculates the total price and toggles the empty-cart message visibility.
     * Total is the sum of (price × quantity) for all cart items.
     */
    private void updateTotalAndVisibility() {
        double total = calculateTotal();
        tvCartTotal.setText(getString(R.string.total_label) + " $" + String.format("%.2f", total));

        if (MyApplication.cartItems.isEmpty()) {
            rvCart.setVisibility(View.GONE);
            tvCartEmpty.setVisibility(View.VISIBLE);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            tvCartEmpty.setVisibility(View.GONE);
        }
    }

    /** Parses a price string like "$349.99" and returns the double value. */
    private double parsePrice(String priceStr) {
        try {
            return Double.parseDouble(priceStr.replace("$", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** Returns the sum of price * quantity for all cart items. */
    private double calculateTotal() {
        double total = 0;
        for (CartItem ci : MyApplication.cartItems) {
            total += parsePrice(ci.getProduct().getPrice()) * ci.getQuantity();
        }
        return total;
    }

    /** Builds the SMS order summary and initiates sending. */
    private void checkoutWithSms() {
        if (MyApplication.cartItems.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.cart_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendOrderSms();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST);
        }
    }

    /**
     * Builds and sends an SMS containing all cart items and the total price.
     * Format: product name (xQty) - $lineTotal, then Total at the bottom.
     */
    private void sendOrderSms() {
        StringBuilder message = new StringBuilder("Order from FastMart:\n");
        int index = 1;
        for (CartItem ci : MyApplication.cartItems) {
            double itemPrice = parsePrice(ci.getProduct().getPrice());
            double lineTotal = itemPrice * ci.getQuantity();
            message.append(index++).append(". ")
                    .append(ci.getProduct().getName())
                    .append(" (x").append(ci.getQuantity()).append(") - $")
                    .append(String.format("%.2f", lineTotal)).append("\n");
        }
        message.append("Total: $").append(String.format("%.2f", calculateTotal()));

        String phoneNumber = getString(R.string.sms_phone_number);
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message.toString());
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            Toast.makeText(requireContext(), "Order placed! SMS sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Unable to send order SMS. Please check your SMS permissions and try again.",
                    Toast.LENGTH_LONG).show();
        }
    }
}