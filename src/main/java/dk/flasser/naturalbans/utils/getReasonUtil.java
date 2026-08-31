package dk.flasser.naturalbans.utils;

import eu.okaeri.platform.core.annotation.Component;

import java.util.Arrays;

@Component
public class getReasonUtil {

    public static String getReason(String[] args, Integer start, Integer end) {

        if (args.length <= start) {
            return "No reason provided";
        } else {
            String[] reasonArgs = Arrays.copyOfRange(args, start, args.length - end);

            return String.join(" ", reasonArgs);
        }
    }
}
