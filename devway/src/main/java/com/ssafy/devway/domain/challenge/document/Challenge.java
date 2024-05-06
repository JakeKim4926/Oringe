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
    private Long challengeId;

    private String challengeTitle;

    private String challengeStart;

    private String challengeEnd;

    private List<Integer> challengeCycle;

    private Boolean challengeAlarm;

    private String challengeAlarmTime;

    private String challengeMemo;

    private Integer challengeStatus;

    @DBRef
    private Member member;

    @DBRef
    private ChallengeDetail challengeDetail;

}
