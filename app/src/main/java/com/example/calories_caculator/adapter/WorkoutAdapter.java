package com.example.calories_caculator.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories_caculator.R;
import com.example.calories_caculator.model.WorkoutVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<WorkoutVideo> workoutVideos;

    public WorkoutAdapter(List<WorkoutVideo> workoutVideos) {
        this.workoutVideos = workoutVideos;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        holder.bind(workoutVideos.get(position));
    }

    @Override
    public int getItemCount() {
        return workoutVideos.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private TextView titleTextView, descriptionTextView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.videoTitle);
            descriptionTextView = itemView.findViewById(R.id.videoDescription);
        }

        public void bind(WorkoutVideo video) {
            titleTextView.setText(video.getTitle());
            descriptionTextView.setText(video.getDescription());
            Picasso.get().load(video.getThumbnailUrl()).into(thumbnailImageView);

            View.OnClickListener openYouTube = v -> {
                String url = "https://www.youtube.com/watch?v=" + video.getVideoId();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(intent);
            };

            thumbnailImageView.setOnClickListener(openYouTube);
            titleTextView.setOnClickListener(openYouTube);
        }
    }
}
