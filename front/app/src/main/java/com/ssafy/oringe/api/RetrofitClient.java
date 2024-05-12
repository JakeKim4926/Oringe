package com.ssafy.oringe.api;


import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.api.record.RecordService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    private static final String BASE_URL = "https://k10b201.p.ssafy.io/";
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
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

//public class RetrofitClient {
//    private static final String BASE_URL = "https://k10b201.p.ssafy.io/";
//
//    public static ChallengeDetailService getApiChallengeDetailService() {
//        return new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(ChallengeDetailService.class);
//    }
//}