package org.main.audio.metadata;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.main.util.JsonLoader;
import org.main.util.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaDataFinder {


    private static MetaDataFinder INSTANCE;
    private static final String PATH = "src/main/resources/audioMetaData/metadata.json";
    private Map<String, SlotAudioMetaData> metaDataList =  new HashMap<>();

    public static MetaDataFinder getInstance() {
        if (INSTANCE == null) {
            MetaDataFinder.INSTANCE = new MetaDataFinder();
        }
        return MetaDataFinder.INSTANCE;
    }

    private MetaDataFinder() {
        Logger.init(getClass());
        if(metaDataList.isEmpty()){

            metaDataList = new Gson().fromJson(JsonLoader.loadJsonFromPath(PATH), new TypeToken<Map<String, SlotAudioMetaData>>(){}.getType());

            assert metaDataList != null;
            Logger.debug("Loaded " + metaDataList.size() + " metadata-objects");

            updateRecommendationsList();
        }
    }

    public SlotAudioMetaData getMetaData(String audioName) {
        return this.metaDataList.get(audioName);
    }

    /*
        Recommendations
     */
    private Map<String, List<String>> recommendationsList = new HashMap<>();



    public List<String> findRecommendations(String songName){
        if(recommendationsList.isEmpty()){
            updateRecommendationsList();
        }
        return  recommendationsList.get(songName);
    }


    public void updateRecommendationsList(){
        if(!metaDataList.isEmpty()){

            for(String audioName : metaDataList.keySet()){
                SlotAudioMetaData metaData = metaDataList.get(audioName);
                for(String metaRec : metaData.getRecommendationSongs()){
                    List<String> recs = recommendationsList.get(metaRec);
                    if(recs == null){
                        recs = new ArrayList<>();
                    }
                    if(!recs.contains(audioName)){
                        recs.add(audioName);
                        recommendationsList.put(metaRec, recs );
                    }
                }
            }
            Logger.debug("Recommendations list updated in class " + this.getClass().getName());
            return;
        }
        Logger.error("No Metadata found");
    }







}
