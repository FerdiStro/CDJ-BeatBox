package org.main.audio;

import org.main.audio.playegrid.Slot;

public class PlayerGrid {


   private final Slot[] slots = {new Slot(), new Slot(),new Slot(),new Slot(),new Slot(),new Slot(),new Slot(),new Slot(), };

    public static PlayerGrid getInstance(){
        return new PlayerGrid();
    }
    private PlayerGrid(){

    }





    public Slot[] getSlots() {
        return slots;
    }
}
