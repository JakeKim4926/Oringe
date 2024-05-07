package com.ssafy.devway.domain.record.controller;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.domain.record.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oringe/api")
@Tag(name = "인증", description = "Record API")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/record")
    @Operation(summary = "인증 생성")
    public ResponseEntity<Record> postRecord(@RequestBody RecordCreateReqDto dto, Long memberId) {
        return ResponseEntity.ok(recordService.insertRecord(dto, memberId));
    }

    @GetMapping("/record/challenge/{challengeId}")
    @Operation(summary = "특정 챌린지의 월 별 인증 전체 조회")
    public ResponseEntity<List<CalendarRecordResDto>> getCalendarRecord(Long challengeId,
        Long memberId, int month) {
        return ResponseEntity.ok(recordService.selectCalendarRecord(challengeId, memberId, month));
    }

    @GetMapping("/record/{recordId}")
    @Operation(summary = "인증 상세 조회")
    public ResponseEntity<Record> getRecord(@PathVariable Long recordId) {
        return ResponseEntity.ok(recordService.selectRecord(recordId));
    }

    @DeleteMapping("/record/{recordId}")
    @Operation(summary = "인증 삭제")
    public ResponseEntity<Long> deleteRecord(@PathVariable Long recordId) {
        return ResponseEntity.ok(recordService.deleteRecord(recordId));
    }
}
