package com.github.kevinm.lb3tomml.lb3;

public class Note {
    
    private static final int STARTING_OCTAVE = 1;
    private static final String[] NOTES = {
            "c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"
    };
    
    private final int value;
    
    public Note(int value) {
        if (value < 0 || value > 0x5f) {
            throw new IllegalArgumentException(String.format("Invalid note value: 0x%2X", value));
        }
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getName() {
        return NOTES[value % 12];
    }
    
    public int getOctave() {
        return (value / 12) + STARTING_OCTAVE;
    }
    
    public int getNoiseFrequency() {
        return value - 0x40;
    }
    
    public boolean isNote() {
        return value < 0x3c;
    }
    
    public boolean isRest() {
        return value == 0x3c;
    }
    
    public boolean isTie() {
        return value == 0x3b;
    }
    
    public boolean isNoise() {
        return value > 0x3f;
    }
    
}
