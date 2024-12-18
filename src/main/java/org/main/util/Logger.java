package org.main.util;

import lombok.Getter;
import org.apache.commons.math3.analysis.function.Abs;
import org.main.util.graphics.components.AbstractComponent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {


    private static final Boolean debug =  true;

    public static final Boolean debugGraphics = false;



    public static void drawMiddle(Graphics2D g, AbstractComponent component, Color color){
        if(Logger.debugGraphics){
            Color before = g.getColor();
            g.setColor(color);
            g.drawLine(component.getX() + component.getDimension().width /2, component.getY(), component.getX()  + component.getDimension().width /2, component.getY() +  component.getDimension().height);
            g.drawLine(component.getX(), component.getY() +  component.getDimension().height /2 , component.getX() +  component.getDimension().width, component.getY() + component.getDimension().height / 2);
            g.setColor(before);
        }

    }

    public static void init(Class initClas){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

        System.out.println(" > Init : " + initClas.getName() +" t:"+  sdf.format(new Date()));
    }

    public static void notImplemented(String message){
        System.out.print((char)27 + "[33m Not Implemented :");
        System.out.println((char)27 + "[39m " + message);
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
