package com.ssafy.devway.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Forecast {

    private String dt_txt;
    private Main main;
    private double pop;
}
