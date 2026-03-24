package com.example.mid1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * RecyclerView adapter for the Favourites (Saved) page.
 * Each item has a cart icon (adds to cart) and a three-dot icon
 * (shows an AlertDialog to confirm removal from favourites).
 */
public class FavItemAdapter extends RecyclerView.Adapter<FavItemAdapter.FavItemViewHolder>{
    Context context;
    ArrayList<items> items;

    public FavItemAdapter(Context context, ArrayList<items> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public FavItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.activity_favorite_item_single_view, null);
        return new FavItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavItemViewHolder holder, int position) {
        items item = items.get(position);
        holder.iv_fav_product_image.setImageResource(item.getImage());
        holder.tv_fav_product_name.setText(item.getName());
        holder.tv_fav_price.setText(item.getPrice());
        holder.tv_fav_product_description.setText(item.getDescription());

        // Cart icon: add product to cart (or increase quantity if already present)
        holder.iv_fav_cart_btn.setOnClickListener(v -> {
            addToCart(item);
            Toast.makeText(context, context.getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
        });

        // Three-dot icon: confirm deletion from favourites via AlertDialog
        holder.iv_fav_menu_btn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.delete_from_favourites)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        // Remove from favourites: unmark isFav flag and remove from list
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION && pos < items.size()) {
                            items.get(pos).setFav(false);
                            items.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, items.size());
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    /**
     * Adds the given product to the cart.
     * If the product is already in the cart, its quantity is incremented.
     * Otherwise, a new CartItem with quantity 1 is added.
     */
    private void addToCart(items product) {
        for (CartItem ci : MyApplication.cartItems) {
            if (ci.getProduct().getName().equals(product.getName())
                    && ci.getProduct().getPrice().equals(product.getPrice())) {
                ci.setQuantity(ci.getQuantity() + 1);
                return;
            }
        }
        MyApplication.cartItems.add(new CartItem(product, 1));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class FavItemViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_fav_product_image , iv_fav_menu_btn , iv_fav_cart_btn;
        TextView tv_fav_product_name , tv_fav_product_description , tv_fav_price;


        public FavItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_fav_cart_btn = itemView.findViewById(R.id.iv_fav_cart_btn);
            iv_fav_menu_btn = itemView.findViewById(R.id.iv_fav_menu_btn);
            iv_fav_product_image = itemView.findViewById(R.id.iv_fav_product_image);
            tv_fav_price = itemView.findViewById(R.id.tv_fav_price);
            tv_fav_product_name = itemView.findViewById(R.id.tv_fav_product_name);
            tv_fav_product_description = itemView.findViewById(R.id.tv_fav_product_description);
        }
    }
}
