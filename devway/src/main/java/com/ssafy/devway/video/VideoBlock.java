package com.ssafy.devway.video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ssafy.devway.block.element.BlockElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoBlock implements BlockElement {

    private String videoPath;  // 비디오 파일의 로컬 경로
    private String videoUrl;   // 비디오 파일의 URL
    private byte[] content; // 바이트 배열로 저장한 비디오 파일

    @Override
    public String getName() {
        return "VIDEO";
    }

    // 비디오 URL을 받아 로컬에 저장
    public void downloadVideo(String videoUrl, String destinationPath) {
        try (InputStream in = new URL(videoUrl).openStream()) {
            Files.copy(in, Paths.get(destinationPath));
            System.out.println("비디오 다운로드 완료: " + destinationPath);
        } catch (IOException e) {
            System.out.println("비디오 다운로드 실패: " + e.getMessage());
        }
    }

    // 로컬의 비디오 파일 경로를 URL로 변환
    public String convertPathToUrl(String localPath) {
        try {
            Path path = Paths.get(localPath);
            URL url = path.toUri().toURL();
            return url.toString();
        } catch (IOException e) {
            System.out.println("파일 경로를 URL로 변환 실패: " + e.getMessage());
            return null;
        }
    }

    // 비디오 파일을 바이트 배열로 변환
    public byte[] videoToBytes(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("비디오를 바이트 배열로 변환 실패: " + e.getMessage());
            return null;
        }
    }

    // 바이트 배열을 비디오 파일로 저장
    public void saveBytesToVideoFile(byte[] videoData, String destinationPath) {
        Path path = Paths.get(destinationPath);
        try (OutputStream out = Files.newOutputStream(path)) {
            out.write(videoData);
            System.out.println("비디오 파일 저장 완료: " + destinationPath);
        } catch (IOException e) {
            System.out.println("비디오 파일 저장 실패: " + e.getMessage());
        }
    }
}
