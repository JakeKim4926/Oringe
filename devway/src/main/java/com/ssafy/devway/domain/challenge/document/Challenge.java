package com.ssafy.devway.domain.challenge.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.member.document.Member;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Challenge {

    @Transient
    public static final String SEQUENCE_NAME = "challenge_sequence";

    @Id
    @NotNull
    private Long challengeId;

    @NotNull
    private String challengeTitle;

    @NotNull
    private LocalDate challengeStart;

    @NotNull
    private LocalDate challengeEnd;

    @NotNull
    private List<Integer> challengeCycle;

    @NotNull
    private Boolean challengeAlarm;

    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime challengeAlarmTime;

    private String challengeMemo;

    @NotNull
    private Integer challengeStatus;

    private String challengeAppName;

    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime challengeAppTime;

    private String challengeCallName;

    private String challengeCallNumber;

    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime challengeWakeupTime;

    private Integer challengeWalk;

    @DBRef
    @NotNull
    private Member member;

    @DBRef
    @NotNull
    private ChallengeDetail challengeDetail;

}
