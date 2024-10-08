package org.main.settings.objects;


import lombok.Getter;
import lombok.Setter;


public class MidiControllerSettings  extends AbstractSettings {

    private static final String PATH = "src/main/resources/configs/midiControllerConfig.json";

    @Getter
    @Setter
    private String MidiControllerName;
    private String MidiControllerNameInit;




    @Override
    public String getPATH() {
        return PATH;
    }

    @Override
    public void reset() {
        this.MidiControllerName = MidiControllerNameInit;
    }

}
