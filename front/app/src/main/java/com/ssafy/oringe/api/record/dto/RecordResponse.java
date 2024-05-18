package com.ssafy.oringe.api.record.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecordResponse {
    @SerializedName("memberId")
    public Long memberId;

    @SerializedName("challengeId")
    public Long challengeId;

    @SerializedName("recordDate")
    public String recordDate;

    @SerializedName("recordSuccess")
    public boolean recordSuccess;

    @SerializedName("recordTemplates")
    public List<String> recordTemplates;
}

