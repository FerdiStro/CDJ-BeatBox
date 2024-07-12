package org.main.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class LoadLibrary {

    public static LoadLibrary getInstance(){
        return new LoadLibrary();
    }

    private Clip clip;
    private File audioFile;
    private AudioInputStream audioStream = null;

    //Todo:  Write Library Loader
    private LoadLibrary(){
        try {
             audioFile = new File("src/main/resources/kick/Mzperx_KICK_04.wav");

        }catch (Exception e){}

    }

    public AudioInputStream getAudioStream(){
        if(audioStream!= null){
            return audioStream;
        }
        return null;
    }





    public Clip getClip(){
        try {
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip  = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
        }
        return this.clip;
    }


}
