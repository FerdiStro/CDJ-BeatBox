package org.main.settings;

import org.main.midi.MidiColorController;
import org.main.util.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
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


    private CDJSettings cdjSettings = new CDJSettings();

    private boolean fullScreen = false;
    private boolean fullScreenBorderLess = false;

    private String midiTransmitterName = "mkII [hw:1,0,0]";
    private List<String> midiTransmittersNamesList = new ArrayList<>();


    public List<String> getMidiTransmittersNamesList(){
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        for(MidiDevice.Info deviceInfo : midiDeviceInfo){
            try {
                MidiDevice midiDevice = MidiSystem.getMidiDevice(deviceInfo);
                if(midiDevice.getMaxReceivers()  >= 0 ){
                    midiTransmittersNamesList.add(deviceInfo.getName());
                }
            } catch (MidiUnavailableException e) {
                Logger.error("Error while reading Midi List in Settings.class");
                throw new RuntimeException(e);
            }
        }
        if(midiTransmittersNamesList.isEmpty()){
            midiTransmittersNamesList.add("NO-Midi");
        }
        return midiTransmittersNamesList;
    }

    public String getMidiTransmitterName(){
        return this.midiTransmitterName;
    }
    public void setMidiTransmitterName(String midiTransmitterName){
        this.midiTransmitterName = midiTransmitterName;
    }



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
