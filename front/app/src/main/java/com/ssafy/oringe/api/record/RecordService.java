package com.ssafy.oringe.api.record;

import com.ssafy.oringe.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringe.api.record.dto.RecordCreateReqDto;
import com.ssafy.oringe.api.record.dto.RecordCreateTTSDto;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RecordService {
    @POST("record")
    Call<String> postRecord(@Body RecordCreateReqDto recordCreateReqDto);

    // 제목 인증 생성
    @POST("record/title")
    Call<String> insertRecordTitle(@Query("recordTitle") String recordTitle);

    // 본문 인증 생성
    @POST("record/content")
    Call<String> insertRecordContent(@Query("recordContent") String recordContent);

    // 이미지 인증 생성
    @Multipart
    @POST("record/image")
    Call<String> insertRecordImage(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // 오디오 인증 생성
    @Multipart
    @POST("record/audio")
    Call<String> insertRecordAudio(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // 비디오 인증 생성
    @Multipart
    @POST("record/video")
    Call<String> insertRecordVideo(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // STT 인증 생성
    @Multipart
    @POST("record/stt")
    Call<String> insertSTT(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // TTS 인증 생성
    @POST("record/tts")
    Call<String> insertTTS(@Body RecordCreateTTSDto recordCreateTTSDto);
}
