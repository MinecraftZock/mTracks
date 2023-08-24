package info.mx.tracks.rest;

public class OpenWeather extends AbstractOpenWeather {
    public OpenWeather() {
        super(DEFAULT_BASE_URL, false);
    }

    public OpenWeather(boolean debug) {
        super(DEFAULT_BASE_URL, debug);
    }

    public OpenWeather(String baseUrl) {
        super(baseUrl, false);
    }

    public OpenWeather(String baseUrl, boolean debug) {
        super(baseUrl, debug);
    }
}
