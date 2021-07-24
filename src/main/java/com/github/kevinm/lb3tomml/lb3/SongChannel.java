package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.spc.Aram;

public class SongChannel {
    
    // Constants
    final Aram aram;
    final int id;
    final int startAddress;
    
    // Runtime variables
    int pc = 0;
    int currentLength = 0;
    int[] returnAddress = {0, 0};
    int[] superloopStartAddress = {0, 0};
    int[] superloopEndAddress = {0, 0};
    int[] superloopCounter = {0, 0};
    
    // private List<>
    
    SongChannel(Aram aram, int id, int startAddress) {
        this.aram = aram;
        this.id = id;
        this.startAddress = startAddress;
    }
    
    public void disassemble() {
        pc = startAddress;
        boolean end = false;
        
        while (!end) { // NOSONAR
            // Read the current hex command.
            int cmd = aram.getUnsignedByte(pc++);
            System.out.println(String.format("Processing command 0x%02x on channel %d", cmd, id));
            HexCommand hexCommand = HexCommand.of(cmd);
            hexCommand.process(this);
        }
    }
    
    public int getNextSignedByte() {
        return aram.getSignedByte(pc++);
    }
    
    public int getNextSignedWord() {
        int next = aram.getSignedWord(pc);
        pc += 2;
        return next;
    }
    
    public int getNextSignedLong() {
        int next = aram.getSignedLong(pc);
        pc += 3;
        return next;
    }
    
    public int getNextUnsignedByte() {
        return aram.getUnsignedByte(pc++);
    }
    
    public int getNextUnsignedWord() {
        int next = aram.getUnsignedWord(pc);
        pc += 2;
        return next;
    }
    
    public int getNextUnsignedLong() {
        int next = aram.getUnsignedLong(pc);
        pc += 3;
        return next;
    }
    
}
