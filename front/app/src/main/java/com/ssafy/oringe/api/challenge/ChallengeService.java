package com.ssafy.oringe.api.challenge;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChallengeService {
        @POST("challenge")
        Call<ResponseBody> sendData(@Body Challenge data, @Query("memberId") Long memberId);

        @GET("challenge/{memberId}")
        Call<List<Challenge>> getData(@Path("memberId") Long id, @Query("status") int status);

        @GET("challenge/today")
        Call<List<Challenge>> getTodayChallengeList(@Query("memberId") Long memberId);

        @DELETE("challenge/{challengeId}")
        Call<Void> deleteChallenge(@Path("challengeId") Long challengeId);

}
