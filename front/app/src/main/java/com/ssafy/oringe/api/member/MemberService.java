package com.ssafy.oringe.api.member;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MemberService {

    @GET("signin")
    Call<Member> getMemberByEmail(@Query("memberEmail") String email);

}
