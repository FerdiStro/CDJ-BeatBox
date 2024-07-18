package org.main.midi;


import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class MidiColorController {

    private static MidiColorController INSTANCE;

    public static MidiColorController getInstance(){
        if(INSTANCE==null){
            INSTANCE =  new MidiColorController();
        }
        return INSTANCE;
    }

    public CompletableFuture<Void> switchColorAsync(int pad, String color) {
        return CompletableFuture.runAsync(() -> {
            try {
                String command = "src/main/java/org/main/midi/color/binSendToMiniLabMk2";

                ProcessBuilder processBuilder = new ProcessBuilder(command, String.valueOf(pad), color);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                int exitCode = process.waitFor();
                System.out.println("Exited with code: " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private MidiColorController(){
    }


}
