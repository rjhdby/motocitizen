package motocitizen.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getTime(Date date) {
        return (new SimpleDateFormat("HH:mm", Locale.getDefault())).format(date);
    }

    public static String getDateTime(Date date) {
        return (new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())).format(date);
    }

    public static String getDbFormat(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())).format(date);
    }
}
