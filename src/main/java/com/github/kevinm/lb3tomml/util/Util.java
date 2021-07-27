package com.github.kevinm.lb3tomml.util;

public final class Util {

    private Util() {}
    
    public static String hexString(int value) {
        return String.format("$%02x", value);
    }
    
    public static String decString(int value) {
        return String.format("%d", value);
    }
    
}
