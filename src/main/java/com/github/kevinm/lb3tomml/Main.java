package com.github.kevinm.lb3tomml;

import java.io.IOException;

import com.github.kevinm.lb3tomml.spc.Aram;
import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;

public final class Main {
    
    private Main() {
        
    }

    public static void main(String[] args) {
        String file = "./05 Light.spc";
        try {
            Spc spc = Spc.loadSpc(file);
            Spc.Id666Tag tag = spc.id666Tag.get();
            System.out.println(tag.gameTitle);
            System.out.println(tag.artistName);
            System.out.println(tag.dumpDate);
            Aram ram = Aram.fromSpc(spc);
            System.out.println(ram.getSignedByte(0));
            System.out.println(ram.getSignedWord(0));
            System.out.println(ram.getSignedLong(0));
            System.out.println(ram.getUnsignedByte(0));
            System.out.println(ram.getUnsignedWord(0));
            System.out.println(ram.getUnsignedLong(0));
        } catch (IOException | SpcException e) {
            e.printStackTrace();
        }
    }

}
