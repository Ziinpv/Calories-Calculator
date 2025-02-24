package com.example.calories_caculator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {
    private RecyclerView workoutRecyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<WorkoutVideo> workoutVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        workoutRecyclerView = findViewById(R.id.workoutRecyclerView);
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        workoutVideos = new ArrayList<>();
        workoutAdapter = new WorkoutAdapter(workoutVideos);
        workoutRecyclerView.setAdapter(workoutAdapter);

        // For demo purposes, using sample data
        // In real app, replace with actual API call
        loadSampleWorkoutVideos();
    }

    private void loadSampleWorkoutVideos() {
        workoutVideos.add(new WorkoutVideo(
                "Bài tập Cardio cơ bản",
                "dQw4w9WgXcQ",
                "Tập luyện cardio 30 phút để đốt cháy calories"
        ));
        workoutVideos.add(new WorkoutVideo(
                "Yoga cho người mới bắt đầu",
                "dQw4w9WgXcQ",
                "Các động tác yoga cơ bản cho người mới"
        ));
        workoutVideos.add(new WorkoutVideo(
                "Bài tập giảm mỡ bụng",
                "dQw4w9WgXcQ",
                "Tập trung giảm mỡ vùng bụng hiệu quả"
        ));
        workoutAdapter.notifyDataSetChanged();
    }
}