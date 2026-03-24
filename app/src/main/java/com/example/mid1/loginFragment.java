





    package com.example.mid1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link loginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class loginFragment extends Fragment {

    Button btn_login ;
    TextInputEditText tiet_username , tiet_password;

    SharedPreferences sPref;
    SharedPreferences.Editor editor;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public loginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment loginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static loginFragment newInstance(String param1, String param2) {
        loginFragment fragment = new loginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_login = view.findViewById(R.id.btn_login);
        tiet_username = view.findViewById(R.id.tiet_username);
        tiet_password = view.findViewById(R.id.tiet_password);
        sPref = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        editor = sPref.edit();


        btn_login.setOnClickListener((v)->{
            login();
        });
    }

        void login(){

            if (tiet_username.getText().toString().trim().isEmpty() || tiet_password.getText().toString().trim().isEmpty()){
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
            }
            else
            if(sPref.getString("username","").equals(tiet_username.getText().toString().trim())
                    && sPref.getString("password","").equals(tiet_password.getText().toString().trim())
            ) {
                editor.putBoolean("isLogin",true);
                editor.commit();
                startActivity(new Intent(requireContext(), MainActivity2.class));
                getActivity().finish();
            }
            else {
                Toast.makeText(requireContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
            }
        }

}