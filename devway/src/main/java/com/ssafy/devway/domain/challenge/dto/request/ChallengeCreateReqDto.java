package com.ssafy.devway.domain.challenge.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "HH:mm")
    private LocalTime challengeAlarmTime;

    private String challengeMemo;

    private String challengeAppName;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime challengeAppTime;

    private String challengeCallName;

    private String challengeCallNumber;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime challengeWakeupTime;

    private Integer challengeWalk;

    private List<Integer> order;

}
