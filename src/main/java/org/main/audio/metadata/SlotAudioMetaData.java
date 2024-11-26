package org.main.audio.metadata;

import lombok.Getter;
import lombok.Setter;
import org.main.audio.pattern.METADATA_TYPE;
import org.main.util.graphics.components.button.Button;

import java.util.List;

@Getter
@Setter
public class SlotAudioMetaData {

    private String key;
    private Integer bpm;
    private String shortName;
    private String longName;
    private List<String> recommendationSongs;

    private METADATA_TYPE type;

    private transient Button button;



}
