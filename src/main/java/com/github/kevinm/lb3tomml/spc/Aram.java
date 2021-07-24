package com.github.kevinm.lb3tomml.spc;

import java.util.Arrays;

public class Aram {
    
    private final byte[] ram;
    
    private Aram(byte[] ram) {
        this.ram = Arrays.copyOf(ram, Spc.RAM_LEN);
    }
    
    public static Aram fromSpc(Spc spc) {
        return new Aram(spc.ram);
    }
    
    public int getSignedByte(int address) {
        return ram[address];
    }
    
    public int getSignedWord(int address) {
        return (ram[address] & 0xff) + (ram[address+1] << 8);
    }
    
    public int getSignedLong(int address) {
        return (ram[address] & 0xff) + (ram[address+1] << 8) + (ram[address+2] << 16);
    }
    
    public int getUnsignedByte(int address) {
        return ram[address] & 0xff;
    }
    
    public int getUnsignedWord(int address) {
        return ((ram[address] & 0xff) + (ram[address+1] << 8)) & 0xffff;
    }
    
    public int getUnsignedLong(int address) {
        return ((ram[address] & 0xff) + ((ram[address+1] << 8) & 0xffff) + (ram[address+2] << 16)) & 0xffffff;
    }
    
}
