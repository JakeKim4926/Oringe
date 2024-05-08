package com.ssafy.devway.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class BookRequestDTO {

    private String query;
    private String sort;
    private int page;
    private int size;
    private String target;
}
