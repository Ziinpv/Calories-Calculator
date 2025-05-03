package com.example.calories_caculator.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories_caculator.R;
import com.example.calories_caculator.adapter.WorkoutAdapter;
import com.example.calories_caculator.model.WorkoutVideo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment {
    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private List<WorkoutVideo> workoutVideos;
    private boolean useFirestore = true; // Thêm biến điều khiển

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.workoutRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        workoutVideos = new ArrayList<>();
        adapter = new WorkoutAdapter(workoutVideos);
        recyclerView.setAdapter(adapter);

        if (useFirestore) {
            fetchWorkoutVideos();
        } else {
            loadSampleWorkoutVideos(); // Thêm phương thức load mẫu
        }
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
        adapter.notifyDataSetChanged();
    }

    private void fetchWorkoutVideos() {
        FirebaseFirestore.getInstance().collection("workout_videos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    workoutVideos.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        WorkoutVideo video = doc.toObject(WorkoutVideo.class);
                        if (video != null) {
                            workoutVideos.add(video);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lấy video tập luyện", e);
                    // Nếu Firestore fail thì load dữ liệu mẫu
                    loadSampleWorkoutVideos();
                });
    }
}
