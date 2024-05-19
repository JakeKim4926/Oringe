package com.ssafy.devway.domain.record.service;

import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_AUDIO;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_CONTENT;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_IMAGE;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_STT;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_TITLE;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_TTS;
import static com.ssafy.devway.domain.challengeDetail.ChallengeDetailOrders.CHALLENGE_DETAIL_VIDEO;

import com.ssafy.devway.STT.STTBlock;
import com.ssafy.devway.TTS.TTSBlock;
import com.ssafy.devway.TTS.TTSCountry;
import com.ssafy.devway.domain.challenge.document.Challenge;
import com.ssafy.devway.domain.challenge.repository.ChallengeRepository;
import com.ssafy.devway.domain.challengeDetail.repository.ChallengeDetailRepository;
import com.ssafy.devway.domain.challengeDetail.service.ChallengeDetailService;
import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import com.ssafy.devway.domain.record.document.Record;
import com.ssafy.devway.domain.record.dto.request.RecordCreateReqDto;
import com.ssafy.devway.domain.record.dto.request.RecordCreateTTSDto;
import com.ssafy.devway.domain.record.dto.response.CalendarRecordResDto;
import com.ssafy.devway.domain.record.dto.response.RecordResDto;
import com.ssafy.devway.domain.record.dto.response.RecordTemplateDto;
import com.ssafy.devway.domain.record.repository.RecordRespository;
import com.ssafy.devway.global.config.autoIncrementSequence.service.AutoIncrementSequenceService;
import com.ssafy.devway.text.TextBlock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.w3c.dom.Text;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final ChallengeDetailService challengeDetailService;
    private final RecordRespository recordRespository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final AutoIncrementSequenceService autoIncrementSequenceService;
    private final ChallengeDetailRepository challengeDetailRepository;
//    private final String API_PATH = "http://localhost:8050/";
    private final String API_PATH = "https://k10b201.p.ssafy.io/oringe_static/";
    public ResponseEntity<?> insertRecord(RecordCreateReqDto dto) {
        Member member = memberRepository.findByMemberId(dto.getMemberId());
        Challenge challenge = challengeRepository.findByChallengeId(dto.getChallengeId());

        LocalDate today = LocalDate.now();

        List<Integer> templatesOrder = challengeDetailService.getTemplatesOrder(
                challenge.getChallengeDetail().getChallengeDetailId());

        List<String> template = new ArrayList<>();
        dto.setRecordTemplates(template);
        for (int order = 0; order < templatesOrder.size(); order++) {
            Boolean bConfirm = confirmTemplates(templatesOrder.get(order),
                    dto);
            if (!bConfirm) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.valueOf(order) + " 리스트 인덱스에 문제가 있어요");
            }
        }

        Record record = Record.builder()
                .recordId(autoIncrementSequenceService.generateSequence(Record.SEQUENCE_NAME))
                .challenge(challenge)
                .member(member)
                .recordDate(today)
                .recordSuccess(true)
                .recordTemplates(dto.getRecordTemplates())
                .build();

        Record save = recordRespository.save(record);

        if (save == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("Success");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarRecordResDto>> selectCalendarRecord(
            Long memberId,
            Long challengeId,
            int month) {
        List<Record> recordList = recordRespository.findByChallenge_ChallengeIdAndMember_MemberIdOrderByRecordDateAsc(
                challengeId, memberId);

        if (recordList.isEmpty()) {
            ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<CalendarRecordResDto> collect = recordList.stream()
                .filter(record -> record.getRecordDate().getMonthValue() == month)
                .map(this::convertToCalendarRecordResDto)
                .toList();

        return ResponseEntity.ok(collect);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> selectRecord(Long recordId) {
        Record byRecordId = recordRespository.findByRecordId(recordId);
        if (byRecordId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("해당 recordId의 document 는 존재하지 않습니다.");
        }

        RecordResDto recordResDto = RecordResDto.builder()
                .memberId(byRecordId.getMember().getMemberId())
                .challengeId(byRecordId.getChallenge().getChallengeId())
                .recordDate(byRecordId.getRecordDate())
                .recordSuccess(byRecordId.getRecordSuccess())
                .recordTemplates(byRecordId.getRecordTemplates())
                .build();

        return ResponseEntity.ok(recordResDto);
    }

    public ResponseEntity<?> getSuccess(Long recordId) {
        Record byRecordId = recordRespository.findByRecordId(recordId);
        if (byRecordId == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(byRecordId.getRecordSuccess());
    }

    public ResponseEntity<?> setSuccess(Long recordId) {
        Record byRecordId = recordRespository.findByRecordId(recordId);
        if (byRecordId == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        byRecordId.setRecordSuccess(true);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getTemplates(Long recordId) {
        Record byRecordId = recordRespository.findByRecordId(recordId);
        if (byRecordId == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(byRecordId.getRecordTemplates());
    }

    public ResponseEntity<?> insertRecordText(String title, int limitLength) {
        TextBlock textBlock = new TextBlock(title);

        String blank = title.replaceAll("\\s+", "");
        blank = blank.replaceAll("\"", "");          // Remove double quotation marks

        if (textBlock.getContent().length() >= limitLength) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("텍스트는 " + String.valueOf(limitLength) + "자 이상 입력이 불가능 합니다.");
        }
        if (blank.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("공백 문자만 입력이 되었습니다.");
        }

        return ResponseEntity.ok(textBlock.getContent());
    }

    public ResponseEntity<?> insertRecordFile(MultipartFile file, String challengeTemplate, Long memberId) {
        String filename = file.getOriginalFilename();
        if(filename == null || filename.isEmpty() || file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 이름이 존재하지 않습니다.");
        if(challengeTemplate.equals("IMAGE") && !checkFileCorrect(challengeTemplate, filename.toLowerCase()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("jpg,png,gif,bmp 파일만 허용 가능합니다.");
        else if(challengeTemplate.equals("AUDIO") && !checkFileCorrect(challengeTemplate, filename.toLowerCase()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("mp3,wav,aac,ogg,flac 파일만 허용 가능합니다.");
        else if(challengeTemplate.equals("VIDEO") && !checkFileCorrect(challengeTemplate, filename.toLowerCase()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("mp4,avi,mpv,wmv,flv 파일만 허용 가능합니다.");

//        String absPath =  "src/main/resources/static/";
        String absPath =  "/app/static/";

        String timestamp = LocalDateTime.now().toString().substring(0, 19)
                .replace(":", "-");
        String manualPath = challengeTemplate + "/" + String.valueOf(
                memberId) + "/" + timestamp + "/";

        String directoryPath = absPath + manualPath;

        String fullPath = directoryPath + filename;
        try {
            // Save path
            Path fileCreate = Paths.get(directoryPath);
            Files.createDirectories(fileCreate);

            // Save the file to a location
            Files.copy(file.getInputStream(), Paths.get(fullPath),
                    StandardCopyOption.REPLACE_EXISTING);

            String resultPath = API_PATH + manualPath + filename;
            return ResponseEntity.ok(resultPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
    }

    private boolean checkFileCorrect(String challengeTemplate, String fileName) {
        if (challengeTemplate.equals("IMAGE")) {
            if(fileName.endsWith(".jpeg"))      return true;
            if(fileName.endsWith(".jpg"))       return true;
            if(fileName.endsWith(".gif"))       return true;
            if(fileName.endsWith(".bmp"))       return true;
        } else if(challengeTemplate.equals("AUDIO")) {
            if(fileName.endsWith(".mp3"))       return true;
            if(fileName.endsWith(".wav"))       return true;
            if(fileName.endsWith(".aac"))       return true;
            if(fileName.endsWith(".ogg"))       return true;
            if(fileName.endsWith(".flac"))      return true;
        } else if(challengeTemplate.equals("VIDEO")) {
            if(fileName.endsWith(".mp4"))       return true;
            if(fileName.endsWith(".avi"))       return true;
            if(fileName.endsWith(".mov"))       return true;
            if(fileName.endsWith(".wmv"))       return true;
            if(fileName.endsWith(".flv"))       return true;
        }

        return false;
    }

    public ResponseEntity<?> insertSTT(MultipartFile file, Long memberId) {
        try {
            if(!file.getOriginalFilename().endsWith(".wav"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wav 파일만 허용 가능합니다.");

            // Temporarily save the file to process
            Path tempDir = Files.createTempDirectory("stt_temp_" + String.valueOf(memberId));
            Path tempFile = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(tempFile);

            STTBlock sttBlock = new STTBlock();
            sttBlock.transcribe(tempFile.toString());
            StringBuilder stringBuilder = new StringBuilder();

            for(String str : sttBlock.getSpeechToTexts()) {
                stringBuilder.append(str);
            }

            String result = stringBuilder.toString();

            Files.delete(tempFile);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not save the stt: " + file.getOriginalFilename() + "!");
        }
    }

    public ResponseEntity<?> insertTTS(RecordCreateTTSDto recordCreateTTSDto) {
        try {
            String timestamp = LocalDateTime.now().toString().substring(0, 19)
                    .replace(":", "-");
//            String absPath = "src/main/resources/static";
            String absPath =  "/app/static/";
            String directoryPath = "/TTS/" + String.valueOf(recordCreateTTSDto.getMemberId())
                    + "/" + timestamp + "/";

            String path = absPath + directoryPath ;

            // Save path
            Path fileCreate = Paths.get(path);
            Files.createDirectories(fileCreate);

            TTSBlock ttsBlock = new TTSBlock(path); // 음성 파일 저장 위치
            ttsBlock.synthesizeText(recordCreateTTSDto.getRecordTTS(), TTSCountry.KR_A_FEMALE); // 텍스트 및 음성 나라 설정

            String resultPath = API_PATH + directoryPath + "output.mp3";
            return ResponseEntity.ok(resultPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not save the tts : " + e.toString());
        }
    }

    private CalendarRecordResDto convertToCalendarRecordResDto(Record record) {
        return new CalendarRecordResDto(
                record.getRecordId(),
                record.getRecordSuccess(),
                record.getRecordDate()
        );
    }


    public ResponseEntity<Integer> getSuccessToday(Long memberId, Long challengeId) {
        List<Record> byChallengeChallengeIdAndMemberMemberIdAndRecordDate = recordRespository.findByChallenge_ChallengeIdAndMember_MemberIdAndRecordDate(
                challengeId, memberId, LocalDate.now());

        final int TRUE = 1;
        final int FALSE = 0;

        if(byChallengeChallengeIdAndMemberMemberIdAndRecordDate.isEmpty())
            return ResponseEntity.ok(FALSE);

        return ResponseEntity.ok(TRUE);
    }

    private Boolean confirmTemplates(Integer challengeDetailIndex,
            RecordCreateReqDto recordTemplateDto) {

        if (challengeDetailIndex == CHALLENGE_DETAIL_TITLE.getOrderCode()) {
            if (recordTemplateDto.getRecordTitle().length() >= 100
                    || recordTemplateDto.getRecordTitle().isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordTitle());

        } else if (challengeDetailIndex == CHALLENGE_DETAIL_CONTENT.getOrderCode()) {
            if (recordTemplateDto.getRecordContent().length() >= 1000
                    || recordTemplateDto.getRecordContent().isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordContent());

        } else if (challengeDetailIndex == CHALLENGE_DETAIL_IMAGE.getOrderCode()) {
            if (recordTemplateDto.getRecordImage() == null || recordTemplateDto.getRecordImage()
                    .isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordImage());

        } else if (challengeDetailIndex == CHALLENGE_DETAIL_AUDIO.getOrderCode()) {
            if (recordTemplateDto.getRecordAudio() == null || recordTemplateDto.getRecordAudio()
                    .isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordAudio());

        } else if (challengeDetailIndex == CHALLENGE_DETAIL_VIDEO.getOrderCode()) {
            if (recordTemplateDto.getRecordVideo() == null || recordTemplateDto.getRecordVideo()
                    .isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordVideo());

        } else if (challengeDetailIndex == CHALLENGE_DETAIL_STT.getOrderCode()) {
            if (recordTemplateDto.getRecordSTT() == null || recordTemplateDto.getRecordSTT()
                    .isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordSTT());

        } else if (challengeDetailIndex == CHALLENGE_DETAIL_TTS.getOrderCode()) {
            if (recordTemplateDto.getRecordTTS() == null || recordTemplateDto.getRecordTTS()
                    .isEmpty()) {
                return false;
            }

            recordTemplateDto.getRecordTemplates().add(recordTemplateDto.getRecordTTS());

        }

        return true;
    }

}
