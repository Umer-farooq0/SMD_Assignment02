package com.example.mid1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * RecyclerView adapter for the shopping cart.
 * Each item shows product name, price, quantity controls (+/−),
 * and a three-dot button that immediately removes the item from the cart.
 */
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private final Context context;
    private final ArrayList<CartItem> cartItems;
    private final OnCartChangedListener listener;

    /** Callback fired whenever the cart contents or quantities change. */
    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartItemAdapter(Context context, ArrayList<CartItem> cartItems, OnCartChangedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        items product = cartItem.getProduct();

        holder.tvCartItemName.setText(product.getName());
        holder.tvCartItemPrice.setText(product.getPrice());
        holder.tvCartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Increase quantity by 1
        holder.btnIncrease.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            holder.tvCartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
            if (listener != null) listener.onCartChanged();
        });

        // Decrease quantity by 1 (minimum is 1)
        holder.btnDecrease.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                holder.tvCartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
                if (listener != null) listener.onCartChanged();
            }
        });

        // Immediately remove item from cart (no AlertDialog needed per requirements)
        holder.btnCartItemMenu.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < cartItems.size()) {
                cartItems.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, cartItems.size());
                if (listener != null) listener.onCartChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /** ViewHolder for a single cart item row. */
    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvCartItemName, tvCartItemPrice, tvCartItemQuantity;
        Button btnIncrease, btnDecrease;
        ImageButton btnCartItemMenu;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCartItemName = itemView.findViewById(R.id.tv_cart_item_name);
            tvCartItemPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvCartItemQuantity = itemView.findViewById(R.id.tv_cart_item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_cart_increase);
            btnDecrease = itemView.findViewById(R.id.btn_cart_decrease);
            btnCartItemMenu = itemView.findViewById(R.id.btn_cart_item_menu);
        }
    }
}
