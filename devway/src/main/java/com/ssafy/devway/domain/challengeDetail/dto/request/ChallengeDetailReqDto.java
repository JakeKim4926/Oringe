package com.ssafy.devway.domain.challengeDetail.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChallengeDetailReqDto {

    private Integer challengeDetailTitle;

    private Integer challengeDetailContent;

    private Integer challengeDetailImage;

    private Integer challengeDetailImageContent;

    private Integer challengeDetailVideo;

    private Integer challengeDetailAppName;

    private Integer challengeDetailAppTime;

    private Integer challengeDetailCallName;

    private Integer challengeDetailCallNumber;

    private Integer challengeDetailWakeupTime;

    private Integer challengeDetailWalk;

    private Integer challengeDetailSTT;

    private Integer challengeDetailTTS;
}
