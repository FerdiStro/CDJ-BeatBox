package org.main.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class JsonLoader {
    private static final Gson gson = new Gson();

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
}
