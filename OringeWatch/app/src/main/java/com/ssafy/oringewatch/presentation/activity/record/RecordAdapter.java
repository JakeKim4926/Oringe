package com.ssafy.oringewatch.presentation.activity.record;

import static androidx.core.app.PendingIntentCompat.getActivity;
import static com.ssafy.oringewatch.presentation.common.Util.API_URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringewatch.presentation.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringewatch.presentation.api.record.Record;
import com.ssafy.oringewatch.presentation.api.record.RecordService;
import com.ssafy.oringewatch.presentation.api.record.dto.RecordResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> records;
    private Context context;

    private Long challengeDetailId;

    private List<Integer> templatesOrder;
    private List<String> recordTemplates;
    private ChallengeDetailService challengeDetailService;
    private RecordService recordService;

    public RecordAdapter(List<Record> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_record_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        recordService = retrofit.create(RecordService.class);
        challengeDetailService = retrofit.create(ChallengeDetailService.class);

        // holder.renderContent를 호출하여 각 아이템 뷰에 데이터를 설정합니다.
        holder.renderContent();
    }

    @Override
    public int getItemCount() {
        return records.size();
    }



    public class RecordViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout contentContainer;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            contentContainer = itemView.findViewById(R.id.content_container);
        }

        private void fetchRecordDetails(Long recordId, TextView textDate) {
            recordService.getRecord(recordId).enqueue(new Callback<RecordResponse>() {
                @Override
                public void onResponse(Call<RecordResponse> call, Response<RecordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RecordResponse recordResponse = response.body();
                        recordTemplates = recordResponse.getRecordTemplates();

                        // Set the date in the TextView
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(recordResponse.getRecordDate());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
                            textDate.setText(dateFormat.format(date));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        fetchChallengeDetail(recordResponse.getChallengeId());
                    } else {
//                    Toast.makeText(, "Failed to load record details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RecordResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchChallengeDetail(Long challengeId) {
            challengeDetailService.getTemplatesId(challengeId).enqueue(new Callback<ChallengeDetailIdResponse>() {
                @Override
                public void onResponse(Call<ChallengeDetailIdResponse> call, Response<ChallengeDetailIdResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        challengeDetailId = response.body().getBody();
                        fetchChallengeDetailOrder(challengeDetailId);
                    } else {
//                    Toast.makeText(getActivity(), "Failed to load challenge detail", Toast.LENGTH_SHORT).show();
                        System.out.println("Failed to load challenge detail");
                    }
                }

                @Override
                public void onFailure(Call<ChallengeDetailIdResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Error: " + t.getMessage());
                }
            });
        }

        private void fetchChallengeDetailOrder(Long challengeDetailId) {
            challengeDetailService.getTemplatesOrder(challengeDetailId).enqueue(new Callback<List<Integer>>() {
                @Override
                public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        templatesOrder = response.body();
                        renderContent();
                    } else {
//                        Toast.makeText(getActivity(), "Failed to fetch order list", Toast.LENGTH_SHORT).show();
                        System.out.println("Failed to fetch order list");
                    }
                }

                @Override
                public void onFailure(Call<List<Integer>> call, Throwable t) {
//                    Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Error: " + t.getMessage());
                }
            });
        }

        public void renderContent() {
            // Clear any existing views
            contentContainer.removeAllViews();

            // Loop through templatesOrder and create corresponding views
            for (int i = 0; i < templatesOrder.size(); i++) {
                int order = templatesOrder.get(i);
                String content = recordTemplates.get(i);

                View view = null;
                switch (order) {
                    case 1: // Title
                        view = createTitleView(content);
                        break;
                    case 2: // Content
                        view = createContentView(content);
                        break;
                    case 3: // Image
                        view = createImageView(content);
                        break;
                    case 4: // Audio
                        view = createAudioView(content);
                        break;
                    case 5: // Video
                        view = createVideoView(content);
                        break;
                    case 6: // STT
                        view = createSTTView(content);
                        break;
                    case 7: // TTS
                        view = createTTSView(content);
                        break;
                }

                if (view != null) {
                    contentContainer.addView(view);
                }
            }
        }

        private View createTitleView(String content) {
            Button button = new Button(context);
            button.setText(content);
            button.setTextSize(18);
            button.setTextColor(context.getResources().getColor(android.R.color.black));
            button.setTypeface(null, Typeface.BOLD);
            button.setBackgroundResource(R.drawable.button_color_light_huge);
            button.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 20, 20, 20);
            button.setLayoutParams(params);
            return button;
        }

        private View createContentView(String content) {
            Button button = new Button(context);
            button.setText(content);
            button.setTextSize(18);
            button.setBackgroundResource(R.drawable.button_color_light_huge);
            button.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(193)
            );
            params.setMargins(20, 20, 20, 20);
            button.setLayoutParams(params);
            return button;
        }

        private View createImageView(String content) {
            ImageView imageView = new ImageView(context);

            // Set layout parameters with initial width and height
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 20, 20, 20);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            // Load the image with Glide
            Glide.with(context)
                    .load(content)
                    .placeholder(R.drawable.loading)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            // Get the intrinsic dimensions of the loaded image
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();

                            // Calculate aspect ratio
                            float aspectRatio = (float) width / height;

                            // Set the ImageView dimensions based on the aspect ratio
                            int maxWidth = context.getResources().getDisplayMetrics().widthPixels - dpToPx(40); // subtracting margins
                            int targetHeight = (int) (maxWidth / aspectRatio);

                            // Update layout parameters with calculated dimensions
                            params.width = maxWidth;
                            params.height = targetHeight;
                            imageView.setLayoutParams(params);
                            imageView.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            imageView.setImageDrawable(placeholder);
                        }
                    });

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullscreenImageActivity.class);
                intent.putExtra("image_url", content);
                context.startActivity(intent);
            });

            return imageView;
        }

        private View createAudioView(String content) {
            Button button = new Button(context);
            button.setText("Play Audio");
            button.setTextSize(18);
            button.setBackgroundResource(R.drawable.button_color_gray_huge);
            button.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(193)
            );
            params.setMargins(20, 20, 20, 20);
            button.setLayoutParams(params);

            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(content);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            button.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    button.setText("Resume Audio");
                } else {
                    mediaPlayer.start();
                    button.setText("Pause Audio");
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                button.setText("Play Audio");
                mediaPlayer.reset();
            });

            return button;
        }

        private View createVideoView(String content) {
            RelativeLayout videoLayout = new RelativeLayout(context);
            VideoView videoView = new VideoView(context);
            ProgressBar progressBar = new ProgressBar(context);

            // Set initial layout parameters
            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            videoParams.setMargins(20, 20, 20, 20);
            videoView.setLayoutParams(videoParams);

            RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(progressParams);

            videoLayout.addView(videoView);
            videoLayout.addView(progressBar);

            // Retrieve video dimensions
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(content);
            String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            int width = Integer.parseInt(widthStr);
            int height = Integer.parseInt(heightStr);

            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Calculate aspect ratio
            float aspectRatio = (float) width / height;

            // Set the VideoView dimensions based on the aspect ratio
            int maxWidth = context.getResources().getDisplayMetrics().widthPixels - dpToPx(40); // subtracting margins
            int targetHeight = (int) (maxWidth / aspectRatio);

            // Update layout parameters with calculated dimensions
            videoParams.width = maxWidth;
            videoParams.height = targetHeight;
            videoView.setLayoutParams(videoParams);

            videoView.setVideoPath(content);
            videoView.setMediaController(new android.widget.MediaController(context));
            videoView.setOnPreparedListener(mp -> progressBar.setVisibility(View.GONE)); // Hide progress bar when video is ready
            videoView.setOnInfoListener((mp, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    progressBar.setVisibility(View.VISIBLE); // Show progress bar when buffering starts
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    progressBar.setVisibility(View.GONE); // Hide progress bar when buffering ends
                }
                return false;
            });

            videoView.requestFocus();
            videoView.start();

            videoView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullscreenVideoActivity.class);
                intent.putExtra("video_url", content);
                context.startActivity(intent);
            });

            return videoLayout;
        }

        private View createSTTView(String content) {
            Button button = new Button(context);
            button.setText(content);
            button.setTextSize(18);
            button.setBackgroundResource(R.drawable.button_color_gray_huge);
            button.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(193)
            );
            params.setMargins(20, 20, 20, 20);
            button.setLayoutParams(params);
            return button;
        }

        private View createTTSView(String content) {
            Button button = new Button(context);
            button.setText("Play TTS Audio");
            button.setTextSize(18);
            button.setBackgroundResource(R.drawable.button_color_gray_huge);
            button.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(193)
            );
            params.setMargins(20, 20, 20, 20);
            button.setLayoutParams(params);

            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(content);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            button.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    button.setText("Resume TTS Audio");
                } else {
                    mediaPlayer.start();
                    button.setText("Pause TTS Audio");
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                button.setText("Play TTS Audio");
                mediaPlayer.reset();
            });

            return button;
        }

        private int dpToPx(int dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }
    }
}
