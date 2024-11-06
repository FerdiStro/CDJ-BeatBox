package org.main.audio.pattern;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import org.main.audio.PlayerGrid;
import org.main.audio.playegrid.Slot;
import org.main.util.JsonLoader;
import org.main.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PatternManager {

    private static PatternManager INSTANCE;

    private static final String PATH = "src/main/resources/audioMetaData/patterns/pattern_";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static PatternManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PatternManager();
        }
        return INSTANCE;
    }

    private PatternManager(){
        Logger.init(getClass());


    }

    private Map<String, PlayPattern> slotCache = new HashMap<>();

    public void loadPattern(PlayerGrid grid,  String name){
        PlayPattern cachedPattern = slotCache
                .computeIfAbsent(name, this::findPatternByName);

        cachedPattern.refreshSlots();
        ArrayList<Slot> slotList = cachedPattern.getSlotList();

        if(grid.getSlots().length > slotList.size()){
            //todo: handle bigger patterns
            Logger.notImplemented("Pattern bigger than  existed Slots. Feature coming soon");
        }else{
            Slot[] newArray =  new Slot[grid.getSlots().length];

            for(int i = 0;  i != grid.getSlots().length; i ++){
                newArray[i] = slotList.get(i);
            }
            slotCache.put(name, new PlayPattern(name, newArray));
            grid.setSlots(newArray);
        }
    }

    private PlayPattern findPatternByName(String name){
        String jsonPattern = JsonLoader.loadJsonFromPath(PATH + name + ".json");
        return  gson.fromJson(jsonPattern, PlayPattern.class);
    }

    public void savePattern(PlayPattern playerPattern){
        slotCache.put(playerPattern.getName(), playerPattern);
        String jsonPattern = gson.toJson(playerPattern, PlayPattern.class);
        JsonLoader.saveJsonToFile(PATH+playerPattern.getName()+".json", jsonPattern);
    }
}