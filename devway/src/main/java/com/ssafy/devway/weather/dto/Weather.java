package com.ssafy.devway.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Weather {

    private String main;
    private String description;
}
