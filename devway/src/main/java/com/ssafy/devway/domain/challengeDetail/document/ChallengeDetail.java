package com.ssafy.devway.domain.challengeDetail.document;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChallengeDetail {

    @Transient
    public static final String SEQUENCE_NAME = "challengeDetail_sequence";

    @Id
    private Long challengeDetailId;

    private Integer challengeDetailTitle;

    private Integer challengeDetailContent;

    private Integer challengeDetailImage;

    private Integer challengeDetailAudio;

    private Integer challengeDetailVideo;

    private Integer challengeDetailSTT;

    private Integer challengeDetailTTS;

}
