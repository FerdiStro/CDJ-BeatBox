package org.main.settings;

import org.main.util.Logger;



public class Settings {


    private static  Settings INSTANCE;

    public static Settings getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new Settings();
            Logger.init(INSTANCE.getClass());
        }
        return INSTANCE;
    }

    private Settings() {}


    private CDJSettings cdjSettings = new CDJSettings();



    private boolean fullScreen = false;
    private boolean fullScreenBorderLess = false;





    public CDJSettings getCdjSettings() {
        return cdjSettings;
    }


    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public boolean isFullScreenBorderLess() {
        return fullScreenBorderLess;
    }

    public void setFullScreenBorderLess(boolean fullScreenBorderLess) {
        this.fullScreenBorderLess = fullScreenBorderLess;
    }
}
