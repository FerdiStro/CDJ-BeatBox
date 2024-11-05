package org.main.audio.pattern;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import org.main.util.JsonLoader;
import org.main.util.Logger;



public class PatternManager {

    private static PatternManager INSTANCE;

    private static final String PATH = "src/main/resources/audioMetaData/patternData.json";

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



    public void savePattern(PlayPattern playerPattern){
        String jsonPattern = gson.toJson(playerPattern, PlayPattern.class);
        Logger.info("");

        JsonLoader.saveJsonToFile(PATH, jsonPattern);


    }










}
