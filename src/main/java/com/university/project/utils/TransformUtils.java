package com.university.project.utils;

public class TransformUtils {
    private TransformUtils(){}

    public static String roundOff(Double value) {
        double roundOff = (double) Math.round(value * 100) / 100;
        return String.valueOf(roundOff);
    }
}
