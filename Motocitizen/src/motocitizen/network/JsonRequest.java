package motocitizen.network;

import java.util.Map;

public class JsonRequest {
    public final String app;
    public final String method;
    public final Boolean isHttps;
    public final Map<String, String> params;
    public final String arrayName;

    @SuppressWarnings("SameParameterValue")
    public JsonRequest(String app, String method, Map<String, String> params, String arrayName, Boolean isHttps) {
        this.app = app;
        this.method = method;
        this.params = params;
        this.arrayName = arrayName;
        this.isHttps = isHttps;
    }
}
