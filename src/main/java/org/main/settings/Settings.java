package org.main.settings;

import org.main.util.Logger;



public class Settings {


    private static  Settings INSTANCE;

    public static Settings getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new Settings();
            Logger.init(INSTANCE.getClass());
        }
        SettingsFrame.getInstance();
        return INSTANCE;
    }

    private Settings() {}


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
