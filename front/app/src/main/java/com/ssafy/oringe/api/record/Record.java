package com.ssafy.oringe.api.record;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Record {

    private Long recordId;

    private LocalDate recordDate;

    private Boolean recordSuccess;

    private List<String> recordTemplates;
}