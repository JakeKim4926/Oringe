package com.ssafy.oringe.activity.record;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringe.api.record.RecordService;
import com.ssafy.oringe.api.record.dto.RecordResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        builder.setTitle("Record Details")
                .setView(R.layout.fragment_record_detail_dialog)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        Long recordId = getArguments().getLong(ARG_RECORD_ID);
        fetchRecordDetails(recordId);

        return builder.create();
    }

    private void fetchRecordDetails(Long recordId) {
        recordService.getRecord(recordId).enqueue(new Callback<RecordResponse>() {
            @Override
            public void onResponse(Call<RecordResponse> call, Response<RecordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecordResponse recordResponse = response.body();
                    recordTemplates = recordResponse.getRecordTemplates();
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
                    Log.e("ChallengeDetail", "Response code: " + response.code());
                    Log.e("ChallengeDetail", "Response message: " + response.message());
                    Toast.makeText(getActivity(), "Failed to load challenge detail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengeDetailIdResponse> call, Throwable t) {
                Log.e("ChallengeDetail", "Error: " + t.getMessage(), t);
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
        TextView textView = new TextView(getActivity());
        textView.setText(content);
        textView.setTextSize(24);
        return textView;
    }

    private View createContentView(String content) {
        TextView textView = new TextView(getActivity());
        textView.setText(content);
        textView.setTextSize(16);
        return textView;
    }


    private View createImageView(String content) {
        ImageView imageView = new ImageView(getActivity());
        // Load the image from the URL using Glide
        Glide.with(getActivity())
                .load(content)
                .placeholder(R.drawable.logo_org) // You can use a placeholder image if needed
                .into(imageView);
        return imageView;
    }

    private View createAudioView(String content) {
        Button button = new Button(getActivity());
        button.setText("Play Audio");

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
        VideoView videoView = new VideoView(getActivity());
        videoView.setVideoPath(content);
        videoView.setMediaController(new android.widget.MediaController(getActivity()));
        videoView.requestFocus();
        videoView.start();

        return videoView;
    }

    private View createSTTView(String content) {
        TextView textView = new TextView(getActivity());
        textView.setText(content); // STT text
        textView.setTextSize(16);
        return textView;
    }

    private View createTTSView(String content) {
        TextView textView = new TextView(getActivity());
        textView.setText(content); // TTS text
        textView.setTextSize(16);
        return textView;
    }
}
