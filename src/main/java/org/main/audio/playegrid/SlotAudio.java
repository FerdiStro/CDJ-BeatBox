package org.main.audio.playegrid;


import javax.sound.sampled.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SlotAudio {
    private final File audioFile;
    private final String name;

   public SlotAudio(File audioFile){
       List<String> splitPath = Arrays.stream(audioFile.getPath().split("/")).toList();
       this.name = splitPath.get(splitPath.size()-1);
       this.audioFile = audioFile;
   }



    public void play(){
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip  = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            clip.addLineListener( event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        }catch (Exception e){}

//            new Thread(() -> {
//                try {
//                    test(clip);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();


    }

    public String getName() {
        return name;
    }
}
