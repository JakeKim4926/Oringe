package com.ssafy.oringe.api.record;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecordService {
    @GET("/api/records/monthly")
    Call<List<Record>> fetchMonthlyRecords(@Query("memberId") Long memberId, @Query("challengeId") Long challengeId, @Query("month") int month);
}

