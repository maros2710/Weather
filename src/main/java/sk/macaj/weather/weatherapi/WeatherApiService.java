package sk.macaj.weather.weatherapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sk.macaj.weather.weatherapi.dto.Weather;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherApiService {

    @Value(value = "${weather.key}")
    private String weatherKey;

    public Weather getWeather(String city) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            return mapper.readValue(createUrl(city), Weather.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private URL createUrl(String city) throws MalformedURLException {
        return new URL("http://api.weatherapi.com/v1/current.json?key=" + weatherKey + "&q=" + city + "&aqi=no");
    }
}
