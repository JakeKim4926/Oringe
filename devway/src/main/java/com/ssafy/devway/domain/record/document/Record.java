package com.ssafy.devway.domain.record.document;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.member.document.Member;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Record {

    @Transient
    public static final String SEQUENCE_NAME = "record_sequence";

    @Id
    @NotNull
    private Long recordId;

    @DBRef
    @NotNull
    private Challenge challenge;

    @DBRef
    @NotNull
    private Member member;

    @NotNull
    private LocalDate recordDate;

    @NotNull
    @Setter
    private Boolean recordSuccess;

    @NotNull
    private List<String> recordTemplates;
}
