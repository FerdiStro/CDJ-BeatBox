package org.main.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static final Boolean debug =  true;


    public static void init(Class initClas){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

        System.out.println(" > Init : " + initClas.getName() +" t:"+  sdf.format(new Date()));
    }

    public static void info(String message){
        System.out.print((char)27 + "[33m Info:");
        System.out.println((char)27 + "[39m " + message);
    }
    public static void error(String message){
        System.out.print((char)27 + "[31m Error:");
        System.out.println((char)27 + "[39m " + message);
    }
    public static void success(String message){
        System.out.print((char)27 + "[32m Success:");
        System.out.println((char)27 + "[39m " + message);
    }


    public static void debug(String message){
        if(debug){
            System.out.print((char)27 + "[31m DEBUG:");
            System.out.println((char)27 + "[39m " + message);
        }
    }

}
