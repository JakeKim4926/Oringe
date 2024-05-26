package com.ssafy.oringewatch.presentation.api.record.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RecordCreateReqDto {

  private Long memberId;
  private Long challengeId;
  private String recordTitle;
  private String recordContent;
  private String recordImage;
  private String recordAudio;
  private String recordVideo;
  private String recordSTT;
  private String recordTTS;

  private List<String> recordTemplates;
}
