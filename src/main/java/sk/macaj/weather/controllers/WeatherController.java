package sk.macaj.weather.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sk.macaj.weather.data.entities.Location;
import sk.macaj.weather.data.entities.Weather;
import sk.macaj.weather.data.repositories.LocationRepository;
import sk.macaj.weather.data.repositories.WeatherRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class WeatherController {

    private final WeatherRepository weatherRepository;
    private final LocationRepository locationRepository;

    public WeatherController(WeatherRepository weatherRepository, LocationRepository locationRepository) {
        this.weatherRepository = weatherRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/city/{city}/{date}")
    public Object weather(@PathVariable String city, @PathVariable String date) throws Exception {
        Date from = getDate(date);
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(from);
        calTo.add(Calendar.DAY_OF_MONTH, 1);

        Location location = locationRepository.findByName(city);

        List<Weather> weathers = weatherRepository.findByLocationAndUpdatedBetween(location, from, calTo.getTime());

        return weathers.stream().collect(Collectors.toMap(Weather::getUpdated, Function.identity(), (first, second) -> first));
    }

    private Date getDate(String d) throws Exception {
        return new SimpleDateFormat("dd.MM.yyyy").parse(d);
    }
}
