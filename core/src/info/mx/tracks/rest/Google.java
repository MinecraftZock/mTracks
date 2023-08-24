package info.mx.tracks.rest;

public class Google extends AbstractGoogle {
    public Google() {
        super(DEFAULT_BASE_URL, false);
    }

    public Google(boolean debug) {
        super(DEFAULT_BASE_URL, debug);
    }

    public Google(String baseUrl) {
        super(baseUrl, false);
    }

    public Google(String baseUrl, boolean debug) {
        super(baseUrl, debug);
    }
}
