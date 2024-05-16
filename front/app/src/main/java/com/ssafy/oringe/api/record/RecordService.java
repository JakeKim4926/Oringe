package com.ssafy.oringe.api.record;

import com.ssafy.oringe.api.record.dto.RecordResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecordService {
    @GET("api/record/challenge")
    Call<List<Record>> fetchMonthlyRecords(@Query("memberId") Long memberId, @Query("challengeId") Long challengeId, @Query("month") int month);
    @GET("/oringe/api/challenge/cycle")
    Call<List<Integer>> fetchCycleDays(@Query("challengeId") Long challengeId);
    @GET("oringe/api/record")
    Call<RecordResponse> getRecord(@Query("recordId") Long recordId);
}

