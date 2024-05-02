package com.ssafy.devway.domain.challenge.service;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.dto.request.ChallengeCreateReqDto;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.member.document.Member;


import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeDetailRepository challengeDetailRepository;
    private final MemberRepository memberRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;
    private final MongoTemplate mongoTemplate;


    /*
     * 2.1 챌린지 생성
     * */
    public Challenge insertChallenge(ChallengeCreateReqDto dto, Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        log.debug("member: " + member);

        ChallengeDetail challengeDetail = ChallengeDetail.builder()
            .challengeDetailId(
                autoIncrementSequenceService.generateSequence(ChallengeDetail.SEQUENCE_NAME))
            .challengeDetailTitle(dto.getOrder().get(0))
            .challengeDetailContent(dto.getOrder().get(1))
            .challengeDetailImage(dto.getOrder().get(2))
            .challengeDetailImageContent(dto.getOrder().get(3))
            .challengeDetailVideo(dto.getOrder().get(4))
            .Digital(dto.getOrder().get(5))
            .Call(dto.getOrder().get(6))
            .WakeUp(dto.getOrder().get(7))
            .Walk(dto.getOrder().get(8))
            .build();
        log.debug("challengeDetail: " + challengeDetail);
        challengeDetailRepository.save(challengeDetail);

        LocalDate today = LocalDate.now();
        int status = -1;
        if (today.isBefore(dto.getChallengeStart())) {
            status = 1;
        } else if (today.isEqual(dto.getChallengeStart()) || (today.isAfter(dto.getChallengeStart())
            && today.isBefore(dto.getChallengeEnd())) || today.isEqual(dto.getChallengeEnd())) {
            status = 2;
        } else if (today.isAfter(dto.getChallengeEnd())) {
            status = 3;
        }

        LocalTime alarmTime = dto.getChallengeAlarmTime();
        alarmTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println(dto.getChallengeAlarmTime());
        Challenge challenge = Challenge.builder()
            .challengeId(autoIncrementSequenceService.generateSequence(Challenge.SEQUENCE_NAME))
            .challengeTitle(dto.getChallengeTitle())
            .challengeStart(dto.getChallengeStart())
            .challengeEnd(dto.getChallengeEnd())
            .challengeCycle(dto.getChallengeCycle())
            .challengeAlarm(dto.getChallengeAlarm())
            .challengeAlarmTime(dto.getChallengeAlarmTime())
            .challengeStatus(status)
            .challengeMemo(dto.getChallengeMemo())
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
    @Transactional(readOnly = true)
    public List<Challenge> selectChallengeList(Long memberId, int status) {
        List<Challenge> challengeList = challengeRepository.findByMember_MemberIdOrderByChallengeIdDesc(
            memberId);
        List<Challenge> statusList = new ArrayList<>();
        for(Challenge challenge:challengeList) {
            if (challenge.getChallengeStatus()==status) {
                statusList.add(challenge);
            }
        }
        return  statusList;
    }

    /*
     * 2.3 오늘 챌린지 목록 조회
     * */
    @Transactional(readOnly = true)
    public List<Challenge> selectTodayChallengeList(Long memberId) {
        int day = LocalDate.now().getDayOfWeek().getValue(); // 오늘 요일
        List<Challenge> challengeList = challengeRepository.findByMember_MemberIdOrderByChallengeIdDesc(
            memberId);
        log.debug("challengeList: " + challengeList);

        List<Challenge> todayList = new ArrayList<>();
        List<Integer> cycle;
        for (Challenge challenge : challengeList) {
            if (challenge.getChallengeStatus() != 2) {
                continue;
            }
            cycle = challenge.getChallengeCycle();
            if (cycle.contains(day)) {
                todayList.add(challenge);
            }
        }

        return todayList;
    }


    /*
     * 2.4 특정 챌린지 삭제
     * */
    public void deleteChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }


    /*
     * 2.5 매일 챌린지 진행상태 스케줄링
     * */
    @Scheduled(cron = "0 0 0 */1 * *")
    public void updateChallengeStatus() {
        List<Challenge> challengeList = challengeRepository.findAll();
        log.debug("challengeList: " + challengeList);

        LocalDate today = LocalDate.now();
        int newStatus = -1;
        for (Challenge challenge : challengeList) {
            if (today.isEqual(challenge.getChallengeStart()) || (
                today.isAfter(challenge.getChallengeStart()) && today.isBefore(
                    challenge.getChallengeEnd())) || today.isEqual(challenge.getChallengeEnd())) {
                newStatus = 2;
            } else if (today.isAfter(challenge.getChallengeEnd())) {
                newStatus = 3;
            }
            // status 변경하는 쿼리
            Query query = new Query(Criteria.where("_id").is(challenge.getChallengeId()));
            Update update = new Update().set("challengeStatus", newStatus);
            mongoTemplate.updateFirst(query, update, Challenge.class);
        }
    }


}
