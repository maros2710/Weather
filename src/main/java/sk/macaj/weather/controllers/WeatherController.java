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
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

    @GetMapping("/chart/{city}/{from}/{to}")
    public List<DailyAverage> chart(@PathVariable String city, @PathVariable String from, @PathVariable String to) throws Exception {
        Date fromDate = getDate(from);
        Date toDate = getDate(to);

        Calendar calTo = Calendar.getInstance();
        calTo.setTime(toDate);
        calTo.add(Calendar.DAY_OF_MONTH, 1);

        Location location = locationRepository.findByName(city);
        if (location == null) {
            return new ArrayList<>();
        }

        List<Weather> weathers = weatherRepository.findByLocationAndUpdatedBetween(location, fromDate, calTo.getTime());

        ZoneId zoneId = ZoneId.systemDefault();
        Map<LocalDate, List<Weather>> byDay = weathers.stream()
                .collect(Collectors.groupingBy(weather ->
                        weather.getUpdated().toInstant().atZone(zoneId).toLocalDate()));

        LocalDate start = fromDate.toInstant().atZone(zoneId).toLocalDate();
        LocalDate end = toDate.toInstant().atZone(zoneId).toLocalDate();

        List<DailyAverage> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<Weather> dayWeathers = byDay.get(date);
            DailyAverage avg = new DailyAverage();
            avg.date = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            if (dayWeathers == null || dayWeathers.isEmpty()) {
                result.add(avg);
                continue;
            }

            avg.avgTemp = average(dayWeathers, false, Weather::getTempC);
            avg.avgTempDay = average(dayWeathers, true, Weather::getTempC);
            avg.avgFeels = average(dayWeathers, false, Weather::getFeelsLikeC);
            avg.avgFeelsDay = average(dayWeathers, true, Weather::getFeelsLikeC);
            avg.minTemp = min(dayWeathers, Weather::getTempC);
            avg.maxTemp = max(dayWeathers, Weather::getTempC);
            avg.condition = resolveCondition(dayWeathers);
            result.add(avg);
        }

        return result;
    }

    private Date getDate(String d) throws Exception {
        return new SimpleDateFormat("dd.MM.yyyy").parse(d);
    }

    private Double average(List<Weather> weathers, boolean dayOnly, Function<Weather, Double> getter) {
        DoubleSummaryStatistics stats = weathers.stream()
                .filter(weather -> !dayOnly || weather.isDay())
                .map(getter)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();
        return stats.getCount() == 0 ? null : stats.getAverage();
    }

    private Double min(List<Weather> weathers, Function<Weather, Double> getter) {
        DoubleSummaryStatistics stats = weathers.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();
        return stats.getCount() == 0 ? null : stats.getMin();
    }

    private Double max(List<Weather> weathers, Function<Weather, Double> getter) {
        DoubleSummaryStatistics stats = weathers.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();
        return stats.getCount() == 0 ? null : stats.getMax();
    }

    private String resolveCondition(List<Weather> weathers) {
        int sunny = 0;
        int cloudy = 0;
        int rainy = 0;
        int snowy = 0;

        for (Weather weather : weathers) {
            Double precip = weather.getPrecipMM();
            Double cloud = weather.getCloud();
            String text = weather.getText();
            if (text != null) {
                String lower = text.toLowerCase();
                if (lower.contains("snow") || lower.contains("sneh")) {
                    snowy++;
                    continue;
                }
            }

            if (precip != null && precip > 0.2) {
                rainy++;
            } else if (cloud != null && cloud >= 60) {
                cloudy++;
            } else if (cloud != null || precip != null) {
                sunny++;
            }
        }

        if (sunny == 0 && cloudy == 0 && rainy == 0 && snowy == 0) {
            return "unknown";
        }

        if (snowy >= rainy && snowy >= cloudy && snowy >= sunny) {
            return "snowy";
        }
        if (rainy >= cloudy && rainy >= sunny) {
            return "rainy";
        }
        if (cloudy >= sunny) {
            return "cloudy";
        }
        return "sunny";
    }

    static class DailyAverage {
        public String date;
        public Double avgTemp;
        public Double avgTempDay;
        public Double avgFeels;
        public Double avgFeelsDay;
        public Double minTemp;
        public Double maxTemp;
        public String condition;
    }
}
