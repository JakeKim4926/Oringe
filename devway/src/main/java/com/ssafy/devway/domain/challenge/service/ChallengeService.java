package com.ssafy.devway.domain.challenge.service;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.dto.request.ChallengeCreateReqDto;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.member.document.Member;


import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeDetailRepository challengeDetailRepository;
    private final MemberRepository memberRepository;
    //    private final RecordRepository recordRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;


    /*
     * 2.1 챌린지 생성
     * */
    public Challenge insertChallenge(ChallengeCreateReqDto dto,
        Long challengeDetailId, Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        log.debug("member: " + member);

        ChallengeDetail challengeDetail = challengeDetailRepository.findByChallengeDetailId(
            challengeDetailId);
        log.debug("challengeDetail: " + challengeDetail);

        LocalDate today = LocalDate.now();
        System.out.println(today + ", " + dto.getChallengeStart() + ", " + dto.getChallengeEnd());
        int status = -1;
        if (today.isBefore(dto.getChallengeStart())) {
            status = 1;
        } else if (today.isEqual(dto.getChallengeStart()) || (today.isAfter(dto.getChallengeStart())
            && today.isBefore(dto.getChallengeEnd())) || today.isEqual(dto.getChallengeEnd())) {
            status = 2;
        } else {
            status = 3;
        }

        Challenge challenge = Challenge.builder()
            .challengeId(autoIncrementSequenceService.generateSequence(Challenge.SEQUENCE_NAME))
            .challengeTitle(dto.getChallengeTitle())
            .challengeStart(dto.getChallengeStart())
            .challengeEnd(dto.getChallengeEnd())
            .challengeCycle(dto.getChallengeCycle())
            .challengeAlarm(dto.getChallengeAlarm())
            .challengeAlarmTime(dto.getChallengeAlarmTime())
            .challengeMemo(dto.getChallengeMemo())
            .challengeStatus(status)
            .challengeAppName(dto.getChallengeAppName())
            .challengeAppTime(dto.getChallengeAppTime())
            .challengeCallName(dto.getChallengeCallName())
            .challengeCallNumber(dto.getChallengeCallNumber())
            .challengeWakeupTime(dto.getChallengeWakeupTime())
            .challengeWalk(dto.getChallengeWalk())
            .challengeDetail(challengeDetail)
            .member(member)
            .build();

        challengeRepository.save(challenge);

        return challenge;
    }

    /*
     * 2.2 챌린지 전체 조회
     * */
    public List<Challenge> selectChallengeList(Long memberId) {
        List<Challenge> challengeList = challengeRepository.findByMember_MemberId(memberId);
        return challengeList;
    }

    /*
     * 2.3 오늘 챌린지 목록 조회
     * */
    public List<Challenge> selectTodayChallengeList(Long memberId) {
        int day = LocalDate.now().getDayOfWeek().getValue(); // 오늘 요일 구하기
        List<Challenge> challengeList = challengeRepository.findByMember_MemberId(memberId);
        List<Challenge> todayList = new ArrayList<>();
        for (Challenge challenge : challengeList) {
            List<Integer> cycle = challenge.getChallengeCycle();
            if (cycle.contains(day)) {
                todayList.add(challenge);
            }
        }
        return todayList;
    }

//    /*
//     * 2.4 특정 챌린지 인증 전체 조회
//     * */
//    public List<ChallengeDetailResDto> selectChallengeDetail(Long challengeId) {
//        List<Record> recordList = recordRepository.findByChallengeId(challengeId);
//
//        List<ChallengeDetailResDto> list = new ArrayList<>();
//        for (Record record : recordList) {
//            ChallengeDetailResDto dto = ChallengeDetailResDto.builder()
//                    .recordId(record.getRecordId())
//                    .recordSuccess(record.getRecordSuccess())
//                    .recordDate(record.getRecordDate())
//                    .build();
//            list.add(dto);
//        }
//        return list;
//    }


    /*
     * 2.5 특정 챌린지 삭제
     * */
    public Long deleteChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
        return challengeId;
    }


    /*
     * 2.6 특정 챌린지 진행상태 갱신
     * */
    @Scheduled(cron = "0 0 0 */1 * *")
    public int updateChallengeStatus(Long challengeId) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId);
        LocalDate today = LocalDate.now();
        if (today.isEqual(challenge.getChallengeStart()) || (
            today.isAfter(challenge.getChallengeStart())
                && today.isBefore(challenge.getChallengeEnd())) || today.isEqual(
            challenge.getChallengeEnd())) {
            challenge.setChallengeStatus(2);
        } else if (today.isAfter(challenge.getChallengeEnd())) {
            challenge.setChallengeStatus(3);
        }

        return challenge.getChallengeStatus();
    }


}
