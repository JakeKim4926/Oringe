package com.ssafy.devway.domain.record.document;
import com.ssafy.devway.domain.member.document.Member;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    @Transient
    public static final String SEQUENCE_NAME = "record_sequence";

    @Id
    private Long recordId;

//    private Challenge challenge;

    private Member member;

    private String recordDate;

    private Boolean recordSuccess;

    private List<String> recordTemplates;
}
