package htw.ava;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by cgeidt on 21.10.2015.
 */
public class Logger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";

    public static void log(String msg){
        System.out.println(ANSI_GREEN + getTime() + ":" + ANSI_RESET + "\n" + msg);
    }

    public static void err(String msg){
        System.err.println(ANSI_RED + getTime() + ": " + ANSI_RESET);
    }

    private static String getTime(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd, hh:mm:ss:S");
        return  ft.format(dNow);
    }
}
