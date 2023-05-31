package sk.macaj.weather.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Current {


    @JsonProperty("last_updated")
    private Date lastUpdated;

    @JsonProperty("temp_c")
    private Double tempC;

    @JsonProperty("is_day")
    private boolean day;

    @JsonProperty("wind_kph")
    private Double windKph;

    @JsonProperty("wind_degree")
    private Double windDegree;

    @JsonProperty("wind_dir")
    private String windDir;

    @JsonProperty("pressure_mb")
    private Double pressureMB;

    @JsonProperty("precip_mm")
    private Double precipMM;

    private Double humidity;

    private Double cloud;

    @JsonProperty("feelslike_c")
    private Double feelsLikeC;

    @JsonProperty("vis_km")
    private Double visKm;

    private Double uv;

    @JsonProperty("gust_kph")
    private Double gustKph;

    private Condition condition;
}
