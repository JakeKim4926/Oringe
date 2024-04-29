package com.ssafy.devway.domain.element;

import org.springframework.stereotype.Component;

@Component
public class Image implements Element {
    private String path;
    private String url;

    public Image(String path, String url) {
        this.path = path;
        this.url = url;
    }

    @Override
    public void display() {
        System.out.println("Image at: " + url);
    }
}