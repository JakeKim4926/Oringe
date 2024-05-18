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

    private int recordId;

    private Boolean recordSuccess;

    private LocalDate recordDate;

//    private List<String> recordTemplates;
}