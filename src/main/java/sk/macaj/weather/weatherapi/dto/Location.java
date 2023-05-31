package sk.macaj.weather.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

    private String name;
    private String region;
    private String country;
    private Double lat;
    private Double lon;
    @JsonProperty("tz_id")
    private String tzId;
    private Date localtime;
}
