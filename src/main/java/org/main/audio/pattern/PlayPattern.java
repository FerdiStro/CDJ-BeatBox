package org.main.audio.pattern;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.main.audio.playegrid.Slot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayPattern {

    @Getter
    private final  String name;

    @Getter
    @Setter
    private ArrayList<Slot> slotList;


    public PlayPattern(String name, Slot[] slots) {;
        this.name = name;
        ArrayList<Slot> arrayList =  new ArrayList<>();
        List<Slot> slotList = new ArrayList<>(Arrays.stream(slots).toList());
        for(Slot slot : slotList){
            Gson gson = new Gson();
            Slot cloned = gson.fromJson(gson.toJson(slot), Slot.class);
            cloned.refreshSelectedSounds();
            arrayList.add(cloned);
        }
        this.slotList = arrayList;
    }

    public void refreshSlots(){
        for(Slot slot : slotList){
            slot.refreshSelectedSounds();
        }
    }

}
