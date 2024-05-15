package com.ssafy.oringe.activity.record;

import android.content.Intent;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_AUDIO;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_CONTENT;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_IMAGE;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_STT;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_TITLE;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_TTS;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_VIDEO;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.common.MainActivity;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.api.record.AudioConverter;
import com.ssafy.oringe.api.record.RecordService;
import com.ssafy.oringe.api.record.dto.RecordCreateReqDto;
import com.ssafy.oringe.api.record.dto.RecordCreateTTSDto;
import com.ssafy.oringe.common.ChallengeDetailOrders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RecordCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String API_URL;
    private List<Challenge> challengeList;
    private String selectedChallengeTitle;
    private List<Integer> challengeDetailOrder = new ArrayList<>();
    private Spinner spinner;
    private Long memberId;
    private Long challengeId;

    private RecordService recordService;
    private RecordService recordJsonService;

    private RecordCreateReqDto recordCreateReqDto;
    private RecordCreateReqDto recordCreateReqDtoSave;

    private LinearLayout buttonContainer;
    private ChallengeDetailService challengeDetailService;

    private File imageFile;
    private File audioFile;
    private File videoFile;
    private File TTSFile;

    Button buttonTitle;
    Button buttonContent;
    Button buttonImage;
    Button buttonAudio;
    Button buttonVideo;
    Button buttonSTT;
    Button buttonTTS;
    Button buttonOK;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record_create);
        API_URL = getString(R.string.APIURL);

        // Intent에서 challengeTitle 받기
        selectedChallengeTitle = getIntent().getStringExtra("challengeTitle");

        recordCreateReqDtoSave = new RecordCreateReqDto();
        recordCreateReqDto = new RecordCreateReqDto();

        setupRetrofitClient();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        buttonContainer = findViewById(R.id.buttonContainer);
        buttonTitle = findViewById(R.id.button_title);
        buttonContent = findViewById(R.id.button_content);
        buttonImage = findViewById(R.id.button_image);
        buttonAudio = findViewById(R.id.button_audio);
        buttonVideo = findViewById(R.id.button_video);
        buttonSTT = findViewById(R.id.button_stt);
        buttonTTS = findViewById(R.id.button_tts);
        buttonOK = findViewById(R.id.button_ok);

        buttonTitle.setVisibility(View.GONE);
        buttonContent.setVisibility(View.GONE);
        buttonImage.setVisibility(View.GONE);
        buttonAudio.setVisibility(View.GONE);
        buttonVideo.setVisibility(View.GONE);
        buttonSTT.setVisibility(View.GONE);
        buttonTTS.setVisibility(View.GONE);

        videoView = findViewById(R.id.video_view);
        videoView.setVisibility(View.GONE);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        buttonTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTitleDialog(CHALLENGE_DETAIL_TITLE);
            }
        });

        buttonContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTitleDialog(CHALLENGE_DETAIL_CONTENT);
            }
        });

        buttonImage.setOnClickListener(v -> openImageSelector());

        buttonAudio.setOnClickListener(v -> openAudioSelector());

        buttonVideo.setOnClickListener(v -> openVideoSelector());

        buttonSTT.setOnClickListener(v -> openAudioSelector());

        buttonTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTitleDialog(CHALLENGE_DETAIL_TITLE);
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();

                Intent intent = new Intent(RecordCreateActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                challengeDetailOrder.clear();
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long savedLoginId = sharedPref.getLong("loginId", 0);
        memberId = savedLoginId;

        getChallengeList(savedLoginId);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Challenge selectedChallenge = (Challenge) parent.getItemAtPosition(position);
        challengeId = selectedChallenge.getChallengeId();
        Toast.makeText(this, "현재 챌린지 : " + selectedChallenge.getChallengeTitle(), Toast.LENGTH_LONG).show();

        getChallengeDetailIdAndOrderList(challengeId);
    }

    public void showButton() {
        buttonContainer.removeAllViews();
        videoView.setVisibility(View.GONE);

        for (int value : challengeDetailOrder) {
            Button buttonToAdd = null;
            if (value == CHALLENGE_DETAIL_TITLE.getOrderCode())      buttonToAdd = buttonTitle;
            if (value == CHALLENGE_DETAIL_CONTENT.getOrderCode())    buttonToAdd = buttonContent;
            if (value == CHALLENGE_DETAIL_IMAGE.getOrderCode())      buttonToAdd = buttonImage;
            if (value == CHALLENGE_DETAIL_AUDIO.getOrderCode())      buttonToAdd = buttonVideo;
            if (value == CHALLENGE_DETAIL_VIDEO.getOrderCode())      buttonToAdd = buttonAudio;
            if (value == CHALLENGE_DETAIL_STT.getOrderCode())        buttonToAdd = buttonSTT;
            if (value == CHALLENGE_DETAIL_TTS.getOrderCode())        buttonToAdd = buttonTTS;

            // Remove the button from its parent before adding it to the new container
            if (buttonToAdd != null) {
                ViewGroup parent = (ViewGroup) buttonToAdd.getParent();
                if (parent != null) {
                    parent.removeView(buttonToAdd);
                }
                buttonToAdd.setVisibility(View.VISIBLE);
                buttonContainer.addView(buttonToAdd);
            }
        }

    }

    public void sendData() {
        System.out.println("save : " + recordCreateReqDtoSave);
        recordCreateReqDto.setMemberId(memberId);
        recordCreateReqDto.setChallengeId(challengeId);

        AtomicInteger completedCalls = new AtomicInteger(0);
        int totalCall = challengeDetailOrder.size();
        // completedCalls, totalCall
        for (int value : challengeDetailOrder) {
            if (value == CHALLENGE_DETAIL_TITLE.getOrderCode())      insertRecordTitle(recordCreateReqDtoSave.getRecordTitle(), completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_CONTENT.getOrderCode())    insertRecordContent(recordCreateReqDtoSave.getRecordContent(), completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_IMAGE.getOrderCode())      insertRecordImage(completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_AUDIO.getOrderCode())      insertRecordAudio(completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_VIDEO.getOrderCode())      insertRecordVideo(completedCalls, totalCall);
//            if (value == CHALLENGE_DETAIL_STT.getOrderCode())        insertSTT(recordCreateReqDtoSave.getRecordTitle());
//            if (value == CHALLENGE_DETAIL_TTS.getOrderCode())        insertTTS(recordCreateReqDtoSave.getRecordTitle());
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public void getChallengeList(Long memberId) {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getData(memberId, 2);
        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    challengeList = response.body();
                    ArrayAdapter<Challenge> adapter = new ArrayAdapter<>(RecordCreateActivity.this,
                            android.R.layout.simple_spinner_item,
                            challengeList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(RecordCreateActivity.this, "Failed to fetch challenges", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupRetrofitClient() {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        challengeDetailService = retrofit.create(ChallengeDetailService.class);

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        recordService = retrofit2.create(RecordService.class);
    }

    private void getChallengeDetailIdAndOrderList(Long challengeId) {
        challengeDetailService.getTemplatesId(challengeId).enqueue(new Callback<ChallengeDetailIdResponse>() {
            @Override
            public void onResponse(Call<ChallengeDetailIdResponse> call, Response<ChallengeDetailIdResponse> response) {
                if (response.isSuccessful()) {
                    ChallengeDetailIdResponse challengeDetailIdResponse = response.body();
                    if (challengeDetailIdResponse != null) {
                        getTemplatesOrder(challengeDetailIdResponse.getBody());
                    } else {
                        Toast.makeText(RecordCreateActivity.this, "Challenge Detail ID not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("API Error Body", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RecordCreateActivity.this, "Failed to fetch Challenge Detail ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengeDetailIdResponse> call, Throwable t) {
                System.out.println("여기냐 ");
                System.out.println(t.getMessage());
                Toast.makeText(RecordCreateActivity.this, "An error occurred while fetching Challenge Detail ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditTitleDialog(ChallengeDetailOrders challengeDetailOrders) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();

                if(challengeDetailOrders == CHALLENGE_DETAIL_TITLE && inputText.length() > 100) {
                    Toast.makeText(RecordCreateActivity.this, "제목은 100자 이하만 가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                } else if(challengeDetailOrders == CHALLENGE_DETAIL_CONTENT && inputText.length() > 1000) {
                    Toast.makeText(RecordCreateActivity.this, "본문은 1000자 이하만 가능합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateButtonTitle(inputText, challengeDetailOrders);
                if(challengeDetailOrders == CHALLENGE_DETAIL_TITLE)     recordCreateReqDtoSave.setRecordTitle(inputText);
                else if(challengeDetailOrders == CHALLENGE_DETAIL_CONTENT)     recordCreateReqDtoSave.setRecordContent(inputText);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateButtonTitle(String text, ChallengeDetailOrders challengeDetailOrders) {
        Button buttonTextTemp = null;
        if(challengeDetailOrders == CHALLENGE_DETAIL_TITLE) {
            buttonTextTemp = findViewById(R.id.button_title);
        } else if(challengeDetailOrders == CHALLENGE_DETAIL_CONTENT) {
            buttonTextTemp = findViewById(R.id.button_content);
        }
        buttonTextTemp.setText(text);
        // Adjust button width based on the text length
        ViewGroup.LayoutParams params = buttonTextTemp.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT; // Allow the button to size itself
        buttonTextTemp.setLayoutParams(params);
    }

    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for picking an image

    private void openImageSelector(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private static final int REQUEST_VIDEO_PICK = 1001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            setButtonBackground(imageUri);
            String imagePath = getPathFromUri(imageUri);
            if (imagePath != null) {
                imageFile = new File(imagePath);
            }
        } else if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri audioUri = data.getData();
            setupAudioButton(audioUri);
            String audioPath = getPathFromUriAudio(audioUri);
            if (audioPath != null) {
                File file = new File(audioPath);

                if (audioPath.endsWith(".m4a")) {
                    // 원래 파일 이름을 유지하면서 확장자를 .wav로 변경
                    String outputFilePath = audioPath.substring(0, audioPath.lastIndexOf(".")) + ".wav";
                    File outputFile = new File(outputFilePath);

                    AudioConverter.convertToWav(file, outputFile, new AudioConverter.ConversionListener() {
                        @Override
                        public void onConversionSuccess(File convertedFile) {
                            audioFile = convertedFile;
                            Toast.makeText(RecordCreateActivity.this, "되어라 : " + convertedFile.getName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onConversionFailed(String error) {
                            Toast.makeText(RecordCreateActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if(audioPath.endsWith(".mp3")
                        || audioPath.endsWith(".wav")
                        || audioPath.endsWith(".aac")
                        || audioPath.endsWith(".ogg")
                        || audioPath.endsWith(".flac")) {
                    audioFile = file;
                } else {
                    Toast.makeText(RecordCreateActivity.this, "m4a,mp3,wav,aac,ogg,flac 파일만 허용 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK) {
            videoUri = data.getData();
            setupVideoPlayback();
            String videoPath = getPathFromUriVideo(videoUri);
            if (videoPath != null) {
                videoFile = new File(videoPath);
            }
        }

    }
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    private String getPathFromUriAudio(Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        return result;
    }

    private String getPathFromUriVideo(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

//    private void setButtonBackground(Uri imageUri) {
//        Button buttonImage = findViewById(R.id.button_image);
//        buttonImage.post(() -> {
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                Drawable drawable = Drawable.createFromStream(inputStream, imageUri.toString());
//                buttonImage.setBackground(drawable);
//            } catch (FileNotFoundException e) {
//                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void setButtonBackground(Uri imageUri) {
        Button buttonImage = findViewById(R.id.button_image);
        final int maxHeightPx = dpToPx(200);  // Convert dp to pixel

        buttonImage.post(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap.getHeight() > maxHeightPx) {
                    // Calculate the scale factor
                    float scale = (float) maxHeightPx / bitmap.getHeight();
                    int newWidth = (int) (bitmap.getWidth() * scale);

                    // Create a new scaled bitmap
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, maxHeightPx, true);
                    bitmap.recycle();  // Recycle the original bitmap as it's no longer needed

                    BitmapDrawable drawable = new BitmapDrawable(getResources(), scaledBitmap);
                    buttonImage.setBackground(drawable);

                    // Adjust the button size to match the scaled image
                    ViewGroup.LayoutParams params = buttonImage.getLayoutParams();
                    params.width = newWidth;
                    params.height = maxHeightPx;
                    buttonImage.setLayoutParams(params);
                } else {
                    // Use the original bitmap if it's within limits
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                    buttonImage.setBackground(drawable);

                    ViewGroup.LayoutParams params = buttonImage.getLayoutParams();
                    params.width = bitmap.getWidth();
                    params.height = bitmap.getHeight();
                    buttonImage.setLayoutParams(params);
                }
                buttonImage.setText("");
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Convert dp to pixel based on the screen density.
     * @param dp the value in dp to convert
     * @return the corresponding pixel value
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private static final int PICK_AUDIO_REQUEST = 101;  // Request code for picking an audio file

    private void openAudioSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }


    private MediaPlayer mediaPlayer;

    private void setupAudioButton(Uri audioUri) {
        Button buttonAudio = findViewById(R.id.button_audio);
        buttonAudio.setText("Play Audio");

        buttonAudio.setOnClickListener(v -> {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(RecordCreateActivity.this, audioUri);
                mediaPlayer.setOnCompletionListener(mp -> {
                    buttonAudio.setText("Play Audio");
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                });
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonAudio.setText("Resume Audio");
            } else {
                mediaPlayer.start();
                buttonAudio.setText("Pause Audio");
            }
        });
    }

    private Uri videoUri; // To hold the video URI

    private void openVideoSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");

        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }

    private void setupVideoPlayback() {

        buttonVideo.setVisibility(View.GONE);  // 버튼 숨기기
        videoView.setVisibility(View.VISIBLE); // 비디오 뷰 보이게

        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this)); // Adding controls
        videoView.requestFocus();
        videoView.start();

        // Listener for completion of video
        videoView.setOnCompletionListener(mp -> {
            // Optionally reset the video to start or handle UI changes
        });

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.resume();
        }
    }

    // ==========================================================
    // =
    // =                        API 연결
    // =
    // ==========================================================


    private void getTemplatesOrder(Long challengeDetailId) {
        challengeDetailService.getTemplatesOrder(challengeDetailId).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    challengeDetailOrder = response.body();
                    showButton();
                    // TODO: Handle the order list, e.g., show in UI or process further
                } else {
                    Toast.makeText(RecordCreateActivity.this, "Failed to fetch order list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while fetching order list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postRecord() {
        System.out.println("when you send it : " + recordCreateReqDto);
        Call<String> call = recordService.postRecord(recordCreateReqDto);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecordCreateActivity.this, "인증 생성 성공", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 실패 처리
                Toast.makeText(RecordCreateActivity.this, "An error occurred while add record", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertRecordTitle(String title, AtomicInteger completedCalls, int totalCalls) {
        Call<String> call = recordService.insertRecordTitle(title);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordTitle(title);
                } else {
                    try {
                        System.out.println("Response was not successful: " + response.code());
                        System.out.println("Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                System.out.println("Request failed: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding title", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void insertRecordContent(String content, AtomicInteger completedCalls, int totalCalls) {
        Call<String> call = recordService.insertRecordContent(content);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordContent(content);
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding content", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void insertRecordImage(AtomicInteger completedCalls, int totalCalls) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
        Call<String> call = recordService.insertRecordImage(body, memberId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordImage(result);
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding image", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void insertRecordAudio(AtomicInteger completedCalls, int totalCalls) {
        if (audioFile == null) {
            Toast.makeText(this, "No audio file selected", Toast.LENGTH_SHORT).show();
            checkIfAllCallsCompleted(completedCalls, totalCalls);
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), audioFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestFile);
        Call<String> call = recordService.insertRecordAudio(body, memberId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordAudio(result);
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding audio", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void insertRecordVideo(AtomicInteger completedCalls, int totalCalls) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), videoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("video", videoFile.getName(), requestFile);
        Call<String> call = recordService.insertRecordVideo(body, memberId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordVideo(result);
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding video", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void insertSTT(File file, Long memberId, AtomicInteger completedCalls, int totalCalls) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("stt", file.getName(), requestFile);
        Call<String> call = recordService.insertSTT(body, memberId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordSTT(result);
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding STT", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void insertTTS(RecordCreateTTSDto recordCreateTTSDto, AtomicInteger completedCalls, int totalCalls) {
        Call<String> call = recordService.insertTTS(recordCreateTTSDto);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordTTS(result);
                }
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding TTS", Toast.LENGTH_SHORT).show();
                checkIfAllCallsCompleted(completedCalls, totalCalls);
            }
        });
    }

    private void checkIfAllCallsCompleted(AtomicInteger completedCalls, int totalCalls) {
        if (completedCalls.incrementAndGet() == totalCalls) {
            System.out.println("All API calls completed");
            postRecord();
        }
    }

}
