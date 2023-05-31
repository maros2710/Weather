package sk.macaj.weather.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sk.macaj.weather.data.repositories.WeatherRepository;

@RestController
public class TestController {

    private WeatherRepository weatherRepository;

    public TestController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @GetMapping("/test")
    public Object test() {
        return weatherRepository.findAll();
    }
}
