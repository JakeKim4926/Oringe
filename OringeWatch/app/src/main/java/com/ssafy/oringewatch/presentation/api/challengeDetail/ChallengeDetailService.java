package com.ssafy.oringewatch.presentation.api.challengeDetail;


import com.ssafy.oringewatch.presentation.api.challengeDetail.dto.ChallengeDetailIdResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChallengeDetailService {
    @GET("/oringe/api/challengeDetail")
    Call<ChallengeDetailIdResponse> getTemplatesId(@Query("challengeId") Long challengeId);

    @GET("/oringe/api/challengeDetail/order")
    Call<List<Integer>> getTemplatesOrder(@Query("challengeDetailId") Long challengeDetailId);
}
