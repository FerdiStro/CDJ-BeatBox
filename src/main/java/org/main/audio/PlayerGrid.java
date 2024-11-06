package org.main.audio;

import lombok.Setter;
import org.main.audio.playegrid.Slot;

public class PlayerGrid {

    @Setter
    private Slot[] slots = {new Slot(), new Slot(),new Slot(),new Slot(),new Slot(),new Slot(),new Slot(),new Slot(), };
    private static PlayerGrid INSTANCE;

    public static PlayerGrid getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PlayerGrid();
        }
        return INSTANCE;
    }

    private PlayerGrid(){}

    public Slot[] getSlots() {
        return slots;
    }
}
