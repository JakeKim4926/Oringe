package com.ssafy.devway.weather;

import com.ssafy.devway.block.element.BlockElement;
import com.ssafy.devway.weather.dto.Forecast;
import com.ssafy.devway.weather.dto.ForecastResponseDTO;
import com.ssafy.devway.weather.dto.WeatherResponseDTO;
import com.ssafy.devway.weather.property.WeatherProperties;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class WeatherBlock implements BlockElement {

    @Getter
    private String cityName;

    @Getter
    private String weather;

    @Getter
    private String description;

    @Getter
    private double temperature;

    @Getter
    private List<Double> temperatureList;

    @Getter
    private List<Double> temperatureMinList;

    @Getter
    private List<Double> temperatureMaxList;

    @Getter
    private List<Double> rainList;

    private final WeatherProperties properties;

    public WeatherBlock(String API_KEY) {
        this.properties = new WeatherProperties();
        properties.setApiKey(API_KEY);
    }

    @Override
    public String getName() {
        return "WEATHER";
    }

    public void todayWeather(String city, WeatherCountry weatherCountry) {
        String selectedCity = city + weatherCountry.textMode;
        StringBuilder urlBuilder = new StringBuilder(properties.getNowUrl());
        try {
            urlBuilder.append("?" + URLEncoder.encode("q", "UTF-8") + "=" + selectedCity);
            urlBuilder.append(
                "&" + URLEncoder.encode("appid", "UTF-8") + "=" + properties.getApiKey());
            urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=kr");
            urlBuilder.append("&" + URLEncoder.encode("units", "UTF-8") + "=metric");

            RestTemplate restTemplate = new RestTemplate();
            WeatherResponseDTO response = restTemplate.getForObject(urlBuilder.toString(),
                WeatherResponseDTO.class);
            if (response != null && response.getWeather() != null && !response.getWeather()
                .isEmpty()) {
                cityName = response.getName();
                weather = response.getWeather().get(0).getMain();
                description = response.getWeather().get(0).getDescription();
                temperature = response.getMain().getTemp();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("API 키를 찾을 수 없습니다. 다시 입력해 주세요.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("도시를 찾을 수 없습니다. 다시 입력해 주세요.");
            } else {
                System.out.println("기타 클라이언트 오류: " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            System.out.println("서버 오류: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("알 수 없는 오류 발생: " + e.getMessage());
        }
    }

    public void forecastWeather(String city, WeatherCountry weatherCountry) {
        String selectedCity = city + weatherCountry.textMode;
        StringBuilder urlBuilder = new StringBuilder(properties.getForecastUrl());
        try {
            urlBuilder.append("?" + URLEncoder.encode("q", "UTF-8") + "=" + selectedCity);
            urlBuilder.append(
                "&" + URLEncoder.encode("appid", "UTF-8") + "=" + properties.getApiKey());
            urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=kr");
            urlBuilder.append("&" + URLEncoder.encode("units", "UTF-8") + "=metric");
            RestTemplate restTemplate = new RestTemplate();
            ForecastResponseDTO response = restTemplate.getForObject(urlBuilder.toString(),
                ForecastResponseDTO.class);
            if (response != null && response.getList() != null && !response.getList()
                .isEmpty()) {
                cityName = response.getCity().getName();
                int cnt = 0;
                double temp = 0.0;
                double min = 100.0;
                double max = 0.0;
                double rain = 0.0;
                temperatureList = new ArrayList<>();
                temperatureMinList = new ArrayList<>();
                temperatureMaxList = new ArrayList<>();
                rainList = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime currentDate = null;
                for (Forecast forecast : response.getList()) {
                    LocalDateTime forecastDate = LocalDateTime.parse(forecast.getDt_txt(),
                        formatter).plusHours(9);
                    if (currentDate == null || currentDate.toLocalDate()
                        .equals(forecastDate.toLocalDate())) {
                        temp += forecast.getMain().getTemp();
                        rain += forecast.getPop();
                        if (min > forecast.getMain().getTemp_min()) {
                            min = forecast.getMain().getTemp_min();
                        }
                        if (max < forecast.getMain().getTemp_max()) {
                            max = forecast.getMain().getTemp_max();
                        }
                        cnt++;
                    } else {
                        temperatureList.add(Math.round(temp / cnt * 100) / 100.0);
                        temperatureMinList.add(min);
                        temperatureMaxList.add(max);
                        if (rain == 0.0) {
                            rainList.add(0.0);
                        } else {
                            rain = rain * 100 / cnt;
                            rainList.add(Math.round(rain * 100) / 100.0);
                        }
                        temp = forecast.getMain().getTemp();
                        min = forecast.getMain().getTemp_min();
                        max = forecast.getMain().getTemp_max();
                        rain = forecast.getPop();
                        cnt = 1;
                    }
                    currentDate = forecastDate;
                }
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("API 키를 찾을 수 없습니다. 다시 입력해 주세요.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("도시를 찾을 수 없습니다. 다시 입력해 주세요.");
            } else {
                System.out.println("기타 클라이언트 오류: " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            System.out.println("서버 오류: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("알 수 없는 오류 발생: " + e.getMessage());
        }
    }
}
