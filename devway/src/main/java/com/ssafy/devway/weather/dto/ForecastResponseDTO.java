package com.ssafy.devway.weather.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ForecastResponseDTO {

    private List<Forecast> list;
    private City city;
}
