package org.main.audio.library;

import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryKind {

    @Getter
    private String name;
    @Getter
    private TYPE type;
    @Getter
    private boolean selected;

    @Getter
    private JScrollPane  tree;
    @Getter
    @Setter
    private String selectedTitel;
    @Setter
    @Getter
    private List<String> filePaths = new ArrayList<>();



    public LibraryKind(String name, boolean selected, JScrollPane tree, TYPE type) {
        this.name = name;
        this.selected = selected;
        this.tree = tree;
        this.type = type;
    }


    public void setSelected(boolean selected) {
        this.tree.setVisible(selected);
        this.selected = selected;
    }

    private Clip preListenClip;
    private final Map<String, File> preListenCache = new HashMap<>();

    public void preListen(){
        for(String path : filePaths ){
            if(path.contains(this.selectedTitel)){
                try {

                    File audioFile = preListenCache.get(path);
                    if(audioFile == null){
                        audioFile = new File(path);
                        this.preListenCache.put(path, audioFile);
                    }
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    preListenClip = AudioSystem.getClip();
                    preListenClip.open(audioStream);
                    preListenClip.start();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
    public void stopRreListen(){
        if(this.preListenClip != null){
            this.preListenClip.stop();
        }
    }


}
