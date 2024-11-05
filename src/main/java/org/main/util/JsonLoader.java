package org.main.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonLoader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T mapFromPath(T t, String path) {
       return mapJSON(t, loadJsonFromPath(path));
    }


    public static String loadJsonFromPath(String path) {
        StringBuilder json = new StringBuilder();
        File file = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines().forEach(json::append);
        } catch (FileNotFoundException e) {
            Logger.error("File not found: " + path);
            return null;
        }
        return json.toString();
    }

    public static <T> T mapJSON(T t, String json) {
        return (T) gson.fromJson(json,t.getClass() );
    }

    public static void saveJsonToFile(String path, String json) {
        try {
            Files.write( Paths.get(path), json.getBytes());
            Logger.debug("Saved settings to: " + path );
        } catch (IOException e) {
            Logger.error("Error saving settings to " + path);
        }
    }


}
