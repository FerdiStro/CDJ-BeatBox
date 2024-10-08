package org.main.settings.objects;

import com.google.gson.Gson;
import org.main.util.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public abstract class AbstractSettings {

    public abstract String getPATH();
    public abstract void reset();

    private static final Gson gson = new Gson();

    public <T> T load(T t) {
        String path = getPATH();
        String jsonConfig = getJsonConfig(path);
        Logger.debug("Loading settings from " + path + ". Class: " + this.getClass().getName());
        return (T) gson.fromJson(jsonConfig,t.getClass() );
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

    private String getJsonConfig(String path){
        StringBuilder json = new StringBuilder();
        File file = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines().forEach(json::append);
        } catch (FileNotFoundException e) {
            Logger.error("File not found: " + path + ". Class: " + this.getClass().getName());
            return null;
        }
        return json.toString();
    }

}
