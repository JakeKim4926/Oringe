package com.ssafy.devway.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookSortMode {
    BOOK_SORT_BY_ACCURACY("accuracy"),
    BOOK_SORT_BY_LATEST("latest");

    public final String sortMode;
}
