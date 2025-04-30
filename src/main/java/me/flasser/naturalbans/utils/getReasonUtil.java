package me.flasser.naturalbans.utils;

import java.util.Arrays;

public class getReasonUtil {

    public static String getReason(String[] args, Integer i) {

        if (args.length < 2+i) {
            return "No reason provided";
        }

        String[] reasonArgs = Arrays.copyOfRange(args, 2, args.length - i);

        return String.join("", reasonArgs);
    }
}
