package dk.flasser.naturalbans.utils;

import eu.okaeri.platform.core.annotation.Component;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DurationUtil {
    public Long parseDuration(String input) {

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

    public String getRemaining(Long expires) {
        Long diff = expires - System.currentTimeMillis();
        if (diff <= 0L) {
            return "Expired";
        } else {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            diff = diff - TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            diff = diff - TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toDays(diff);
            diff = diff - TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toDays(diff);
            diff = diff - TimeUnit.SECONDS.toMillis(seconds);
            StringBuilder result = new StringBuilder();
            if (days > 0L) {
                result.append(days).append(" day").append(days > 1L ? "s" : "").append(" ");
            }

            if (hours > 0L) {
                result.append(hours).append(" hour").append(hours > 1L ? "s" : "").append(" ");
            }

            if (minutes > 0L) {
                result.append(minutes).append(" minute").append(minutes > 1L ? "s" : "").append(" ");
            }

            if (seconds > 0L) {
                result.append(seconds).append(" second").append(seconds > 1L ? "s" : "");
            }

            return result.toString();
        }
    }
}
