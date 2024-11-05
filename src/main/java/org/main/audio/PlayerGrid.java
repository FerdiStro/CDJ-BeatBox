package org.main.audio;


import org.main.audio.pattern.PlayPattern;
import org.main.audio.playegrid.Slot;


public class PlayerGrid {


   private Slot[] slots = {new Slot(), new Slot(),new Slot(),new Slot(),new Slot(),new Slot(),new Slot(),new Slot(), };
    private static PlayerGrid INSTANCE;

    public static PlayerGrid getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PlayerGrid();
        }
        return INSTANCE;
    }
    public PlayerGrid(){

    }


    public void loadPattern(PlayPattern pattern){
        slots = pattern.getGrid().getSlots();
    }





    public Slot[] getSlots() {
        return slots;
    }
}
