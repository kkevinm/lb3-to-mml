package com.github.kevinm.lb3tomml.util;

public final class Log {
    
    private static final String TAB = "  ";
    private static boolean logEnabled = true;
    private static int indentation = 0;
    
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
    
    public static final void log(String format, Object... vars) {
        if (logEnabled) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < indentation && indentation >= 0; i++) {
                string.append(TAB);
            }
            string.append(format);
            System.out.println(String.format(string.toString(), vars));
        }
    }
    
    public static final void logIndent(String format, Object... vars) {
        indent();
        log(format, vars);
        unindent();
    }
    
    public static final void newLine() {
        log("");
    }
    
}
