package htw.ava;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by cgeidt on 21.10.2015.
 */
public class Logger {


    private boolean debugEnabled;

    public Logger(boolean debugEnabled){
        this.debugEnabled = debugEnabled;
    }

    public void debug(String msg){
        if (debugEnabled){
            System.out.println(getTime() + ": "+ msg);
        }
    }

    public void log(String msg){
        System.out.println(getTime() + ": "+ msg);
    }

    public void err(String msg){
        System.err.println(getTime() + ": " + msg);
    }

    private String getTime(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd, hh:mm:ss:S");
        return  ft.format(dNow);
    }
}
