package com.ssafy.devway.image;

import com.ssafy.devway.block.element.BlockElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageBlock implements BlockElement {

    private String imagePath;  // 이미지 파일의 로컬 경로
    private String imageUrl;   // 이미지 파일의 URL
    private byte[] content; // 바이트배열로 저장한 이미지파일.

    // constructor

    @Override
    public String getName() {
        return "IMAGE";
    }

    // 이미지 URL을 받아 로컬에 저장
    public void downloadImage(String imageUrl, String destinationPath) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, Paths.get(destinationPath));
            System.out.println("이미지 다운로드 완료: " + destinationPath);
        } catch (IOException e) {
            System.out.println("이미지 다운로드 실패: " + e.getMessage());
        }
    }

    // 로컬의 이미지 파일 경로를 URL로 변환 (예시로 로컬 경로를 URL 형식으로 가정)
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

    // 이미지 파일을 바이트 배열로 변환
    public byte[] imageToBytes(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("이미지를 바이트 배열로 변환 실패: " + e.getMessage());
            return null;
        }
    }

    // 바이트 배열을 이미지 파일로 저장
    public void saveBytesToImageFile(byte[] imageData, String destinationPath) {
        Path path = Paths.get(destinationPath);
        try (OutputStream out = Files.newOutputStream(path)) {
            out.write(imageData);
            System.out.println("이미지 파일 저장 완료: " + destinationPath);
        } catch (IOException e) {
            System.out.println("이미지 파일 저장 실패: " + e.getMessage());
        }
    }

}
