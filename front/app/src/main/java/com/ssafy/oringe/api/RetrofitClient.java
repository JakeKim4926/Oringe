package com.ssafy.oringe.api;


import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://k10b201.p.ssafy.io/";

    public static ChallengeDetailService getApiChallengeDetailService() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChallengeDetailService.class);
    }
}