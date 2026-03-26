package com.example.mid1;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    EditText etSearch;
    ImageButton btnSearchBack;
    RecyclerView rvSearchResults;
    ItemAdapter searchAdapter;
    ArrayList<items> filteredItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.et_search);
        btnSearchBack = view.findViewById(R.id.btn_search_back);
        rvSearchResults = view.findViewById(R.id.rv_search_results);

        filteredItems = new ArrayList<>(MyApplication.items);

        searchAdapter = new ItemAdapter(requireContext(), filteredItems);
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(searchAdapter);

        // Back arrow: dismiss keyboard and clear the search field
        btnSearchBack.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.clearFocus();
            hideKeyboard();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().trim());
            }
        });


    }

    private void filterProducts(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            filteredItems.addAll(MyApplication.items);
        } else {
            for (items item : MyApplication.items) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list in case items were added/modified
        filterProducts(etSearch != null ? etSearch.getText().toString().trim() : "");
    }
}