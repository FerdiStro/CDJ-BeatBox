package org.main.settings.objects;

import com.google.gson.Gson;
import org.main.util.JsonLoader;
import org.main.util.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public abstract class AbstractSettings {

    public abstract String getPATH();

    public void reset(){
        //ignore if no changes in Settings
    }


    private static final Gson gson = new Gson();

    public <T> T load(T t) {
        String path = getPATH();
        String jsonConfig = JsonLoader.loadJsonFromPath(path);
        Logger.debug("Loading settings from " + path + ". Class: " + this.getClass().getName());
        return  JsonLoader.mapJSON(t, jsonConfig);
    }

    public <T> void save(T t){
        String json = gson.toJson(t, t.getClass());
        try {
            Files.write( Paths.get(getPATH()), json.getBytes());
            Logger.debug("Saved settings to: " + getPATH() + ". Class: " + this.getClass().getName());
        } catch (IOException e) {
            Logger.error("Error saving settings to " + getPATH() + ". Class: " + this.getClass().getName());
        }
    }


}
