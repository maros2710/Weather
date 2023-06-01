$(document).ready(function() {

    let weatherData = {};

    let fillWeather = function() {
        $('#weather').html("");

        let d = $('#datetime').val();

        let weather = weatherData[d];

        if(weather) {
            $('#weather').append('<div><img src="' + weather.icon + '"></div>');
            $('#weather').append('<div>Pocasie:&nbsp;' + weather.text + '</div>');
            $('#weather').append('<div>Teplota:&nbsp;' + weather.tempC + '</div>');
            $('#weather').append('<div>Pocitova teplota:&nbsp;' + weather.feelsLikeC + '</div>');
            $('#weather').append('<div>Oblaky:&nbsp;' + weather.cloud + '</div>');
            $('#weather').append('<div>Zrazky (mm):&nbsp;' + weather.precipMM + '</div>');
            $('#weather').append('<div>Vietor (km/h):&nbsp;' + weather.windKph + '</div>');
            $('#weather').append('<div>Narazy (km/h):&nbsp;' + weather.gustKph + '</div>');
            $('#weather').append('<div>Smer vetra:&nbsp;' + weather.windDir + '</div>');
            $('#weather').append('<div>Vlhkost:&nbsp;' + weather.humidity + '</div>');
            $('#weather').append('<div>Tlak (MB):&nbsp;' + weather.pressureMB + '</div>');
            $('#weather').append('<div>UV index:&nbsp;' + weather.uv + '</div>');
        } else {
            $('#weather').append('<div>Ziadne data</div>');
        }
    };

    let load = function() {
        let date = $('#date').val();
        let city = $('#city').val();

        $.get( "/city/" + city + "/" + date, function(data) {
            weatherData = data;

            $("#weather").html("test");

            $("#datetime").html("");

            Object.keys(weatherData).forEach(key => {
                $("#datetime").append("<option value='" + key + "'>" + key + "</option>");
            });

            fillWeather();
        });
    }

    $('#datetime').change(function(){
       fillWeather();
    });

    $('#date').datepicker({
        dateFormat: "dd.mm.yy",
        minDate: new Date(2023, 5 - 1, 31)
    });
    $('#date').datepicker('setDate', 'today');


    $('#date').change(function(){
        load();
    });

    $('#city').change(function() {
        load();
    });

    load();
});