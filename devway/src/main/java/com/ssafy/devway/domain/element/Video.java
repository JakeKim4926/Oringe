package com.ssafy.devway.domain.element;

import org.springframework.stereotype.Component;

@Component
public class Video implements Element {
    private String path;
    private byte[] data;

    public Video(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    @Override
    public void display() {
        System.out.println("Video: " + path);
    }
}