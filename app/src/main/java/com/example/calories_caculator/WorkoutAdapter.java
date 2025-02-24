package com.example.calories_caculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
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
        WorkoutVideo video = workoutVideos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return workoutVideos.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private YouTubePlayerView youTubePlayerView;
        private TextView titleTextView;
        private TextView descriptionTextView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = itemView.findViewById(R.id.youtubePlayerView);
            titleTextView = itemView.findViewById(R.id.videoTitle);
            descriptionTextView = itemView.findViewById(R.id.videoDescription);
        }

        public void bind(WorkoutVideo video) {
            titleTextView.setText(video.getTitle());
            descriptionTextView.setText(video.getDescription());

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(video.getVideoId(), 0);
                }
            });
        }
    }
}