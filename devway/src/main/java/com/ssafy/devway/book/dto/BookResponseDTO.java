package com.ssafy.devway.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class BookResponseDTO {

    private String title;
    private String isbn;
    private String datetime;
    private String[] authors;
    private String publisher;
    private int price;
    private String thumnail;

}
