package org.main.settings;

import lombok.Getter;
import lombok.Setter;
import org.main.BeatBoxWindow;
import org.main.midi.MidiController;
import org.main.settings.objects.BeatBoxWindowSettings;
import org.main.settings.objects.CDJSettings;
import org.main.settings.objects.MidiControllerSettings;
import org.main.settings.objects.AbstractSettings;
import org.main.util.Logger;

import java.util.ArrayList;
import java.util.List;


public class Settings {



    private static Settings INSTANCE;

    public static Settings getInstance(){
        if (INSTANCE == null) {
            Settings.INSTANCE = new Settings();
        }
        return Settings.INSTANCE;
    }

    private Settings() {
        Logger.init(Settings.class);
    }


     /*
        Load-Settings
    */
     List<AbstractSettings> settingsObjects = new ArrayList<>();

    MidiControllerSettings midiControllerSettings;

    @Getter
    CDJSettings cdjSettings;

    @Getter
    BeatBoxWindowSettings beatBoxWindowSettings;

    public void loadSettings() {
        Logger.info("Start Loading all settings");

        //Midi settings
        midiControllerSettings = getSettingsObject(new MidiControllerSettings());
        MidiController.getInstance(midiControllerSettings);
        //CDJ settings
        cdjSettings = getSettingsObject(new CDJSettings());
        //Window settings
        beatBoxWindowSettings = getSettingsObject(new BeatBoxWindowSettings());

    }

    private   <T extends AbstractSettings> T getSettingsObject(T settingsObject){
        T load = settingsObject.load(settingsObject);
        settingsObjects.add(load);
        Logger.debug("Loaded settings object: " + settingsObject);
        return load;
    }

    /*
         Save-Settings
     */
    public void saveSettings() {
        for(AbstractSettings settingsObject : settingsObjects){
            settingsObject.save(midiControllerSettings);
        }
    }

    public void setMidiTransmitterName(String midiTransmitterName){
        midiControllerSettings.setMidiControllerName(midiTransmitterName);
    }


















    private boolean visible = false;








    public void toggleVisible() {
        this.visible = !this.visible;
        SettingsWindow.getInstance().setVisible(this.visible);
    }

    public void update(){
        SettingsWindow.getInstance().update();
    }

















}
