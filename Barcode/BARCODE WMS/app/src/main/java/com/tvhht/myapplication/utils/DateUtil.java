package com.tvhht.myapplication.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static final String DATE_FORMAT_PATTERN1 = "dd-MM-yyyy";
    public static final String DATE_FORMAT_PATTERN3 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_PATTERN2 = "yyyy-MM-dd'T'HH:mm:ss.SSS+HH:mm";
    private static final String TAG = "DateUtil";

    public static String getDateYYYYMMDD(String time) {

        if (TextUtils.isEmpty(time)) {
            return "";
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN2);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN1);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            str = time;
            Log.e(TAG, "Exception " + e.getMessage());
        }
        return str;
    }


    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN2);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


    public static String getCurrentDateOnly() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN1);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getCurrentDateYYYYOnly() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN3);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


}
