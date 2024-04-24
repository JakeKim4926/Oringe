package com.ssafy.devway.domain.challenge.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChallengeCreateReqDto {

    private String challengeTitle;

    private LocalDate challengeStart;

    private LocalDate challengeEnd;

    private List<Integer> challengeCycle;

    private Boolean challengeAlarm;

    private LocalDateTime challengeAlarmTime;

    private String challengeMemo;

    private Integer challengeStatus;

    private String challengeAppName;

    private LocalDateTime challengeAppTime;

    private String challengeCallName;

    private String challengeCallNumber;

    private LocalDateTime challengeWakeupTime;

    private Integer challengeWalk;

    private Long challengeDetailId;

}
