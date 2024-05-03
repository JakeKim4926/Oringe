package com.ssafy.devway.domain.record.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RecordCreateReqDto {

  private Long memberId;
  private Long challengeId;

  private List<String> recordTemplates;

}
