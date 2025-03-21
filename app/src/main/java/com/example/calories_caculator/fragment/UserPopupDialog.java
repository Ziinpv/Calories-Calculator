package com.example.calories_caculator.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.calories_caculator.R;
import com.example.calories_caculator.firebase.FirestoreHelper;

public class UserPopupDialog {
    private Dialog dialog;
    private EditText etAge, etWeight, etHeight, etActivityLevel;
    private TextView tvName, tvGender;
    private Button btnEdit, btnSave, btnClose;
    private FirestoreHelper firestoreHelper;
    private String userId;

    public UserPopupDialog(Context context, String userId) {
        this.userId = userId;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_user);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = dialog.findViewById(R.id.tvName);
        tvGender = dialog.findViewById(R.id.tvGender);
        etAge = dialog.findViewById(R.id.etAge);
        etWeight = dialog.findViewById(R.id.etWeight);
        etHeight = dialog.findViewById(R.id.etHeight);
        etActivityLevel = dialog.findViewById(R.id.etActivityLevel);
        btnEdit = dialog.findViewById(R.id.btnEdit);
        btnSave = dialog.findViewById(R.id.btnSave);
        btnClose = dialog.findViewById(R.id.btnClose);

        firestoreHelper = new FirestoreHelper();
        loadUserInfo();

        btnSave.setVisibility(View.GONE);
        btnEdit.setOnClickListener(v -> enableEditing(true));
        btnSave.setOnClickListener(v -> saveUserInfo(context));
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void loadUserInfo() {
        firestoreHelper.loadUserInfo(userId, new FirestoreHelper.UserCallback() {
            @Override
            public void onUserLoaded(String name, String gender, Long age, Long weight, Long height, String activityLevel) {
                tvName.setText(name);
                tvGender.setText(gender);
                etAge.setText(String.valueOf(age));
                etWeight.setText(String.valueOf(weight));
                etHeight.setText(String.valueOf(height));
                etActivityLevel.setText(activityLevel);
                enableEditing(false);
            }

            @Override
            public void onError(String errorMessage) {
                tvName.setText("Lỗi tải dữ liệu");
            }
        });
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

    private void saveUserInfo(Context context) {
        int age = Integer.parseInt(etAge.getText().toString());
        int weight = Integer.parseInt(etWeight.getText().toString());
        int height = Integer.parseInt(etHeight.getText().toString());
        String activityLevel = etActivityLevel.getText().toString();

        firestoreHelper.saveUserInfo(context, userId, age, weight, height, activityLevel, new FirestoreHelper.SaveCallback() {
            @Override
            public void onSuccess() {
                enableEditing(false);
            }

            @Override
            public void onError(String errorMessage) {
                tvName.setText("Lỗi cập nhật");
            }
        });
    }

    public void show() {
        dialog.show();
    }
}

