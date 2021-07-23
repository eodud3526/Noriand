package com.noriand.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getTodayDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String today = simpleDateFormat.format(new Date());
        return today;
    }

    public static String getMinuteSecondByLong(long l) {
        String str = new SimpleDateFormat("mm:ss").format(new Date(l));
        return str;
    }
}
