package com.ssafy.devway.TTS;

import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.ssafy.devway.block.element.BlockElement;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.OutputStream;
import lombok.Data;


@Data
public class TTSBlock implements BlockElement {

    private String filePath = "";
    private String model = "en-US-Standard-C";

    public TTSBlock(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getName() {
        return "TTS";
    }

    public void synthesizeText(String text, TTSCountry country) throws Exception {
        model = country.getTextMode();

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(model.substring(0, 5))
                .setName(model)
                .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                audioConfig);
            ByteString audioContents = response.getAudioContent();
            try (OutputStream out = new FileOutputStream(filePath + "/output.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
            }
        }

    }
}