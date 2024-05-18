package com.ssafy.oringe.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.api.record.RecordService;

import java.time.LocalDate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
        private static final String BASE_URL = "https://k10b201.p.ssafy.io/oringe/";
    private static Retrofit retrofit = null;
    private static Gson customGson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(customGson))
                    .build();
        }
        return retrofit;
    }

    public static ChallengeDetailService getApiChallengeDetailService() {
        return getRetrofitInstance().create(ChallengeDetailService.class);
    }

    public static RecordService getApiRecordService() {
        return getRetrofitInstance().create(RecordService.class);
    }
}