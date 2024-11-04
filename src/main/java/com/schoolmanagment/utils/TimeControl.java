package com.schoolmanagment.utils;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeControl {

    public static boolean check(LocalTime start, LocalTime stop){
        return start.isAfter(stop) || start.equals(stop);
    }
}
