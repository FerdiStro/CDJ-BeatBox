package org.main.audio.metadata;

import lombok.Getter;
import lombok.Setter;
import org.main.util.graphics.components.Button;

import java.util.List;

@Getter
@Setter
public class SlotAudioMetaData {

    private String key;
    private Integer bpm;
    private String shortName;
    private List<String> recommendationSongs;

    private Button button;



}
