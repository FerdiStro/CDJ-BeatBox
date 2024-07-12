package org.main.audio.playegrid;

public class Slot {
    private boolean active  = false;
    private SlotAudio slotAudio;

    //todo: load other slot audio with library
    public Slot(){
        this.slotAudio = new SlotAudio();
    }


    public void play(){
        slotAudio.play();
    }


    public void toggleActive(){
        this.active = !this.active;
    }

    public boolean isActive() {
        return active;
    }

}
