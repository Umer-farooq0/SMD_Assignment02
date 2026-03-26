package com.example.mid1;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DealItemAdapter extends RecyclerView.Adapter<DealItemAdapter.DealItemViewHolder> {
    Context context;
    ArrayList<items> items;

    public DealItemAdapter(Context context, ArrayList<items> items) {
        this.context = context;
        this.items = items;

    }

    @NonNull
    @Override
    public DealItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.activity_single_deal_item_design, null);
        return new DealItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealItemViewHolder holder, int position) {
        items item = items.get(position);
        holder.iv_dealimg.setImageResource(item.getImage());
        holder.tv_dealname.setText(item.getName());
        holder.tv_dealprice.setText(item.getPrice());
        holder.tv_dealdesc.setText(item.getDescription());



        // Open product detail page on card click
        holder.deals.setOnClickListener((v) -> {
            Intent intent = new Intent(context, detail_card.class);
            intent.putExtra("image", item.getImage());
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("desc", item.getDescription());
            intent.putExtra("detail", item.getDetails());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class DealItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dealimg;
        LinearLayout deals;
        TextView tv_dealname,tv_dealprice,tv_dealdesc;
        public DealItemViewHolder(@NonNull View itemView) {
            super(itemView);
            deals = itemView.findViewById(R.id.deals);
            iv_dealimg = itemView.findViewById(R.id.iv_dealimg);
            tv_dealname = itemView.findViewById(R.id.tv_dealname);
            tv_dealprice = itemView.findViewById(R.id.tv_dealprice);
            tv_dealdesc = itemView.findViewById(R.id.tv_dealdesc);
        }
    }
}