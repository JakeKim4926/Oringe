package com.ssafy.devway.domain.challengeDetail.controller;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChallengeDetailController {

    private final ChallengeDetailRepository challengeDetailRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;

    @PostMapping("/add/detail")
    public void insert() {
        ChallengeDetail challenge = ChallengeDetail.builder()
            .challengeDetailId(
                autoIncrementSequenceService.generateSequence(ChallengeDetail.SEQUENCE_NAME))
            .challengeDetailTitle(1)
            .build();
        challengeDetailRepository.save(challenge);
    }
}
