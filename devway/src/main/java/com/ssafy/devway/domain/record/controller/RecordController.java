package com.ssafy.devway.domain.record.controller;

import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.request.RecordCreateTTSDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.dto.response.RecordResDto;
import com.ssafy.devway.domain.record.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oringe/api/record")
@Tag(name = "인증", description = "Record API")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @Operation(summary = "인증 생성")
    public ResponseEntity<?> postRecord(@RequestBody RecordCreateReqDto recordCreateReqDto) {
        return recordService.insertRecord(recordCreateReqDto);
    }

    @GetMapping("/challenge")
    @Operation(summary = "특정 챌린지의 월 별 인증 전체 조회")
    public ResponseEntity<List<CalendarRecordResDto>> getCalendarRecord(
            @RequestParam Long memberId,
            @RequestParam Long challengeId,
            @RequestParam int month) {
        return recordService.selectCalendarRecord(memberId, challengeId, month);
    }

    @GetMapping
    @Operation(summary = "인증 상세 조회")
    public ResponseEntity<?> getRecord(@RequestParam Long recordId) {
        return recordService.selectRecord(recordId);
    }

    @GetMapping("/success")
    @Operation(summary = "인증 성공 여부 조회")
    public ResponseEntity<?> getSuccess(@RequestParam Long recordId) {
        return recordService.getSuccess(recordId);
    }

    @GetMapping("/success/today")
    @Operation(summary = "금일 인증 성공 여부 조회")
    public ResponseEntity<Integer> getTodaySuccess(@RequestParam Long memberId, @RequestParam Long challengeId) {
        return recordService.getSuccessToday(memberId, challengeId);
    }

    @PatchMapping("/success")
    @Operation(summary = "인증 성공 하기")
    public ResponseEntity<?> setSuccess(@RequestParam Long recordId) {
        return recordService.setSuccess(recordId);
    }

    @PostMapping("/title")
    @Operation(summary = "제목 인증 생성")
    public ResponseEntity<?> insertRecordTitle(
            @RequestParam String recordTitle
    ) {
        return recordService.insertRecordText(recordTitle, 100);
    }

    @PostMapping("/content")
    @Operation(summary = "본문 인증 생성")
    public ResponseEntity<?> insertRecordContent(
            @RequestParam String recordContent
    ) {
        return recordService.insertRecordText(recordContent, 1000);
    }

    @PostMapping("/image")
    @Operation(summary = "이미지 인증 생성")
    public ResponseEntity<?> insertRecordImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam Long memberId
    ) {
        return recordService.insertRecordFile(file, "IMAGE", memberId);
    }

    @PostMapping("/audio")
    @Operation(summary = "오디오 인증 생성")
    public ResponseEntity<?> insertRecordAudio(
            @RequestParam("audio") MultipartFile file,
            @RequestParam Long memberId
    ) {
        return recordService.insertRecordFile(file, "AUDIO", memberId);
    }

    @PostMapping("/video")
    @Operation(summary = "비디오 인증 생성")
    public ResponseEntity<?> insertRecordVideo(
            @RequestParam("video") MultipartFile file,
            @RequestParam Long memberId
    ) {
        return recordService.insertRecordFile(file, "VIDEO", memberId);
    }

    @PostMapping("/stt")
    @Operation(summary = "STT 인증 생성")
    public ResponseEntity<?> insertSTT(
            @RequestParam("stt") MultipartFile file,
            @RequestParam Long memberId
    ) {
        return recordService.insertSTT(file, memberId);
    }

    @PostMapping("/tts")
    @Operation(summary = "STT 인증 생성")
    public ResponseEntity<?> insertTTS(
            @RequestBody RecordCreateTTSDto recordCreateTTSDto
    ) {
        return recordService.insertTTS(recordCreateTTSDto);
    }


}
