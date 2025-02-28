package com.example.calories_caculator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        // Load sample workout videos
        loadSampleWorkoutVideos();
    }

    private void loadSampleWorkoutVideos() {
        workoutVideos.add(new WorkoutVideo(
                "Bài tập Cardio cơ bản",
                "Mx24iENIEY",
                "Tập luyện cardio 30 phút để đốt cháy calories",
                "https://img.youtube.com/vi/Mx24iENIEY/0.jpg"
        ));
        workoutVideos.add(new WorkoutVideo(
                "Yoga cho người mới bắt đầu",
                "LwWEBTOMyRE",
                "Các động tác yoga cơ bản cho người mới",
                "https://img.youtube.com/vi/LwWEBTOMyRE/0.jpg"
        ));
        workoutAdapter.notifyDataSetChanged();
    }
}