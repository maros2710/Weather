$(document).ready(function() {

    let weatherData = {};

    let fillWeather = function() {
        $('#weather').html("");

        let d = $('#datetime').val();

        let weather = weatherData[d];

        if(weather) {
            let details = [
                { label: 'Oblaky', value: weather.cloud },
                { label: 'Zrazky (mm)', value: weather.precipMM },
                { label: 'Vietor (km/h)', value: weather.windKph },
                { label: 'Narazy (km/h)', value: weather.gustKph },
                { label: 'Smer vetra', value: weather.windDir },
                { label: 'Vlhkost', value: weather.humidity },
                { label: 'Tlak (MB)', value: weather.pressureMB },
                { label: 'UV index', value: weather.uv }
            ];

            let detailMarkup = details.map(item =>
                '<div class="weather-item"><span>' + item.label + '</span><strong>' + item.value + '</strong></div>'
            ).join('');

            let iconMarkup = weather.icon ? '<img src="' + weather.icon + '" alt="' + weather.text + '">' : '';

            $('#weather').html(
                '<div class="weather-header">' +
                    '<div class="weather-icon">' + iconMarkup + '</div>' +
                    '<div>' +
                        '<div class="weather-title">' + weather.text + '</div>' +
                        '<div class="weather-temp">' + weather.tempC + '&deg;C</div>' +
                        '<div class="weather-feels">Pocitovo ' + weather.feelsLikeC + '&deg;C</div>' +
                    '</div>' +
                '</div>' +
                '<div class="weather-grid">' + detailMarkup + '</div>'
            );
        } else {
            $('#weather').append('<div class="weather-empty">Ziadne data</div>');
        }
    };

    let load = function() {
        let date = $('#date').val();
        let city = $('#city').val();

        $.get( "/city/" + city + "/" + date, function(data) {
            weatherData = data;

            $("#datetime").html("");

            Object.keys(weatherData).sort(compareKeysByTime).forEach(key => {
                let formattedTime = formatTime(key);
                $("#datetime").append("<option value='" + key + "'>" + formattedTime + "</option>");
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

    $('.tab-button').on('click', function() {
        let target = $(this).data('tab');
        $('.tab-button').removeClass('is-active');
        $(this).addClass('is-active');
        $('.tab-panel').removeClass('is-active');
        $('#' + target).addClass('is-active');
    });

    let formatTime = function(value) {
        let isoMatch = value.match(/T(\d{2}:\d{2})/);
        if (isoMatch) {
            return isoMatch[1];
        }
        let parsed = new Date(value);
        if (isNaN(parsed.getTime())) {
            let match = value.match(/(\d{1,2}:\d{2})(?::\d{2})?/);
            return match ? match[1] : value;
        }
        return parsed.toLocaleTimeString('sk-SK', {
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    let compareKeysByTime = function(a, b) {
        let dateA = new Date(a);
        let dateB = new Date(b);
        let timeA = isNaN(dateA.getTime()) ? 0 : dateA.getTime();
        let timeB = isNaN(dateB.getTime()) ? 0 : dateB.getTime();
        return timeA - timeB;
    };

    let chartMetricLabels = {
        avgTemp: 'Priemerna teplota',
        avgTempDay: 'Priemerna teplota cez den',
        avgFeels: 'Priemerna pocitova teplota',
        avgFeelsDay: 'Priemerna pocitova teplota cez den',
        minTemp: 'Minimalna teplota',
        maxTemp: 'Maximalna teplota'
    };

    let parseDateValue = function(value) {
        let parts = value.split('.');
        if (parts.length !== 3) {
            return null;
        }
        let day = parseInt(parts[0], 10);
        let month = parseInt(parts[1], 10) - 1;
        let year = parseInt(parts[2], 10);
        let parsed = new Date(year, month, day);
        return isNaN(parsed.getTime()) ? null : parsed;
    };

    let formatDateValue = function(date) {
        let day = String(date.getDate()).padStart(2, '0');
        let month = String(date.getMonth() + 1).padStart(2, '0');
        let year = date.getFullYear();
        return day + '.' + month + '.' + year;
    };

    let renderChart = function(data, metricKey) {
        let title = chartMetricLabels[metricKey] || 'Graf';
        $('#chart-title').text(title);

        if (!data || data.length === 0) {
            $('#chart-content').html('<div class="chart-empty">Ziadne data pre vybrany rozsah.</div>');
            return;
        }

        let values = data.map(item => item[metricKey]);
        let numericValues = values.filter(value => value !== null && value !== undefined);
        if (numericValues.length === 0) {
            $('#chart-content').html('<div class="chart-empty">Ziadne data pre zvoleny typ.</div>');
            return;
        }

        let min = Math.min.apply(null, numericValues);
        let max = Math.max.apply(null, numericValues);
        if (min === max) {
            min = min - 1;
            max = max + 1;
        }

        let width = 700;
        let height = 260;
        let padding = { top: 20, right: 20, bottom: 40, left: 48 };
        let plotWidth = width - padding.left - padding.right;
        let plotHeight = height - padding.top - padding.bottom;

        let points = data.map((item, index) => {
            let value = item[metricKey];
            if (value === null || value === undefined) {
                return null;
            }
            let x = padding.left + (index / Math.max(data.length - 1, 1)) * plotWidth;
            let ratio = (value - min) / (max - min);
            let y = padding.top + (1 - ratio) * plotHeight;
            return {
                x: x,
                y: y,
                value: value,
                label: item.date
            };
        });

        let path = '';
        let started = false;
        points.forEach(point => {
            if (!point) {
                started = false;
                return;
            }
            if (!started) {
                path += 'M ' + point.x + ' ' + point.y;
                started = true;
            } else {
                path += ' L ' + point.x + ' ' + point.y;
            }
        });

        let gridLines = '';
        let ticks = 4;
        for (let i = 0; i <= ticks; i++) {
            let y = padding.top + (i / ticks) * plotHeight;
            let value = max - (i / ticks) * (max - min);
            gridLines += '<line class="chart-grid" x1="' + padding.left + '" y1="' + y + '" x2="' + (width - padding.right) + '" y2="' + y + '"></line>';
            gridLines += '<text class="chart-label" x="10" y="' + (y + 4) + '">' + value.toFixed(1) + '</text>';
        }

        let pointMarkup = '';
        points.forEach(point => {
            if (!point) {
                return;
            }
            pointMarkup += '<circle class="chart-point" cx="' + point.x + '" cy="' + point.y + '" r="4"' +
                ' data-value="' + point.value.toFixed(1) + '"' +
                ' data-label="' + point.label + '"></circle>';
        });

        let labelStep = Math.ceil(data.length / 6);
        let labels = '';
        data.forEach((item, index) => {
            if (index % labelStep !== 0 && index !== data.length - 1) {
                return;
            }
            let x = padding.left + (index / Math.max(data.length - 1, 1)) * plotWidth;
            let labelDate = item.date;
            if (labelDate && labelDate.includes('-')) {
                let parts = labelDate.split('-');
                labelDate = parts[2] + '.' + parts[1];
            }
            labels += '<text class="chart-label" x="' + (x - 12) + '" y="' + (height - 12) + '">' + labelDate + '</text>';
        });

        let svg = '<svg class="chart-svg" viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="' + title + '">' +
            gridLines +
            '<path class="chart-line" d="' + path + '"></path>' +
            pointMarkup +
            labels +
            '</svg>';

        $('#chart-content').html('<div id="chart-tooltip" class="chart-tooltip"></div>' + svg);
        bindChartTooltip();
    };

    let conditionLabels = {
        sunny: 'Slnecno',
        cloudy: 'Zamracene',
        rainy: 'Prsalo',
        snowy: 'Snezenie',
        unknown: 'Nezname'
    };

    let conditionIcons = {
        sunny:
            '<svg viewBox="0 0 48 48" aria-hidden="true">' +
                '<circle cx="24" cy="24" r="9"></circle>' +
                '<line x1="24" y1="6" x2="24" y2="12"></line>' +
                '<line x1="24" y1="36" x2="24" y2="42"></line>' +
                '<line x1="6" y1="24" x2="12" y2="24"></line>' +
                '<line x1="36" y1="24" x2="42" y2="24"></line>' +
                '<line x1="11" y1="11" x2="15" y2="15"></line>' +
                '<line x1="33" y1="33" x2="37" y2="37"></line>' +
                '<line x1="33" y1="15" x2="37" y2="11"></line>' +
                '<line x1="11" y1="37" x2="15" y2="33"></line>' +
            '</svg>',
        cloudy:
            '<svg viewBox="0 0 64 48" aria-hidden="true">' +
                '<circle cx="24" cy="24" r="12"></circle>' +
                '<circle cx="38" cy="20" r="14"></circle>' +
                '<rect x="12" y="24" width="40" height="16" rx="8"></rect>' +
            '</svg>',
        rainy:
            '<svg viewBox="0 0 64 56" aria-hidden="true">' +
                '<circle cx="24" cy="20" r="12"></circle>' +
                '<circle cx="38" cy="16" r="14"></circle>' +
                '<rect x="12" y="20" width="40" height="16" rx="8"></rect>' +
                '<line x1="20" y1="40" x2="16" y2="50"></line>' +
                '<line x1="32" y1="40" x2="28" y2="52"></line>' +
                '<line x1="44" y1="40" x2="40" y2="50"></line>' +
            '</svg>',
        snowy:
            '<svg viewBox="0 0 64 56" aria-hidden="true">' +
                '<circle cx="24" cy="20" r="12"></circle>' +
                '<circle cx="38" cy="16" r="14"></circle>' +
                '<rect x="12" y="20" width="40" height="16" rx="8"></rect>' +
                '<circle cx="20" cy="44" r="3"></circle>' +
                '<circle cx="32" cy="48" r="3"></circle>' +
                '<circle cx="44" cy="44" r="3"></circle>' +
            '</svg>',
        unknown:
            '<svg viewBox="0 0 48 48" aria-hidden="true">' +
                '<circle cx="24" cy="24" r="12"></circle>' +
                '<line x1="24" y1="16" x2="24" y2="26"></line>' +
                '<circle cx="24" cy="32" r="2"></circle>' +
            '</svg>'
    };

    let renderConditionChart = function(data) {
        if (!data || data.length === 0) {
            $('#condition-content').html('<div class="chart-empty">Ziadne data pre vybrany rozsah.</div>');
            return;
        }

        let items = data.map(item => {
            let status = item.condition || 'unknown';
            let date = item.date || '';
            let displayDate = date;
            if (displayDate.includes('-')) {
                let parts = displayDate.split('-');
                displayDate = parts[2] + '.' + parts[1];
            }
            return (
                '<div class="condition-day">' +
                    '<div class="condition-date">' + displayDate + '</div>' +
                    '<div class="condition-icon condition-icon-' + status + '">' +
                        (conditionIcons[status] || conditionIcons.unknown) +
                        '<span class="sr-only">' + (conditionLabels[status] || conditionLabels.unknown) + '</span>' +
                    '</div>' +
                '</div>'
            );
        }).join('');

        $('#condition-content').html('<div class="condition-grid">' + items + '</div>');
    };

    let bindChartTooltip = function() {
        let chartContent = $('#chart-content');
        let tooltip = $('#chart-tooltip');
        if (!tooltip.length) {
            return;
        }

        let moveTooltip = function(event) {
            let offset = chartContent.offset();
            let x = event.pageX - offset.left + 12;
            let y = event.pageY - offset.top - 32;
            tooltip.css({ left: x + 'px', top: y + 'px' });
        };

        chartContent.find('.chart-point').on('mouseenter', function(event) {
            let value = $(this).data('value');
            let label = $(this).data('label');
            tooltip.html('<strong>' + value + ' C</strong><span>' + label + '</span>');
            tooltip.addClass('is-visible');
            moveTooltip(event);
        });

        chartContent.find('.chart-point').on('mousemove', function(event) {
            moveTooltip(event);
        });

        chartContent.find('.chart-point').on('mouseleave', function() {
            tooltip.removeClass('is-visible');
        });
    };

    let loadChart = function() {
        let city = $('#chart-city').val();
        let from = $('#chart-from').val();
        let to = $('#chart-to').val();
        let metric = $('#chart-metric').val();

        if (!city || !from || !to) {
            return;
        }

        let fromDate = parseDateValue(from);
        let toDate = parseDateValue(to);
        if (!fromDate || !toDate) {
            return;
        }
        if (fromDate > toDate) {
            let temp = fromDate;
            fromDate = toDate;
            toDate = temp;
            $('#chart-from').val(formatDateValue(fromDate));
            $('#chart-to').val(formatDateValue(toDate));
        }

        $.get("/chart/" + city + "/" + $('#chart-from').val() + "/" + $('#chart-to').val(), function(data) {
            renderChart(data, metric);
            renderConditionChart(data);
        });
    };

    $('#chart-city, #chart-metric').change(function() {
        loadChart();
    });

    $('#chart-from, #chart-to').change(function() {
        loadChart();
    });

    load();

    $('#chart-from').datepicker({
        dateFormat: "dd.mm.yy",
        minDate: new Date(2023, 5 - 1, 31)
    });
    $('#chart-to').datepicker({
        dateFormat: "dd.mm.yy",
        minDate: new Date(2023, 5 - 1, 31)
    });

    let today = new Date();
    let weekAgo = new Date();
    weekAgo.setDate(today.getDate() - 6);
    $('#chart-from').datepicker('setDate', weekAgo);
    $('#chart-to').datepicker('setDate', today);

    loadChart();
});
