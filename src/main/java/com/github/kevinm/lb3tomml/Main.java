package com.github.kevinm.lb3tomml;

import com.github.kevinm.lb3tomml.lb3.Lb3Disassembler;
import com.github.kevinm.lb3tomml.spc.BrrSample;
import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;
import com.github.kevinm.lb3tomml.util.Log;
import com.github.kevinm.lb3tomml.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {

    private static final String[][] OPTIONS = {
            {"--log", "", "Logs the parser output into a .log file."},
            {"--address", "<addr>", "Sets the song data starting address manually."}
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

        run(args);
    }

    private static void run(String[] args) {
        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) {
                break;
            }
            switch (arg) {
                case "--log":
                    Log.enableLog();
                    break;
                case "--address":
                    i++;
                    Lb3Disassembler.SONG_DATA_ADDRESS_OVERRIDE = Integer.parseUnsignedInt(args[i], 16);
                    break;
                default:
                    System.out.printf("Error: option %s was not recognised!%n", arg);
                    System.exit(-1);
            }
        }

        for (; i < args.length; i++) {
            disassemble(args[i]);
        }
    }
    
    private static void disassemble(String fileName) {
        Spc spc;
        Lb3Disassembler disassembler;
        
        try {
            spc = Spc.loadSpc(fileName);
            disassembler = new Lb3Disassembler(spc);
        } catch (IOException | SpcException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
            return;
        }

        String baseName = removeExtension(fileName);
        File mmlFile = new File(baseName + ".txt");
        File logFile = new File(baseName + ".log");

        Log.openLogFile(logFile);
        
        String output = disassembler.disassemble();

        try (FileWriter fileWriter = new FileWriter(mmlFile)) {
            fileWriter.write(output);
            System.out.printf("%s exported without errors.%n", mmlFile.getName());
        } catch (IOException e) {
            error(e);
        }

        String sampleDir = getPath(fileName) + disassembler.getSamplesPath() + Util.SEPARATOR;

        new File(sampleDir).mkdir();
        for (BrrSample sample: disassembler.getSamples()) {
            String sampleFile = sampleDir + sample.getFullName();

            try {
                Files.write(Paths.get(sampleFile), sample.getData());
                System.out.printf("%s exported without errors.%n", sample.getFullName());
            } catch (IOException e) {
                error(e);
            }
        }

        Log.closeLogFile();
    }

    private static void error(Exception e) {
        e.printStackTrace();
        Log.closeLogFile();
        System.exit(-1);
    }

    private static String getPath(String fileName) {
        String fullPath = new File(fileName).getAbsolutePath();
        return fullPath.substring(0, fullPath.lastIndexOf(Util.SEPARATOR)+1);
    }

    private static String removeExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex == -1) {
            return fileName;
        }

        int lastSeparatorIndex = fileName.lastIndexOf(Util.SEPARATOR);
        if (extensionIndex > lastSeparatorIndex) {
            return fileName.substring(0, extensionIndex);
        } else {
            return fileName;
        }
    }

}
