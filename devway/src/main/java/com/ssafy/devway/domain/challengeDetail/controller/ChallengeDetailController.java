package com.ssafy.devway.domain.challengeDetail.controller;

import com.ssafy.devway.domain.challengeDetail.dto.request.ChallengeDetailReqDto;
import com.ssafy.devway.domain.challengeDetail.service.ChallengeDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oringe/api/challengeDetail")
@Tag(name = "템플릿", description = "ChallengeDetail API")
public class ChallengeDetailController {

    private final ChallengeDetailService challengeDetailService;

    @PostMapping
    @Operation(summary = "템플릿 순서 생성")
    public ResponseEntity<Long> postChallengeDetail(
        @RequestBody ChallengeDetailReqDto dto) {
        return ResponseEntity.ok(challengeDetailService.insertChallengeDetail(dto));
    }

}
