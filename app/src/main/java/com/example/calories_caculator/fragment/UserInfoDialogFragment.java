package com.example.calories_caculator.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.calories_caculator.firebase.FirestoreHelper;

import com.example.calories_caculator.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class UserInfoDialogFragment extends BottomSheetDialogFragment {
    private EditText etAge, etWeight, etHeight, etActivityLevel;
    private Button btnEdit, btnSave, btnClose;
    private TextView tvName,tvGender;
    private TextView tvWeight;
    private TextView tvHeight;
    private TextView tvActivityLevel;
    private DocumentReference userRef;
    private ListenerRegistration userListener;
    private FirebaseFirestore db;

    //private final String USER_ID = "user1";

    public static UserInfoDialogFragment newInstance(String userId) {
        UserInfoDialogFragment fragment = new UserInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("user1", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_user, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvGender = view.findViewById(R.id.tvGender);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);
        etAge = view.findViewById(R.id.etAge);
        etWeight = view.findViewById(R.id.etWeight);
        etHeight = view.findViewById(R.id.etHeight);
        etActivityLevel = view.findViewById(R.id.etActivityLevel);

        btnEdit = view.findViewById(R.id.btnEdit);
        btnSave = view.findViewById(R.id.btnSave);
        btnClose = view.findViewById(R.id.btnClose);

        db = FirebaseFirestore.getInstance();
//        userRef = db.collection("user").document(USER_ID);
//        loadUserData();

        String userId = getArguments().getString("user1");
        if (userId != null) {
            loadUserInfo(userId);
        }

        btnSave.setVisibility(View.GONE);

        btnEdit.setOnClickListener(v -> enableEditing(true));
        btnSave.setOnClickListener(v -> saveUserInfo("user1"));
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }
//    private void loadUserData() {
//        userListener = userRef.addSnapshotListener((snapshot, e) -> {
//            if (e != null) {
//                Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (snapshot != null && snapshot.exists()) {
//                tvName.setText(snapshot.getString("name")); // Hiển thị tên
//                tvGender.setText(snapshot.getString("gender")); // Hiển thị giới tính
//                etAge.setText(String.valueOf(snapshot.getLong("age")));
//                etWeight.setText(String.valueOf(snapshot.getDouble("wieght")));
//                etHeight.setText(String.valueOf(snapshot.getDouble("height")));
//                etActivityLevel.setText(snapshot.getString("acivity_level"));
//            }
//        });
//    }

    private void loadUserInfo(String userId) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvName.setText(String.valueOf(document.getString("name"))); // Hiển thị tên
                        tvGender.setText(String.valueOf(document.getString("gender")));
                        etAge.setText(String.valueOf(document.getLong("age")));
                        etWeight.setText(String.valueOf(document.getLong("weight")));
                        etHeight.setText(String.valueOf(document.getLong("height")));
                        etActivityLevel.setText(document.getString("activityLevel"));
                        enableEditing(false);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
                );
    }
    private void enableEditing(boolean enable) {
        etAge.setEnabled(enable);
        etWeight.setEnabled(enable);
        etHeight.setEnabled(enable);
        etActivityLevel.setEnabled(enable);

        if (enable) {
            etAge.setInputType(InputType.TYPE_CLASS_NUMBER);
            etWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            etHeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            etActivityLevel.setInputType(InputType.TYPE_CLASS_TEXT);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        } else {
            etAge.setInputType(InputType.TYPE_NULL);
            etWeight.setInputType(InputType.TYPE_NULL);
            etHeight.setInputType(InputType.TYPE_NULL);
            etActivityLevel.setInputType(InputType.TYPE_NULL);
            btnSave.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
        }
    }

    private void saveUserInfo(String userId) {
        int age = Integer.parseInt(etAge.getText().toString());
        int weight = Integer.parseInt(etWeight.getText().toString());
        int height = Integer.parseInt(etHeight.getText().toString());
        String activityLevel = etActivityLevel.getText().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("age", age);
        updatedData.put("weight", weight);
        updatedData.put("height", height);
        updatedData.put("activityLevel", activityLevel);

        db.collection("user").document(userId).update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    enableEditing(false);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi cập nhật!", Toast.LENGTH_SHORT).show()
                );
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) {
            userListener.remove();
        }
    }
}