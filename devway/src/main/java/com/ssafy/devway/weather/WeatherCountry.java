package com.ssafy.devway.weather;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeatherCountry {
    //대문자
    NORMAL(""),
    Seoul("Seoul"),
    Incheon("Incheon"),
    Busan("Busan"),
    Gwangju("Gwangju"),
    Daejeon("Daejeon"),
    Daegu("Daegu"),
    Ulsan("Ulsan"),
    Sejong("Sejong"),
    Jeju("Jeju"),
    ;

    public final String textMode;
}
