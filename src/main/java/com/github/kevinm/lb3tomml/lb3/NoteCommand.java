package com.github.kevinm.lb3tomml.lb3;

public class NoteCommand extends HexCommand {
    
    private static final int STARTING_OCTAVE = 1;
    private static final String[] NOTES = {
            "c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"
    };
    
    public NoteCommand(int value) {
        super(value);
        
        if (value < 0 || value > 0x5f) {
            throw new IllegalArgumentException(String.format("Invalid note command: 0x%02x", value));
        }
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

    @Override
    public void process(SongChannel channel) {
        // TODO Auto-generated method stub
        
    }
    
}
