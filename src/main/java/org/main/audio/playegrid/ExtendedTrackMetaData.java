package org.main.audio.playegrid;

import lombok.Getter;
import lombok.Setter;

import org.deepsymmetry.beatlink.data.TrackMetadata;
import org.main.util.graphics.components.button.Button;

@Setter
@Getter
public class ExtendedTrackMetaData    {

    private Button button;

    private String title;
    private int tempo;
    private String artist;
    private String key;


    public ExtendedTrackMetaData(TrackMetadata trackMetadata){
        if(trackMetadata != null){
            this.title  =  trackMetadata.getTitle();
            this.tempo  =  trackMetadata.getTempo();
            this.artist = trackMetadata.getArtist().label;
            this.key  =  trackMetadata.getKey().label;
        }else{
            this.title = "";
            this.tempo = 0;
            this.artist = "";
            this.key = "";
        }

    }









}
