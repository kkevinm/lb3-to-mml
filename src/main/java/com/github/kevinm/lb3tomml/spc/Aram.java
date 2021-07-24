package com.github.kevinm.lb3tomml.spc;

import java.util.Arrays;

public class Aram {
    
    private final byte[] ram;
    
    private Aram(byte[] ram) {
        this.ram = new byte[Spc.RAM_LEN];
        Arrays.copyOf(ram, Spc.RAM_LEN);
    }
    
    public static Aram fromSpc(Spc spc) {
        return new Aram(spc.ram);
    }
    
    public byte getByte(int address) {
        return ram[address];
    }
    
    public short getWord(int address) {
        return (short) ((ram[address] & 0xff) + (ram[address+1] << 8));
    }
    
    public int getLong(int address) {
        return (ram[address] & 0xff) + (ram[address+1] << 8) + (ram[address+2] << 16);
    }
    
}
