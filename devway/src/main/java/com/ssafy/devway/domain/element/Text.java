package com.ssafy.devway.domain.element;

import org.springframework.stereotype.Component;

@Component
public class Text implements Element{
    private String content;
    private int length;

    public Text(String content) {
        this.content = content;
        this.length = content.length();
    }

    @Override
    public void display() {
        System.out.println("Text: " + content);
    }
}