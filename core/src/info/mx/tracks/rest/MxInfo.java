package info.mx.tracks.rest;

public class MxInfo extends AbstractMxInfo {
    public MxInfo() {
        super(DEFAULT_BASE_URL, false);
    }

    public MxInfo(boolean debug) {
        super(DEFAULT_BASE_URL, debug);
    }

    public MxInfo(String baseUrl) {
        super(baseUrl, false);
    }

    public MxInfo(String baseUrl, boolean debug) {
        super(baseUrl, debug);
    }
}
