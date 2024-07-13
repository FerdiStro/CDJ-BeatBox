package org.main.audio.playegrid;

import java.util.ArrayList;
import java.util.List;

public class Slot {
    private List<SlotAudio> selectedSounds = new ArrayList<>();

    //todo: load other slot audio with library


    public void addSelectedSound(SlotAudio audio){
        boolean contains  = false;
        for(SlotAudio name : selectedSounds){
            if(name.getName().equals(audio.getName())){
                contains = true;
            }
        }
        if(!contains){
            this.selectedSounds.add(audio);
        }
    }



    public void play(){
        for(SlotAudio audio: selectedSounds){
            audio.play();
        }

    }

    public List<SlotAudio> getSelectedSounds() {
        return selectedSounds;
    }

    public boolean isActive() {
        return !selectedSounds.isEmpty();
    }

}
