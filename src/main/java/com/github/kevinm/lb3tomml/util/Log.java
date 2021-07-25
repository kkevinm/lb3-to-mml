package com.github.kevinm.lb3tomml.util;

import java.io.FileWriter;
import java.io.IOException;

public final class Log {
    
    private static final String TAB = "  ";
    private static boolean logEnabled = true;
    private static int indentation = 0;
    private static FileWriter logFile;
    
    private Log() {
        
    }
    
    public static void enableLog() {
        logEnabled = true;
    }
    
    public static void disableLog() {
        logEnabled = false;
    }
    
    public static void indent() {
        indentation++;
    }
    
    public static void unindent() {
        indentation--;
    }
    
    public static void openLogFile(String fileName) {
        closeLogFile();
        
        try {
            logFile = new FileWriter(fileName);
        } catch (IOException e) {
            logFile = null;
            e.printStackTrace();
        }
    }
    
    public static void closeLogFile() {
        if (logFile != null) {
            try {
                logFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logFile = null;
        }
    }
    
    public static final void log(String format, Object... vars) {
        if (logEnabled) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < indentation && indentation >= 0; i++) {
                string.append(TAB);
            }
            string.append(format);
            String output = String.format(string.toString(), vars);
            if (logFile == null) {
                System.out.println(output);
            } else {
                try {
                    logFile.write(output);
                    logFile.write('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static final void logIndent(String format, Object... vars) {
        indent();
        log(format, vars);
        unindent();
    }
    
    public static final void logUnindent(String format, Object... vars) {
        unindent();
        log(format, vars);
        indent();
    }
    
    public static final void newLine() {
        log("");
    }
    
}
