package motocitizen.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class Props {
    private static final String     GLOBAL  = "global.properties";
    private static       Properties summary = new Properties();
    public static        String     SERVER  = "server";

    public Props(Context context) {
        AssetManager as = context.getAssets();
        InputStream  is;
        try {
            is = as.open(GLOBAL);
            Reader reader;
            reader = new InputStreamReader(is, "UTF-8");
            summary.load(reader);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return summary.getProperty(key);
    }
}
