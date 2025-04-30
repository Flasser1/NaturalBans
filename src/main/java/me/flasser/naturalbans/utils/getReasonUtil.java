package me.flasser.naturalbans.utils;

import java.util.Arrays;

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
