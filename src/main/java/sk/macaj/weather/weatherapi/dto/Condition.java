package sk.macaj.weather.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition {

    private String text;
    private String icon;
    private Integer code;

}
