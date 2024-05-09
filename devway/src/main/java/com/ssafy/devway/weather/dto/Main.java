package com.ssafy.devway.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class Main {

    private double temp;
    private double temp_min;
    private double temp_max;
}
