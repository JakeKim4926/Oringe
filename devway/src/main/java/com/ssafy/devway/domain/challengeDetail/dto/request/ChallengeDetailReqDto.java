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

    private Integer challengeDetailGif;

    private Integer challengeDetailAudio;

    private Integer challengeDetailVideo;

    private Integer challengeDetailTTS;

    private Integer challengeDetailSTT;
}
