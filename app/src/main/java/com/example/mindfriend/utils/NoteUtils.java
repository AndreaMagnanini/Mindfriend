package com.example.mindfriend.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteUtils {
    
    public static String dateFromLong(long time){
        DateFormat format = new SimpleDateFormat("EEE, dd MMM YYYY 'alle' hh:mm aaa", Locale.ITALIAN);
        return format.format(new Date(time));
    }
}
