package sk.macaj.weather.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Weather {
    @Id
    @GeneratedValue
    private Long id;

    private Date updated;

    private Double tempC;

    private boolean day;

    private String text;

    private String icon;

    private Integer code;

    private Double windKph;

    private Double windDegree;

    private String windDir;

    private Double pressureMB;

    private Double precipMM;

    private Double humidity;

    private Double cloud;

    private Double feelsLikeC;

    private Double visKm;

    private Double uv;

    private Double gustKph;

    @ManyToOne
    private Location location;
}
