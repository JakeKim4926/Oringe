package com.ssafy.devway.domain.challengeDetail.document;

import com.ssafy.devway.domain.challenge.document.Challenge;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDetail {

    @Transient
    public static final String SEQUENCE_NAME = "challengeDetail_sequence";

    @Id
    private Long challengeDetailId;

    private Integer challengeDetailTitle;

    private Integer challengeDetailContent;

    private Integer challengeDetailTImage;

    private Integer challengeDetailImageContent;

    private Integer challengeDetailVideo;

    private Integer challengeDetailAppName;

    private Integer challengeDetailAppTime;

    private Integer challengeDetailCallName;

    private Integer challengeDetailCallNumber;

    private Integer challengeDetailWakeupTime;

    private Integer challengeDetailWalk;


}
