package com.github.kevinm.lb3tomml;

import java.io.IOException;

import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;

public final class Main {
    
    private Main() {
        
    }

    public static void main(String[] args) {
        // Just for quick testing
        args = new String[1];
        args[0] = "./05 Light.txt";
        //
        
        if (args.length < 1) {
            System.out.println("Error: not enough parameters!");
            System.out.println("Usage: java -jar lib3-to-mml.jar <spc file>");
            return;
        }
        
        Spc spc;
        try {
            spc = Spc.loadSpc(args[0]);
        } catch (IOException | SpcException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return;
        }
    }

}
