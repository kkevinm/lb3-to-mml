package com.github.kevinm.lb3tomml;

import java.io.IOException;

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
        } catch (IOException | SpcException e) {
            e.printStackTrace();
        }
    }

}
