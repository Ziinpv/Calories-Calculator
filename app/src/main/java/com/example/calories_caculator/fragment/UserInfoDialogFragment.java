package com.example.calories_caculator.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.calories_caculator.R;


public class UserInfoDialogFragment extends DialogFragment {

    private TextView tvAge, tvWeight, tvHeight, tvActivityLevel;
    private EditText etAge, etWeight, etHeight, etActivityLevel;
    private Button btnEdit, btnSave, btnClose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEdit = view.findViewById(R.id.btnEdit);
        btnSave = view.findViewById(R.id.btnSave);
        btnClose = view.findViewById(R.id.btnClose);

        tvAge = view.findViewById(R.id.tvAge);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);

        etAge = view.findViewById(R.id.etAge);
        etWeight = view.findViewById(R.id.etWeight);
        etHeight = view.findViewById(R.id.etHeight);
        //etActivityLevel = view.findViewById(R.id.etActivityLevel);

        btnEdit.setOnClickListener(v -> enableEditing(true));
        btnSave.setOnClickListener(v -> saveUserInfo());
        btnClose.setOnClickListener(v -> dismiss());
    }

    private void enableEditing(boolean enable) {
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);

        tvAge.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvWeight.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvHeight.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvActivityLevel.setVisibility(enable ? View.GONE : View.VISIBLE);

        etAge.setVisibility(enable ? View.VISIBLE : View.GONE);
        etWeight.setVisibility(enable ? View.VISIBLE : View.GONE);
        etHeight.setVisibility(enable ? View.VISIBLE : View.GONE);
        etActivityLevel.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void saveUserInfo() {
        tvAge.setText(etAge.getText().toString());
        tvWeight.setText(etWeight.getText().toString());
        tvHeight.setText(etHeight.getText().toString());
        tvActivityLevel.setText(etActivityLevel.getText().toString());

        enableEditing(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}