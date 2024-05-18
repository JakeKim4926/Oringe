package com.ssafy.oringe.api.challengeDetail.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChallengeDetailIdResponse {
    @SerializedName("headers")
    private Object headers;

    @SerializedName("body")
    private Long body;

    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("statusCodeValue")
    private int statusCodeValue;

}
