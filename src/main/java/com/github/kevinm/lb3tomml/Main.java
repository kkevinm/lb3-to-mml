package com.github.kevinm.lb3tomml;

import java.io.IOException;

import com.github.kevinm.lb3tomml.lb3.Lb3Disassembler;
import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;

public final class Main {
    
    private Main() {
        
    }

    public static void main(String[] args) {
        // Just for quick testing
        // TODO: remove
        args = new String[] {"./05 Light.spc"};
        //
        
        if (args.length < 1) {
            System.out.println("Error: not enough parameters!");
            System.out.println("Usage: java -jar lib3-to-mml.jar <spc file>");
            return;
        }
        
        Spc spc;
        Lb3Disassembler disassembler;
        String output;
        try {
            spc = Spc.loadSpc(args[0]);
            output = removeExtension(args[0]) + ".txt";
            disassembler = new Lb3Disassembler(spc);
        } catch (IOException | SpcException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return;
        }
        
        disassembler.disassemble();
    }
    
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex == -1) {
            return filename;
        }

        String separator = System.getProperty("file.separator");
        int lastSeparatorIndex = filename.lastIndexOf(separator);
        if (extensionIndex > lastSeparatorIndex) {
            return filename.substring(0, extensionIndex);
        } else {
            return filename;
        }
    }

}
