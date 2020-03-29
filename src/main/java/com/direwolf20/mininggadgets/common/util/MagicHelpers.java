package com.direwolf20.mininggadgets.common.util;

public class MagicHelpers {
    public static String tidyValue(float value) {
        if (value < 1000)
            return String.valueOf(value);

        int exp = (int) (Math.log(value) / Math.log(1000));
        return String.format("%.1f%c",
                value / Math.pow(1000, exp),
                "kMGTPE_____".charAt(exp - 1));
    }
}
