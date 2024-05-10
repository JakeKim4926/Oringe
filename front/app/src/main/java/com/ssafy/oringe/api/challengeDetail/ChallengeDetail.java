package com.ssafy.oringe.api.challengeDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDetail {

    private Long challengeDetailId;

    private Integer challengeDetailTitle;

    private Integer challengeDetailContent;

    private Integer challengeDetailImage;

    private Integer challengeDetailAudio;

    private Integer challengeDetailVideo;

    private Integer challengeDetailSTT;

    private Integer challengeDetailTTS;

}