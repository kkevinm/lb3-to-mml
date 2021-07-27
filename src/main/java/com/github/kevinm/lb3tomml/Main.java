package com.github.kevinm.lb3tomml;

import java.io.FileWriter;
import java.io.IOException;

import com.github.kevinm.lb3tomml.lb3.Lb3Disassembler;
import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;
import com.github.kevinm.lb3tomml.util.Log;

public final class Main {

    private static final String[][] OPTIONS = {
            {"--log", "", "Logs the parser output into a .log file."}
    };

    private Main() {}

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: not enough parameters!\n");
            System.out.println("Usage: java -jar lib3-to-mml.jar [options] <spc file>");
            System.out.println("   or: java -jar lib3-to-mml.jar [options] *.spc\n");
            System.out.println("Possible options:");
            for (String[] option: OPTIONS) {
                System.out.printf("  %s\t%s\t%s%n", option[0], option[1], option[2]);
            }
            System.exit(-1);
        }

        int i;
        for (i = 0; i < args.length; i++) {
            if (!args[i].startsWith("--")) {
                break;
            }
            processOption(args[i]);
        }
        
        for (; i < args.length; i++) {
            disassemble(args[i]);
        }
    }

    private static void processOption(String arg) {
        switch (arg) {
            case "--log":
                Log.enableLog();
                break;
            default:
                System.out.printf("Error: option %s was not recognised!%n", arg);
                System.exit(-1);
        }
    }
    
    private static void disassemble(String file) {
        Spc spc;
        Lb3Disassembler disassembler;
        
        try {
            spc = Spc.loadSpc(file);
            disassembler = new Lb3Disassembler(spc);
        } catch (IOException | SpcException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return;
        }
        
        String baseName = removeExtension(file);
        String outputName = baseName + ".txt";
        String logName = baseName + ".log";

        Log.openLogFile(logName);
        
        String output = disassembler.disassemble();
        boolean success;

        try (FileWriter fileWriter = new FileWriter(outputName)) {
            fileWriter.write(output);
            System.out.printf("%s exported without errors.%n", outputName);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        Log.closeLogFile();
    }
    
    private static String removeExtension(String filename) {
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
