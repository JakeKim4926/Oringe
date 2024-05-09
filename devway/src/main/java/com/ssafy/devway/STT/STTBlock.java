package com.ssafy.devway.STT;

import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

import com.ssafy.devway.block.element.BlockElement;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import lombok.Getter;


@Getter
public class STTBlock implements BlockElement {

    private ArrayList<String> speechToTexts = new ArrayList<>();

    public void transcribe(String fileName) throws Exception {
        if (!isWavFile(fileName)) {
            throw new IllegalArgumentException(
                "Unsupported file format. Only .wav files are allowed.");
        }

        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);

        int sampleRate = getSampleRate(fileName); // 샘플링 주파수 읽는 메소드 호출

        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setLanguageCode("en-US")
                .setSampleRateHertz(sampleRate)
                .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(com.google.protobuf.ByteString.copyFrom(data))
                .build();

            List<SpeechRecognitionResult> results = speechClient.recognize(config, audio)
                .getResultsList();
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//        System.out.printf("Transcription: %s%n", alternative.getTranscript());
                speechToTexts.add(alternative.getTranscript());
            }
        }
    }

    private int getSampleRate(String fileName) throws Exception {
        File file = new File(fileName);
        AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
        AudioFormat format = fileFormat.getFormat();
        return (int) format.getSampleRate();
    }

    private boolean isWavFile(String fileName) {
        return fileName.toLowerCase().endsWith(".wav");
    }

    @Override
    public String getName() {
        return "STT";
    }
}
