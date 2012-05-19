/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author gabrielpoca
 */
public class Debug {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void dump(String s) {
	System.out.println(ANSI_BLACK+"DEBG:: " + s);
    }
    
    public static void dumpPackageSent(String s) {
	System.out.println(ANSI_BLUE+"SENT:: " + s);
    }
    
    public static void dumpPackageReceived(String s) {
	System.out.println(ANSI_GREEN+"RECE:: " + s);
    }  
    
    public static void dumpException(String s) {
	System.out.println(ANSI_RED+"EXCE:: " + s);
    }
}
