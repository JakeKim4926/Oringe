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
    @POST("/")
    Call<Void> postRecord(@Body RecordCreateReqDto recordCreateReqDto);

    // 제목 인증 생성
    @POST("/title")
    Call<Void> insertRecordTitle(@Query("recordTitle") String recordTitle);

    // 본문 인증 생성
    @POST("/content")
    Call<Void> insertRecordContent(@Query("recordContent") String recordContent);

    // 이미지 인증 생성
    @Multipart
    @POST("/image")
    Call<Void> insertRecordImage(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // 오디오 인증 생성
    @Multipart
    @POST("/audio")
    Call<Void> insertRecordAudio(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // 비디오 인증 생성
    @Multipart
    @POST("/video")
    Call<Void> insertRecordVideo(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // STT 인증 생성
    @Multipart
    @POST("/stt")
    Call<Void> insertSTT(
            @Part MultipartBody.Part file,
            @Query("memberId") Long memberId
    );

    // TTS 인증 생성
    @POST("/tts")
    Call<Void> insertTTS(@Body RecordCreateTTSDto recordCreateTTSDto);
}
