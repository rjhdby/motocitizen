package motocitizen.network;

import java.util.Map;

/**
 * Created by elagin on 31.03.15.
 */
public class JsonRequest {
    public String app;
    public String method;
    public Boolean isHttps;
    public Map<String, String> params;
    public String arrayName;

    public JsonRequest(String app, String method, Map<String, String> params, String arrayName, Boolean isHttps) {
        this.app = app;
        this.method = method;
        this.params = params;
        this.arrayName = arrayName;
        this.isHttps = isHttps;
    }
}
