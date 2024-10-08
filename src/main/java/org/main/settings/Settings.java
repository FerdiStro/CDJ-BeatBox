package org.main.settings;

import org.main.midi.MidiController;
import org.main.settings.objects.MidiControllerSettings;
import org.main.settings.objects.SettingsObject;
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

    MidiControllerSettings midiControllerSettings;

    List<SettingsObject> settingsObjects = new ArrayList<>();

    public void loadSettings() {
        Logger.info("Start Loading all settings");

        //Midi Settings
        midiControllerSettings = getSettingsObject(new MidiControllerSettings());
        MidiController.getInstance(midiControllerSettings);

    }

    private   <T extends  SettingsObject> T getSettingsObject(T settingsObject){
        T load = settingsObject.load(settingsObject);
        settingsObjects.add(load);
        return load;
    }

    /*
         Save-Settings
     */
    public void saveSettings() {
        for(SettingsObject settingsObject : settingsObjects){
            settingsObject.save(midiControllerSettings);
        }
    }

    public void setMidiTransmitterName(String midiTransmitterName){
        midiControllerSettings.setMidiControllerName(midiTransmitterName);
    }




    private CDJSettings cdjSettings = new CDJSettings();

    private boolean fullScreen = false;
    private boolean fullScreenBorderLess = false;
















    private boolean visible = false;

    public CDJSettings getCdjSettings() {
        return cdjSettings;
    }



    public boolean isFullScreen() {
        return fullScreen;
    }

    public void toggleVisible() {
        this.visible = !this.visible;
        SettingsFrame.getInstance().setVisible(this.visible);
    }

    public void update(){
        SettingsFrame.getInstance().update();
    }


    public boolean isFullScreenBorderLess() {
        return fullScreenBorderLess;
    }

    public void setFullScreenBorderLess(boolean fullScreenBorderLess) {
        this.fullScreenBorderLess = fullScreenBorderLess;
    }








}
