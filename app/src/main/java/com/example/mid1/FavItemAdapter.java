package com.example.mid1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
