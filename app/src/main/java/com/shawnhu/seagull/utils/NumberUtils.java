package com.shawnhu.seagull.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtils {
    /*
     * NumberFormat isn't synchronized, so a separate instance must be created for each thread
     * http://developer.android.com/reference/java/text/NumberFormat.html
     */
    private static final ThreadLocal<NumberFormat> IntegerInstance = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getIntegerInstance();
        }
    };

    private static final ThreadLocal<DecimalFormat> DecimalInstance = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return (DecimalFormat) DecimalFormat.getInstance();
        }
    };

    /*
     * returns the passed integer formatted with thousands-separators based on the current locale
     */
    public static final String formatInt(int value) {
        return IntegerInstance.get().format(value).toString();
    }

    public static final String formatDecimal(int value) {
        return DecimalInstance.get().format(value).toString();
    }

    static public final int K = 1000;
    static public final int M = K*K;
    static public final int B = K*M;

    public static final String formatIntToRought(int value) {
        String sign = value >= 0 ? "" : "-";
        value = Math.abs(value);

        String vs;
        if (value < K) {
            vs = String.valueOf(value);
        } else if (value >= K && value < M) {
            value /= K;
            vs = String.valueOf(value) + "K";
        } else if (value >= M && value < B) {
            value /= M;
            vs = String.valueOf(value) + "M";
        } else {
            value /= B;
            vs = String.valueOf(value) + "B";
        }

        return sign+vs;
    }
}
