package sk.macaj.weather.data.entities;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Location {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    private String country;

    private String region;

    private Double lat;

    private Double lon;

    private String tzId;
}
