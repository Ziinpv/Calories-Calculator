package com.example.calories_caculator.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.calories_caculator.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class StatsBottomSheet extends BottomSheetDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_status, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(view);

        Spinner spinnerTime = view.findViewById(R.id.spinner_time);
        TextView tvCaloriesTotal = view.findViewById(R.id.tv_calories_total);
        Button btnClose = view.findViewById(R.id.btn_close);

        // Thiết lập dữ liệu cho Spinner
        String[] timeOptions = {"Hôm nay", "Tuần này", "Tháng này", "Chọn ngày"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, timeOptions);
        spinnerTime.setAdapter(adapter);

        // Xử lý khi người dùng chọn thống kê theo ngày, tuần, tháng
        spinnerTime.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = timeOptions[position];
                tvCaloriesTotal.setText("Dữ liệu calo cho: " + selectedOption);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}