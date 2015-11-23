package htw.ava;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Class that helps to log and debug the nodes
 */
public class Logger {


    private boolean debugEnabled;

    /**
     * Creates a logger object
     *
     * @param debugEnabled debug mode on(true) or off(false)
     */
    public Logger(boolean debugEnabled){
        this.debugEnabled = debugEnabled;
    }

    /**
     * Prints an debug message
     *
     * @param msg debug message
     */
    public void debug(String msg){
        if (debugEnabled){
            System.out.println(getTime() + ": "+ msg);
        }
    }

    /**
     * Prints an log message
     *
     * @param msg log message
     */
    public void log(String msg){
        System.out.println(getTime() + ": "+ msg);
    }

    /**
     * Prints an error Message
     *
     * @param msg error message
     */
    public void err(String msg){
        System.err.println(getTime() + ": " + msg);
    }

    /**
     * Returns the time as a String
     *
     * @return time as a String
     */
    private String getTime(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd, hh:mm:ss:S");
        return  ft.format(dNow);
    }
}
