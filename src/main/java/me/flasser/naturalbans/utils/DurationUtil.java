package me.flasser.naturalbans.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtil {
    public static Long parseDuration(String input) {

        Pattern pattern = Pattern.compile("^(\\d+)([mhd])$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return null;
        }

        int time = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        Calendar calendar = Calendar.getInstance();
        switch (unit) {
            case "s":
                calendar.add(Calendar.SECOND, time);
                break;
            case "m":
                calendar.add(Calendar.MINUTE, time);
                break;
            case "h":
                calendar.add(Calendar.HOUR, time);
                break;
            case "d":
                calendar.add(Calendar.DAY_OF_MONTH, time);
                break;
            case "w":
                calendar.add(Calendar.DAY_OF_MONTH, time*7);
                break;
            case "y":
                calendar.add(Calendar.YEAR, time);
                break;
            default:
                return null;
        }

        return calendar.getTimeInMillis();
    }
}
