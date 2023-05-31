package sk.macaj.weather.data;

import org.springframework.stereotype.Service;
import sk.macaj.weather.data.entities.Location;
import sk.macaj.weather.data.entities.Weather;
import sk.macaj.weather.data.repositories.LocationRepository;
import sk.macaj.weather.data.repositories.WeatherRepository;

@Service
public class DatabaseService {

    private LocationRepository locationRepository;

    private WeatherRepository weatherRepository;


    public DatabaseService(LocationRepository locationRepository, WeatherRepository weatherRepository) {
        this.locationRepository = locationRepository;
        this.weatherRepository = weatherRepository;
    }

    public void save(sk.macaj.weather.weatherapi.dto.Weather weather) {
        Location location = getLocation(weather.getLocation());
        Weather dbWeather = convert(weather);
        dbWeather.setLocation(location);

        weatherRepository.save(dbWeather);
    }

    private Location getLocation(sk.macaj.weather.weatherapi.dto.Location location) {
        Location loc = locationRepository.findByName(location.getName());
        if(loc == null) {
            loc = new Location();
            loc.setCountry(location.getCountry());
            loc.setName(location.getName());
            loc.setLon(location.getLon());
            loc.setLat(location.getLat());
            loc.setRegion(location.getRegion());
            loc.setTzId(location.getTzId());
            locationRepository.save(loc);
        }

        return loc;
    }

    private Weather convert (sk.macaj.weather.weatherapi.dto.Weather weather) {
        Weather db = new Weather();

        db.setCloud(weather.getCurrent().getCloud());
        db.setDay(weather.getCurrent().isDay());

        db.setIcon(weather.getCurrent().getCondition().getIcon());
        db.setCode(weather.getCurrent().getCondition().getCode());
        db.setText(weather.getCurrent().getCondition().getText());

        db.setHumidity(weather.getCurrent().getHumidity());
        db.setUv(weather.getCurrent().getUv());
        db.setGustKph(weather.getCurrent().getGustKph());
        db.setFeelsLikeC(weather.getCurrent().getFeelsLikeC());
        db.setPrecipMM(weather.getCurrent().getPrecipMM());
        db.setPressureMB(weather.getCurrent().getPressureMB());
        db.setTempC(weather.getCurrent().getTempC());
        db.setUpdated(weather.getCurrent().getLastUpdated());
        db.setVisKm(weather.getCurrent().getVisKm());
        db.setWindDegree(weather.getCurrent().getWindDegree());
        db.setWindDir(weather.getCurrent().getWindDir());
        db.setWindDegree(weather.getCurrent().getWindDegree());
        db.setWindKph(weather.getCurrent().getWindKph());

        return db;
    }
}
