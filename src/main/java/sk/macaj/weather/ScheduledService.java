package sk.macaj.weather;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sk.macaj.weather.data.DatabaseService;
import sk.macaj.weather.weatherapi.WeatherApiService;
import sk.macaj.weather.weatherapi.dto.Weather;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class ScheduledService {

    private WeatherApiService weatherApiService;
    private DatabaseService databaseService;

    private final List<String> cities;

    public ScheduledService(WeatherApiService weatherApiService, DatabaseService databaseService) {
        this.weatherApiService = weatherApiService;
        this.databaseService = databaseService;

        cities = Arrays.asList("Trnava", "Bratislava", "Trencin", "Zilina", "Kosice", "Poprad", "Banska Bystrica", "Nitra", "Piestany");
    }

//    @PostConstruct
//    public void init() {
//        execute();
//    }

    @Scheduled(fixedDelay = 14400000)
    public void execute() {
        cities.forEach(c -> {
            try {
                Weather weather = weatherApiService.getWeather(c);

                if (weather != null) {
                    databaseService.save(weather);
                }
                Thread.sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
