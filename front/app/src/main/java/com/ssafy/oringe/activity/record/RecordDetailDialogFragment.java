package com.ssafy.oringe.activity.record;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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

public class RecordDetailDialogFragment extends DialogFragment {
    private static final String ARG_RECORD_ID = "record_id";
    private static final String BASE_URL = "https://k10b201.p.ssafy.io/";
    private RecordService recordService;
    private ChallengeDetailService challengeDetailService;
    private Long challengeDetailId;
    private List<Integer> templatesOrder;
    private List<String> recordTemplates;

    public static RecordDetailDialogFragment newInstance(Long recordId) {
        RecordDetailDialogFragment fragment = new RecordDetailDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RECORD_ID, recordId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        recordService = retrofit.create(RecordService.class);
        challengeDetailService = retrofit.create(ChallengeDetailService.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_record_detail, null);

        // Set up OK button
//        Button buttonOk = view.findViewById(R.id.button_ok);
//        buttonOk.setOnClickListener(v -> dismiss());

        // Set up date TextView
        TextView textDate = view.findViewById(R.id.text_date);

        builder.setView(view);

        Long recordId = getArguments().getLong(ARG_RECORD_ID);
        fetchRecordDetails(recordId, textDate);

        AlertDialog dialog = builder.create();

        // Set the dialog background to transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set the dialog width and height to wrap content
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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
                    Toast.makeText(getActivity(), "Failed to load record details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecordResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Failed to load challenge detail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengeDetailIdResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Failed to fetch order list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderContent() {
        // Get the container layout where we will add the views
        LinearLayout contentContainer = getDialog().findViewById(R.id.content_container);

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
        Button button = new Button(getActivity());
        button.setText(content);
        button.setTextSize(18);
        button.setTextColor(getResources().getColor(android.R.color.black));
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
        Button button = new Button(getActivity());
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
        ImageView imageView = new ImageView(getActivity());

        // Set layout parameters with initial width and height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 20, 20, 20);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Load the image with Glide
        Glide.with(getActivity())
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
            Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
            intent.putExtra("image_url", content);
            startActivity(intent);
        });

        return imageView;
    }



    private View createAudioView(String content) {
        Button button = new Button(getActivity());
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

//    private View createVideoView(String content) {
//        VideoView videoView = new VideoView(getActivity());
//
//        // Set initial layout parameters
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        params.setMargins(20, 20, 20, 20);
//        videoView.setLayoutParams(params);
//
//        // Retrieve video dimensions
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(content);
//        String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//        String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
//        int width = Integer.parseInt(widthStr);
//        int height =     Integer.parseInt(heightStr);
//        try {
//            retriever.release();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Calculate aspect ratio
//        float aspectRatio = (float) width / height;
//
//        // Set the VideoView dimensions based on the aspect ratio
//        int maxWidth = getResources().getDisplayMetrics().widthPixels - dpToPx(40); // subtracting margins
//        int targetHeight = (int) (maxWidth / aspectRatio);
//
//        // Update layout parameters with calculated dimensions
//        params.width = maxWidth;
//        params.height = targetHeight;
//        videoView.setLayoutParams(params);
//
//        videoView.setVideoPath(content);
//        videoView.setMediaController(new android.widget.MediaController(getActivity()));
//        videoView.requestFocus();
//        videoView.start();
//
//        videoView.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), FullscreenVideoActivity.class);
//            intent.putExtra("video_url", content);
//            startActivity(intent);
//        });
//
//
//        return videoView;
//    }
private View createVideoView(String content) {
    RelativeLayout videoLayout = new RelativeLayout(getActivity());
    VideoView videoView = new VideoView(getActivity());
    ProgressBar progressBar = new ProgressBar(getActivity());

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
    videoView.setMediaController(new android.widget.MediaController(getActivity()));
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
        Intent intent = new Intent(getActivity(), FullscreenVideoActivity.class);
        intent.putExtra("video_url", content);
        startActivity(intent);
    });

    return videoLayout;
}


    private View createSTTView(String content) {
        Button button = new Button(getActivity());
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
        Button button = new Button(getActivity());
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
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
