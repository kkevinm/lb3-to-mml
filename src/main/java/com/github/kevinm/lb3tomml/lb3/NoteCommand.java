package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.util.Log;

public class NoteCommand extends HexCommand {
    
    private static final int STARTING_OCTAVE = 1;
    
    public NoteCommand(int value) {
        super(value);
        
        if (value < 0 || value > 0x5f) {
            throw new IllegalArgumentException(String.format("Invalid note command: 0x%02x", value));
        }
    }
    
    public String getName() {
        if (isNote()) {
            return MmlSymbol.NOTES[value % 12];
        } else if (isRest()) {
            return MmlSymbol.REST;
        } else if (isTie()) {
            return MmlSymbol.TIE;
        } else {
            return MmlSymbol.NOISE;
        }
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
    public MmlCommand process(SongChannel channel) {
        StringBuilder newNote = new StringBuilder();

        if (value == 0x3e) {
            Log.log("Unsupported command 0x3e");
            return MmlCommand.empty();
        }

        if (isNoise()) {
            newNote.append(getName());
            newNote.append(String.format("%02x", getNoiseFrequency()));
            newNote.append(" c");
        } else if(isNote()) {
            int currentOctave = channel.getCurrentOctave();
            int newOctave = getOctave();
            if (currentOctave == 0) {
                newNote.append(MmlSymbol.OCTAVE);
                newNote.append(newOctave);
                newNote.append(' ');
            } else if (currentOctave != newOctave) {
                newNote.append(getOctaveChange(currentOctave, newOctave));
                newNote.append(' ');
            }
            newNote.append(getName());
            channel.setCurrentOctave(newOctave);
        } else {
            newNote.append(getName());
        }
        
        MmlCommand result = new MmlCommand(newNote.toString(), channel.getTickLength());
        
        Log.log("Processing note 0x%02x", value);
        Log.indent();
        Log.log("Note: %s - Rest: %s - Tie: %s - Noise: %s", isNote(), isRest(), isTie(), isNoise());
        Log.log("Name: %s - Octave: %d - Length: %d", getName(), getOctave(), channel.getCurrentLength());
        Log.log("Converted to: %s", result.toString());
        Log.unindent();
        
        return result;
    }
    
    private String getOctaveChange(int currentOctave, int newOctave) {
        StringBuilder res = new StringBuilder();
        char symbol = currentOctave > newOctave ? '<' : '>';
        for (int i = 0; i < Math.abs(currentOctave-newOctave); i++) {
            res.append(symbol);
        }
        return res.toString();
    }
    
}
