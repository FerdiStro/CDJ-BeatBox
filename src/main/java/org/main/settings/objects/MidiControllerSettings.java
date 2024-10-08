package org.main.settings.objects;





public class MidiControllerSettings  extends SettingsObject {

    private static final String PATH = "src/main/resources/configs/midiControllerConfig.json";


    private String MidiControllerName;
    private String MidiControllerNameInit;




    @Override
    public String getPATH() {
        return PATH;
    }



    public void resetMidiControllerName() {
        this.MidiControllerName = MidiControllerNameInit;
    }

    public String getMidiControllerName() {
        return MidiControllerName;
    }
    public void setMidiControllerName(String midiControllerName) {
        this.MidiControllerName = midiControllerName;
    }
}
