package com.ssafy.oringe.activity.record;

import static com.ssafy.oringe.common.Util.comment;

import android.animation.StateListAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.common.FullscreenImageActivity;
import com.ssafy.oringe.activity.common.FullscreenVideoActivity;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringe.api.record.RecordService;
import com.ssafy.oringe.api.record.dto.RecordResponse;

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

public class RecordDetailActivity extends AppCompatActivity {
    private static final String ARG_RECORD_ID = "record_id";
    private static final String BASE_URL = "https://k10b201.p.ssafy.io/";
    private RecordService recordService;
    private ChallengeDetailService challengeDetailService;
    private Long challengeDetailId;
    private List<Integer> templatesOrder;
    private List<String> recordTemplates;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        recordService = retrofit.create(RecordService.class);
        challengeDetailService = retrofit.create(ChallengeDetailService.class);

        // Set up date TextView
        TextView textDate = findViewById(R.id.text_date);

        Long recordId = getIntent().getLongExtra(ARG_RECORD_ID, -1);
        fetchRecordDetails(recordId, textDate);
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
                    Toast.makeText(RecordDetailActivity.this, "Failed to load record details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecordResponse> call, Throwable t) {
                Toast.makeText(RecordDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RecordDetailActivity.this, "Failed to load challenge detail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengeDetailIdResponse> call, Throwable t) {
                Toast.makeText(RecordDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RecordDetailActivity.this, "Failed to fetch order list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Toast.makeText(RecordDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderContent() {
        // Get the container layout where we will add the views
        LinearLayout contentContainer = findViewById(R.id.content_container);

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

    private View createLabeledView(String labelText, View viewContent) {
        // 라벨 생성
        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(14);
        label.setTextColor(Color.parseColor("#BBBBBB"));
        label.setPadding(8, 8, 8, 8);

        // 레이아웃 생성
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setBackgroundResource(R.drawable.rounded_corner_background);
        // 라벨과 콘텐츠 뷰를 레이아웃에 추가
        layout.addView(label);
        layout.addView(viewContent);

        return layout;
    }

    private View createTitleView(String content) {
        Button button = new Button(this);
        button.setText(content);
        button.setStateListAnimator(null);
        button.setTextSize(20);
        button.setBackgroundColor(Color.parseColor("#FDFDFD"));
        button.setTextColor(Color.parseColor("#FF6B00"));
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(16, 16, 16, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 20, 20, 20);
        button.setLayoutParams(params);

        return createLabeledView("제목", button);
    }

    private View createContentView(String content) {
        Button button = new Button(this);
        button.setText(content);
        button.setTextSize(18);
        button.setBackgroundColor(Color.parseColor("#FDFDFD"));
        button.setStateListAnimator(null);
        button.setBackgroundColor(Color.parseColor("#FDFDFD"));
        button.setPadding(16, 16, 16, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(193)
        );
        params.setMargins(20, 20, 20, 20);
        button.setLayoutParams(params);

        return createLabeledView("내용", button);
    }

    private View createImageView(String content) {
        ImageView imageView = new ImageView(this);

        // Set layout parameters with initial width and height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 20, 20, 20);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Load the image with Glide
        Glide.with(this)
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
                        int maxWidth = getResources().getDisplayMetrics().widthPixels - dpToPx(40); // subtracting margins
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
            Intent intent = new Intent(RecordDetailActivity.this, FullscreenImageActivity.class);
            intent.putExtra("image_url", content);
            startActivity(intent);
        });

        return createLabeledView("이미지", imageView);
    }

    private View createAudioView(String content) {
        Button button = new Button(this);
        button.setText("Play Audio");
        button.setTextSize(18);
        button.setBackgroundColor(Color.parseColor("#FDFDFD"));
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

        return createLabeledView("오디오", button);
    }

    private View createVideoView(String content) {
        RelativeLayout videoLayout = new RelativeLayout(this);
        VideoView videoView = new VideoView(this);
        ProgressBar progressBar = new ProgressBar(this);

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
        int maxWidth = getResources().getDisplayMetrics().widthPixels - dpToPx(40); // subtracting margins
        int targetHeight = (int) (maxWidth / aspectRatio);

        // Update layout parameters with calculated dimensions
        videoParams.width = maxWidth;
        videoParams.height = targetHeight;
        videoView.setLayoutParams(videoParams);

        videoView.setVideoPath(content);
        videoView.setMediaController(new android.widget.MediaController(this));
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
            Intent intent = new Intent(RecordDetailActivity.this, FullscreenVideoActivity.class);
            intent.putExtra("video_url", content);
            startActivity(intent);
        });

        return createLabeledView("비디오", videoLayout);
    }

    private View createSTTView(String content) {
        Button button = new Button(this);
        button.setText(content);
        button.setTextSize(18);
        button.setPadding(16, 16, 16, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(193)
        );
        params.setMargins(20, 20, 20, 20);
        button.setLayoutParams(params);

        return createLabeledView("STT", button);
    }

    private View createTTSView(String content) {
        // TextView 생성
        TextView textView = new TextView(this);
        String quotedComment = "\"" + comment + "\"";
        textView.setText(quotedComment);
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#555555"));
        textView.setPadding(8, 8, 8, 8);

        // TextView의 레이아웃 매개변수 설정
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParams.gravity = Gravity.CENTER_HORIZONTAL; // TextView를 수평으로 가운데에 배치
        textView.setLayoutParams(textViewParams);

        // Button 생성
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.play); // Ripple 효과 적용
        button.setPadding(8, 8, 8, 8);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                120,
                120
        );

        buttonParams.setMargins(20, 20, 20, 20);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL; // 버튼을 수평으로 가운데에 배치
        button.setLayoutParams(buttonParams);


        // 미디어 플레이어 설정
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
                button.setBackgroundResource(R.drawable.resume);
            } else {
                mediaPlayer.start();
                button.setBackgroundResource(R.drawable.pause);
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            button.setBackgroundResource(R.drawable.play);
            mediaPlayer.reset();
        });

        // LinearLayout 생성 및 설정
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(20, 20, 20, 20);

        // TextView와 Button을 레이아웃에 추가
        layout.addView(button); // Button을 TextView 위에 추가
        layout.addView(textView);

        return createLabeledView("TTS", layout);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
