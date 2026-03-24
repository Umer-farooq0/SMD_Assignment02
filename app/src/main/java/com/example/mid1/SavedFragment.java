package com.example.mid1;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    RecyclerView rv_saved;
    FavItemAdapter adapter;
    ArrayList<items> favList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_saved = view.findViewById(R.id.rv_saved);
        rv_saved.setHasFixedSize(true);

        // Initialize favorite list
        favList = new ArrayList<>();
        updateFavList();

        adapter = new FavItemAdapter(requireContext(), favList);
        rv_saved.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_saved.setAdapter(adapter);

        // Store adapter reference globally so HomeFragment can notify it
        MyApplication.favItemAdapter = adapter;
        MyApplication.favList = favList;
    }

    private void updateFavList() {
        favList.clear();
        for (items item : MyApplication.items) {
            if (item.isFav()) {
                favList.add(item);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavList();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}