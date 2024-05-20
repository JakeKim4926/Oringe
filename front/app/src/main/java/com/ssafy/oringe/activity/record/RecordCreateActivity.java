package com.ssafy.oringe.activity.record;

import android.app.ProgressDialog;
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
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.regex.Pattern;

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

    private RecordCreateReqDto recordCreateReqDto;
    private RecordCreateReqDto recordCreateReqDtoSave;

    private RecordCreateTTSDto recordCreateTTSDto;

    private LinearLayout buttonContainer;
    private ChallengeDetailService challengeDetailService;

    private File imageFile;
    private File audioFile;
    private File videoFile;
    private File STTFile;

    Button buttonTitle;
    Button buttonContent;
    Button buttonImage;
    Button buttonAudio;
    Button buttonVideo;
    Button buttonSTT;
    Button buttonTTS;
    Button buttonOK;
    VideoView videoView;

    private AlertDialog loadingDialog;

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
        recordCreateTTSDto = new RecordCreateTTSDto();

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

        buttonSTT.setOnClickListener(v -> openSTTSelector());

        buttonTTS.setOnClickListener(v -> showEditTitleDialog(CHALLENGE_DETAIL_TTS));

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                sendData();
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
        if (selectedChallenge.getChallengeId() != null && selectedChallenge.getChallengeId() != -1) {
            challengeId = selectedChallenge.getChallengeId();
            Toast.makeText(this, "현재 챌린지 : " + selectedChallenge.getChallengeTitle(), Toast.LENGTH_LONG).show();
            getChallengeDetailIdAndOrderList(challengeId);
        }
    }

    public void showButton() {
        buttonContainer.removeAllViews();
        videoView.setVisibility(View.GONE);

        for (int value : challengeDetailOrder) {
            Button buttonToAdd = null;
            if (value == CHALLENGE_DETAIL_TITLE.getOrderCode())      buttonToAdd = buttonTitle;
            if (value == CHALLENGE_DETAIL_CONTENT.getOrderCode())    buttonToAdd = buttonContent;
            if (value == CHALLENGE_DETAIL_IMAGE.getOrderCode())      buttonToAdd = buttonImage;
            if (value == CHALLENGE_DETAIL_AUDIO.getOrderCode())      buttonToAdd = buttonAudio;
            if (value == CHALLENGE_DETAIL_VIDEO.getOrderCode())      buttonToAdd = buttonVideo;
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
        recordCreateTTSDto.setMemberId(memberId);

        AtomicInteger completedCalls = new AtomicInteger(0);
        int totalCall = challengeDetailOrder.size();
        System.out.println("Size ===== " + totalCall);
        // completedCalls, totalCall
        for (int value : challengeDetailOrder) {
            if (value == CHALLENGE_DETAIL_TITLE.getOrderCode())      insertRecordTitle(recordCreateReqDtoSave.getRecordTitle(), completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_CONTENT.getOrderCode())    insertRecordContent(recordCreateReqDtoSave.getRecordContent(), completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_IMAGE.getOrderCode())      insertRecordImage(completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_AUDIO.getOrderCode())      insertRecordAudio(completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_VIDEO.getOrderCode())      insertRecordVideo(completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_STT.getOrderCode())        insertSTT(completedCalls, totalCall);
            if (value == CHALLENGE_DETAIL_TTS.getOrderCode())        insertTTS(recordCreateTTSDto, completedCalls, totalCall);
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
                    List<Challenge> challenges = new ArrayList<>();
                    // "선택하세요"를 첫 번째 아이템으로 추가
                    Challenge placeholder = new Challenge();
                    placeholder.setChallengeTitle("선택하세요");
                    challenges.add(placeholder);
                    challenges.addAll(response.body());

                    ArrayAdapter<Challenge> adapter = new ArrayAdapter<>(RecordCreateActivity.this,
                            android.R.layout.simple_spinner_item, challenges);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    // 인텐트에서 받은 challengeTitle과 일치하는 항목 찾기
                    if (selectedChallengeTitle != null && !selectedChallengeTitle.isEmpty()) {
                        for (int i = 0; i < challenges.size(); i++) {
                            if (selectedChallengeTitle.equals(challenges.get(i).getChallengeTitle())) {
                                spinner.setSelection(i);
                                break;
                            }
                        }
                    }
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

                if (challengeDetailOrders == CHALLENGE_DETAIL_TITLE) {
                    if (inputText.length() > 100) {
                        Toast.makeText(RecordCreateActivity.this, "제목은 100자 이하만 가능합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordCreateReqDtoSave.setRecordTitle(inputText);
                } else if (challengeDetailOrders == CHALLENGE_DETAIL_CONTENT) {
                    if ( inputText.length() > 1000 ) {
                        Toast.makeText(RecordCreateActivity.this, "본문은 1000자 이하만 가능합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordCreateReqDtoSave.setRecordContent(inputText);
                } else if (challengeDetailOrders == CHALLENGE_DETAIL_TTS) {
                    if (inputText.length() > 1000) {
                        Toast.makeText(RecordCreateActivity.this, "해당 내용은 1000자 이하만 가능합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Pattern pattern = Pattern.compile("^[가-힣0-9,.'\"!?\\s]+$");
                    if (!pattern.matcher(inputText).matches()) {
                        Toast.makeText(RecordCreateActivity.this, "한글과 숫자만 입력 가능합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordCreateTTSDto.setRecordTTS(inputText);
                }

                updateButtonTitle(inputText, challengeDetailOrders);
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
        if (challengeDetailOrders == CHALLENGE_DETAIL_TITLE) {
            buttonTextTemp = findViewById(R.id.button_title);
        } else if (challengeDetailOrders == CHALLENGE_DETAIL_CONTENT) {
            buttonTextTemp = findViewById(R.id.button_content);
        } else if (challengeDetailOrders == CHALLENGE_DETAIL_TTS) {
            buttonTextTemp = findViewById(R.id.button_tts);
        }
        buttonTextTemp.setText(text);
        // Adjust button width based on the text length
        ViewGroup.LayoutParams params = buttonTextTemp.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT; // Allow the button to size itself
        buttonTextTemp.setLayoutParams(params);
    }

    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for picking an image

    private void openImageSelector() {
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
                audioFile = new File(audioPath);
                if(audioPath.endsWith(".mp3")
                        || audioPath.endsWith(".wav")
                        || audioPath.endsWith(".aac")
                        || audioPath.endsWith(".ogg")
                        || audioPath.endsWith(".flac")) {
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
        } else if (requestCode == PICK_WAV_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri audioUri = data.getData();
            setupSTTButton(audioUri);
            String audioPath = getPathFromUriAudio(audioUri);
            System.out.println("여기 ? ");
            if (audioPath != null) {
                System.out.println("여긴 ?");
                if (audioPath.endsWith(".wav")) {
                    STTFile = new File(audioPath);
                } else {
                    Toast.makeText(RecordCreateActivity.this, "wav 파일만 허용 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("RecordCreateActivity", "Failed to get audio path");
            }
        } else {
            Log.e("RecordCreateActivity", "Request failed or canceled");
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

    private void setButtonBackground(Uri imageUri) {
        Button buttonImage = findViewById(R.id.button_image);
        final int maxHeightPx = dpToPx(200);  // Convert dp to pixel

        buttonImage.post(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Close the input stream to reopen it for Exif data
                inputStream.close();

                inputStream = getContentResolver().openInputStream(imageUri);
                ExifInterface exif = new ExifInterface(inputStream);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = rotateBitmap(bitmap, orientation);

                int originalWidth = bitmap.getWidth();
                int originalHeight = bitmap.getHeight();
                int newWidth = originalWidth;
                int newHeight = originalHeight;

                if (originalHeight > maxHeightPx) {
                    // Calculate the scale factor
                    float scale = (float) maxHeightPx / originalHeight;
                    newWidth = (int) (originalWidth * scale);
                    newHeight = maxHeightPx;
                }

                // Create a new scaled bitmap
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                bitmap.recycle();  // Recycle the original bitmap as it's no longer needed

                BitmapDrawable drawable = new BitmapDrawable(getResources(), scaledBitmap);
                buttonImage.setBackground(drawable);

                // Adjust the button size to match the scaled image
                ViewGroup.LayoutParams params = buttonImage.getLayoutParams();
                params.width = newWidth;
                params.height = newHeight;
                buttonImage.setLayoutParams(params);

                buttonImage.setText("");
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;  // No rotation needed
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*"); // Only allow .wav files to be selected

        startActivityForResult(Intent.createChooser(intent, "Select Audio"), PICK_AUDIO_REQUEST);
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

    private void setupSTTButton(Uri audioUri) {
        Button buttonAudio = findViewById(R.id.button_stt);
        buttonAudio.setText("Play Audio");

        buttonAudio.setOnClickListener(v -> {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(RecordCreateActivity.this, audioUri);
                mediaPlayer.setOnCompletionListener(mp -> {
                    buttonSTT.setText("Play Audio");
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                });
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonSTT.setText("Resume Audio");
            } else {
                mediaPlayer.start();
                buttonSTT.setText("Pause Audio");
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

    private static final int PICK_WAV_REQUEST = 3;
    private void openSTTSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*"); // Only allow .wav files to be selected
        startActivityForResult(intent, PICK_WAV_REQUEST);
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.loading_dialog, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            loadingDialog = builder.create();
        }
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
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

    private void postRecord(Runnable onSuccess, Runnable onFailure) {
        System.out.println("when you send it : " + recordCreateReqDto);
        Call<String> call = recordService.postRecord(recordCreateReqDto);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecordCreateActivity.this, "인증 생성 성공", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    System.out.println("Response was not successful: " + response.code());
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("Request failed: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(RecordCreateActivity.this, "An error occurred while adding record", Toast.LENGTH_SHORT).show();
                onFailure.run();
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

    private void insertSTT(AtomicInteger completedCalls, int totalCalls) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), STTFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("stt", STTFile.getName(), requestFile);
        Call<String> call = recordService.insertSTT(body, memberId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    recordCreateReqDto.setRecordSTT(result);
                } else {
                    Toast.makeText(RecordCreateActivity.this, "An error occurred while getting res", Toast.LENGTH_SHORT).show();
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
        if (completedCalls.incrementAndGet() >= totalCalls) {
            System.out.println(completedCalls.get());
            System.out.println("All API calls completed");

            postRecord(
                    () -> {
                        // 성공 시
                        hideLoadingDialog();  // 로딩 화면 숨기기
                        Intent intent = new Intent(RecordCreateActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        challengeDetailOrder.clear();
                    },
                    () -> {
                        // 실패 시
                        hideLoadingDialog();  // 로딩 화면 숨기기
                        Toast.makeText(RecordCreateActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                    }
            );
        }
    }

}
