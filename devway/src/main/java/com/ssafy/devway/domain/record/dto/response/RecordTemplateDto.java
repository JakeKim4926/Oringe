package com.ssafy.devway.domain.record.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordTemplateDto {
  private String recordTitle;
  private String recordContent;
  private String recordImage;
  private String recordAudio;
  private String recordVideo;
  private String recordSTT;
  private String recordTTS;
}
