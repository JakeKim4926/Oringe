package com.ssafy.devway.domain.challenge.service;

import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.dto.request.ChallengeCreateReqDto;
import com.ssafy.devway.domain.challenge.dto.response.ChallengeDetailResDto;
import com.ssafy.devway.domain.challenge.dto.response.TodayChallengeListResDto;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.record.document.Record;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeDetailRepository challengeDetailRepository;
    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;

    /*
     * 2.1 챌린지 생성
     * */
    public void insertChallenge(ChallengeCreateReqDto dto) {
        Optional<ChallengeDetail> optChallengeDetail = challengeDetailRepository.findById(
            dto.getChallengeDetailId());
        if (optChallengeDetail.isEmpty()) {

        }
        Challenge challenge = Challenge.builder()
            .challengeTitle(dto.getChallengeTitle())
            .challengeMemo(dto.getChallengeMemo())
            .challengeCycle(dto.getChallengeCycle())
            .challengeAlarm(dto.getChallengeAlarm())
            .challengeAlarmTime(dto.getChallengeAlarmTime())
            .challengeStart(dto.getChallengeStart())
            .challengeEnd(dto.getChallengeEnd())
            .challengeStatus(dto.getChallengeStatus())
            .challengeDetail(optChallengeDetail.get())
            .challengeAppName(dto.getChallengeAppName())
            .challengeAppTime(dto.getChallengeAppTime())
            .challengeCallName(dto.getChallengeCallName())
            .challengeCallNumber(dto.getChallengeCallNumber())
            .challengeWakeupTime(dto.getChallengeWakeupTime())
            .challengeWalk(dto.getChallengeWalk())
            .build();

        challengeRepository.save(challenge);
    }

    /*
     * 2.2 챌린지 전체 조회
     * */
    public List<Challenge> selectChallengeList(Long memberId) {
        List<Challenge> challengeList = challengeRepository.findByMemberId(memberId);
        return challengeList;
    }

    /*
     * 2.3 오늘 챌린지 목록 조회
     * */
    public List<TodayChallengeListResDto> selectTodayChallengeList(Long memberId) {
        List<TodayChallengeListResDto> todayChallengeList = challengeRepository.findTodayListByMemberId(memberId);
        return todayChallengeList;
    }

    /*
     * 2.4 특정 챌린지 인증 전체 조회
     * */
    public List<ChallengeDetailResDto> selectChallengeDetail(Long challengeId) {
        List<Record> recordList = recordRepository.findByChallengeId(challengeId);

        List<ChallengeDetailResDto> list = new ArrayList<>();
        for (Record record : recordList) {
            ChallengeDetailResDto dto = ChallengeDetailResDto.builder()
                .recordId(record.getRecordId())
                .recordSuccess(record.getRecordSuccess())
                .recordDate(record.getRecordDate())
                .build();
            list.add(dto);
        }
        return list;
    }


    /*
     * 2.5 특정 챌린지 삭제
     * */
    public void deleteChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }


    /*
     * 2.6 특정 챌린지 진행상태 갱신
     * */
    public void updateChallengeStatus(Long challengeId) {
        Challenge challenge = challengeRepository.findByChallengeId(challengeId);
        LocalDate today = LocalDate.now();
        if (today.isAfter(challenge.getChallengeEnd())) {
            challenge.setChallengeStatus(3);
        }
    }


}
