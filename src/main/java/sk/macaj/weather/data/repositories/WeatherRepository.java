package sk.macaj.weather.data.repositories;

import org.springframework.data.repository.CrudRepository;
import sk.macaj.weather.data.entities.Location;
import sk.macaj.weather.data.entities.Weather;

import java.util.Date;
import java.util.List;

public interface WeatherRepository extends CrudRepository<Weather, Long> {

    List<Weather> findByLocationAndUpdatedBetween(Location location, Date from, Date to);

}
