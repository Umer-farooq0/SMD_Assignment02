package com.example.mid1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    RecyclerView rvItems , rvDealItems;

    ItemAdapter adapter ;
    DealItemAdapter dealAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvItems = view.findViewById(R.id.rvItems);
        rvDealItems = view.findViewById(R.id.rvDealItems);

        // Only populate items list once to avoid duplicates when re-navigating
        if (MyApplication.items.isEmpty()) {
            MyApplication.items.add(new items("Sony Premium Wireless Headphones", "$349.99", R.drawable.sony_premium_1, "Model: WH-1000M4, Black", getString(R.string.desc_cards), false));
            MyApplication.items.add(new items("Sony Premium Wireless Headphones", "$349.99", R.drawable.sony_premium_2, "Model: WH-1000M4, Beige", getString(R.string.desc_cards), false));
            MyApplication.items.add(new items("RODE PodMic", "$108.20", R.drawable.rod_podmic, "Dynamic microphone, Speaker microphone", getString(R.string.desc_deal), false));
        }

        adapter = new ItemAdapter(requireContext(), MyApplication.items);
        dealAdapter = new DealItemAdapter(requireContext(),MyApplication.items);
        rvItems.setLayoutManager(new GridLayoutManager(requireContext(),2));
        rvItems.setAdapter(adapter);
        rvItems.setNestedScrollingEnabled(false);
        rvDealItems.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        rvDealItems.setAdapter(dealAdapter);
    }
}