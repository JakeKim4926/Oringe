package com.ssafy.devway.weather;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeatherCountry {
    NORMAL(""),
    SEOUL("Seoul"),
    INCHEON("Incheon"),
    BUSAN("Busan"),
    GWANGJU("Gwangju"),
    DAEJEON("Daejeon"),
    DAEGU("Daegu"),
    ULSAN("Ulsan"),
    SEJONG("Sejong"),
    JEJU("Jeju"),
    ;

    public final String textMode;
}
