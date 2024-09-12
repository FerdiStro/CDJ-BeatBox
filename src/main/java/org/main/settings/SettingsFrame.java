package org.main.settings;

import javax.swing.*;

public class SettingsFrame extends JFrame {
    private static SettingsFrame INSTANCE;
    private boolean frameVisible =  false;

    private final int with = 400 ;
    private final int height = 400 ;


    public static SettingsFrame getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SettingsFrame();
        }
        return INSTANCE;
    }

    private SettingsFrame() {
        this.setTitle("Settings");
        this.setVisible(frameVisible);
        this.setSize(with, height);
    }


    public void toggleFrameVisible() {
        this.setVisible(!frameVisible);
    }


}
