package sk.macaj.weather.data.repositories;

import org.springframework.data.repository.CrudRepository;
import sk.macaj.weather.data.entities.Location;
import sk.macaj.weather.data.entities.Weather;

public interface WeatherRepository extends CrudRepository<Weather, Long> {

}
