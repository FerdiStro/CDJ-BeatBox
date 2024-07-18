package org.main.audio.library;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryKind {

    private String name;
    private TYPE type;


    private boolean selected;

    private JScrollPane  tree;
    private String selectedTitel;
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



    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public JScrollPane getTree() {
        return tree;
    }

    public String getSelectedTitel() {
        return selectedTitel;
    }

    public void setSelectedTitel(String selectedTitel) {
        this.selectedTitel = selectedTitel;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public TYPE getType() {
        return type;
    }
}
