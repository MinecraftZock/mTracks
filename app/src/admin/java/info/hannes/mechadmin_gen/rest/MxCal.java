package info.hannes.mechadmin_gen.rest;

public class MxCal extends AbstractMxCal {
    public MxCal() {
        super(DEFAULT_BASE_URL, false);
    }

    public MxCal(boolean debug) {
        super(DEFAULT_BASE_URL, debug);
    }

    public MxCal(String baseUrl) {
        super(baseUrl, false);
    }

    public MxCal(String baseUrl, boolean debug) {
        super(baseUrl, debug);
    }
}
