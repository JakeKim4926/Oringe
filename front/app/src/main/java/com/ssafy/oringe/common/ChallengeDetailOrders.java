package com.ssafy.oringe.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeDetailOrders {
    CHALLENGE_DETAIL_TITLE(1),
    CHALLENGE_DETAIL_CONTENT(2),
    CHALLENGE_DETAIL_IMAGE(3),
    CHALLENGE_DETAIL_AUDIO(4),
    CHALLENGE_DETAIL_VIDEO(5),
    CHALLENGE_DETAIL_STT(6),
    CHALLENGE_DETAIL_TTS(7),
    ;
    private final int orderCode;
}