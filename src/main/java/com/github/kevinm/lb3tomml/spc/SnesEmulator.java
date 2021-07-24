package com.github.kevinm.lb3tomml.spc;

public enum SnesEmulator {
    ZSNES("ZSnes"), SNES9X("Snes9x"), UNKNOWN("Unknown");
    
    private final String description;
    
    SnesEmulator(String description) {
        this.description = description;
    }
    
    public static SnesEmulator fromId(int id) {
        switch(id) {
        case 1:
            return ZSNES;
        case 2:
            return SNES9X;
        default:
            return UNKNOWN;
        }
    }
    
    @Override
    public String toString() {
        return description;
    }
    
}
