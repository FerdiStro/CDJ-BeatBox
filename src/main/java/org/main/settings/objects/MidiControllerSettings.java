package org.main.settings.objects;


import lombok.Getter;
import lombok.Setter;
import org.main.midi.MidiController;

@Getter
@Setter
public class MidiControllerSettings  extends AbstractSettings {

    private static final String PATH = "src/main/resources/configs/midiControllerConfig.json";


    private String MidiControllerName;
    private String MidiControllerNameInit;

    private boolean MidiControllerDisable;
    private boolean MidiControllerDisableInit;


    public void setMidiControllerDisable(boolean disable) {
        this.MidiControllerDisable = disable;
        if(!disable){
            MidiController.getInstance(null).setTransmitter();

        }
    }


    @Override
    public String getPATH() {
        return PATH;
    }

    @Override
    public void reset() {
        this.MidiControllerName = MidiControllerNameInit;
    }

}
