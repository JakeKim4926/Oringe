package com.ssafy.devway.domain.challenge.service;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.dto.request.ChallengeCreateReqDto;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.member.document.Member;


import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.data.mongodb.uri}")
    private String springDataMongoDBUri;
    @Value("${spring.data.mongodb.database}")
    private String springDataMongoDBDatabase;

    private final ChallengeRepository challengeRepository;
    private final ChallengeDetailRepository challengeDetailRepository;
    private final MemberRepository memberRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @PostConstruct
    public void init() {
        mongoClient = MongoClients.create(springDataMongoDBUri);
        database = mongoClient.getDatabase(springDataMongoDBDatabase);
        collection = database.getCollection("challenge");
    }


    /*
     * 2.1 챌린지 생성
     * */
    public Challenge insertChallenge(ChallengeCreateReqDto dto, Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);

        ChallengeDetail challengeDetail = ChallengeDetail.builder()
            .challengeDetailId(
                autoIncrementSequenceService.generateSequence(ChallengeDetail.SEQUENCE_NAME))
            .challengeDetailTitle(dto.getOrder().get(0))
            .challengeDetailContent(dto.getOrder().get(1))
            .challengeDetailImage(dto.getOrder().get(2))
            .challengeDetailAudio(dto.getOrder().get(3))
            .challengeDetailVideo(dto.getOrder().get(4))
            .challengeDetailSTT(dto.getOrder().get(5))
            .challengeDetailTTS(dto.getOrder().get(6))
            .build();
        challengeDetailRepository.save(challengeDetail);

        LocalDate today = LocalDate.now();
        LocalDate start = LocalDate.parse(dto.getChallengeStart());
        LocalDate end = LocalDate.parse(dto.getChallengeEnd());

        int status = -1;
        if (today.isBefore(start)) {
            status = 1;
        } else if (today.isEqual(start) || today.isEqual(end)
            || (today.isAfter(start) && today.isBefore(end))) {
            status = 2;
        } else if (today.isAfter(end)) {
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
            .challengeStatus(status)
            .challengeMemo(dto.getChallengeMemo())
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
        for (Challenge challenge : challengeList) {
            if (challenge.getChallengeStatus() == status) {
                statusList.add(challenge);
            }
        }
        return statusList;
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
    @Scheduled(cron = "0 0 0 * * *")
    public void updateChallengeStatus() {
        LocalDate today = LocalDate.now();
        System.out.println("오눌: " + today);

        Bson query1 = eq("challengeStatus", 1);
        Bson filter1 = Filters.and(
            Filters.lte("challengeStart", today.toString()),
            Filters.gte("challengeEnd", today.toString())
        );
        Bson update1 = Updates.set("challengeStatus", 2);
        collection.updateMany(filter1, update1);

        Bson query2 = eq("challengeStatus", 2);
        Bson filter2 = Filters.lt("challengeEnd", today.toString());
        Bson update2 = Updates.set("challengeStatus", 3);
        collection.updateMany(filter2, update2);

    }


    /*
     * 2.6 챌린지의 cycle 조회
     * */
    public List<Integer> selectChallengeCycleList(Long challengeId){
        Document challengeCycleDoc = challengeRepository.findChallengeCycleDocumentByChallengeId(challengeId);
        if (challengeCycleDoc == null) {
            return new ArrayList<>();
        }
        return (List<Integer>) challengeCycleDoc.get("challengeCycle");
    }

}
