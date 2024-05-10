package com.ssafy.devway.domain.challengeDetail.service;

import com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders;
import com.ssafy.devway.domain.challengeDetail.document.ChallengeDetail;
import com.ssafy.devway.domain.challengeDetail.dto.request.ChallengeDetailReqDto;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import java.lang.reflect.Field;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeDetailService {

    private final ChallengeDetailRepository challengeDetailRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;
    private final int START_TEMPLATES_NUM = 1;
    private final int END_TEMPLATES_NUM = 5;

    /*
     * 3.1 챌린지 순서 생성
     * */
    public Long insertChallengeDetail(ChallengeDetailReqDto dto) {
        ChallengeDetail challengeDetail = ChallengeDetail.builder()
            .challengeDetailId(
                autoIncrementSequenceService.generateSequence(ChallengeDetail.SEQUENCE_NAME))
            .challengeDetailTitle(dto.getChallengeDetailTitle())
            .challengeDetailContent(dto.getChallengeDetailContent())
            .challengeDetailImage(dto.getChallengeDetailImage())
            .challengeDetailAudio(dto.getChallengeDetailAudio())
            .challengeDetailVideo(dto.getChallengeDetailVideo())
            .challengeDetailTTS(dto.getChallengeDetailTTS())
            .challengeDetailSTT(dto.getChallengeDetailSTT())
            .build();

        challengeDetailRepository.save(challengeDetail);

        return challengeDetail.getChallengeDetailId();
    }

    public List<Integer> getTemplatesOrder(Long challengeDetailId) {
        ChallengeDetail byChallengeDetailId = challengeDetailRepository.findByChallengeDetailId(
            challengeDetailId);

        if(byChallengeDetailId == null)
            return null;

        // gets order
        List<Integer> orderList = setTemplatesOrder(byChallengeDetailId);

        return orderList;
    }

    private List<Integer> setTemplatesOrder(ChallengeDetail challengeDetail) {
        List<Integer> list = new ArrayList<>();
        int[] orders = new int[END_TEMPLATES_NUM + 1];

        int temp = challengeDetail.getChallengeDetailTitle();
        if(temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_TITLE.getOrderCode();

        temp = challengeDetail.getChallengeDetailContent();
        if(temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_CONTENT.getOrderCode();

        temp = challengeDetail.getChallengeDetailImage();
        if(temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_IMAGE.getOrderCode();

        temp = challengeDetail.getChallengeDetailAudio();
        if(temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_AUDIO.getOrderCode();

        temp = challengeDetail.getChallengeDetailVideo();
        if(temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_VIDEO.getOrderCode();

        temp = challengeDetail.getChallengeDetailSTT();
        if(temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_STT.getOrderCode();

        temp = challengeDetail.getChallengeDetailTTS();
        if (temp != 0)
            orders[temp] = ChallengeDetailOrders.CHALLENGE_DETAIL_TTS.getOrderCode();

        for(int i = START_TEMPLATES_NUM; i <= END_TEMPLATES_NUM; i++){
            if(orders[i] == 0)
                break;
            list.add(orders[i]);
        }

        return list;
    }

}
