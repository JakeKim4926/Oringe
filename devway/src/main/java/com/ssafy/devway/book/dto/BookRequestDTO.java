package com.ssafy.devway.book.dto;

import com.ssafy.devway.book.BookSortMode;
import com.ssafy.devway.book.BookTargetMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    private String query;
    private String sort;
    private int page;
    private int size;
    private String target;


}
