package com.ssafy.devway.book.dto.kakao;

import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Document {

    private String[] authors;
    private String contents;
    private String datetime;
    private String isbn;
    private int price;
    private String publisher;
    private int sale_price;
    private String status;
    private String thumbnail;
    private String title;
    private String[] translators;
    private String url;
}
