package org.main.audio.plugin;


import com.google.gson.Gson;
import lombok.Getter;
import lombok.SneakyThrows;
import org.main.audio.plugin.model.Plugin;
import org.main.util.JsonLoader;
import org.main.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {

    private static PluginManager INSTANCE;

    private static final String PLUGINS_FOLDER = "src/main/resources/plugins/";



    @Getter
    private List<Plugin> plugins = new ArrayList<>();;



    public static PluginManager getInstance() {

        if(INSTANCE == null) INSTANCE = new PluginManager();
        return INSTANCE;
    }


    private PluginManager() {
        Logger.init(getClass());
        loadPlugins();
    }


    public Plugin getPluginWithPos(int pos) {
        if(plugins.isEmpty()) return null;
        for(Plugin plugin : plugins){
            if(pos == plugin.getPosPad() || pos == plugin.getPosMIDI()){
                return plugin;
            }
        }
        return null;
    }


    public void loadPlugins(){
        File[] files = new File(PLUGINS_FOLDER).listFiles();

        if(files != null  && files.length == 0){
            Logger.info("No plugins found");
            return;
        }


        for(File file : files){
            String json = JsonLoader.loadJsonFromPath(file.getAbsolutePath());
            Plugin plugin = new Gson().fromJson(json, Plugin.class);
            plugin.init();
            Logger.info("Loading plugin: " + file.getAbsolutePath());
            plugins.add(plugin);
        }
    }

}
