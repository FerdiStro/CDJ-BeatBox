package org.main.settings;


import org.main.midi.MidiController;
import org.main.util.Coordinates;
import org.main.util.graphics.components.AbstractComponent;
import org.main.util.graphics.components.button.OnEvent;
import org.main.util.graphics.components.menu.CustomDropdown;
import org.main.util.graphics.SettingsDescribeFrame;
import org.main.util.Logger;
import org.main.util.graphics.components.button.Button;
import org.main.util.graphics.components.menu.MultipleComponentMenuHorizontal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsWindow extends JFrame {
    private static SettingsWindow INSTANCE;
    private boolean frameVisible = false;

    private final int with = 700;
    private final int height = 700;

    private final Font font =  new Font("Arial", Font.PLAIN, 30);

    private final int buttonWidth =  100;
    private final int buttonHeight = 50;





    /*
        MIDI-MK2
     */

    private final Button midiDisableButton =  new Button(new Coordinates(20, getY() + buttonHeight * 3), new Font("Arial", Font.PLAIN, 16),"Disable");

    private final int midiX  = 40;
    private final int midiY  = 200;
    private final int midiHeight = 400;
    private final int midiWidth = 650;

    private final int midiBlackKnobX1 = midiX + 147;
    private final int midiBlackKnobY1= midiY + 43;
    private boolean midiBlackKnob1 = false;
    private final SettingsDescribeFrame midiBlackKnob1SettingsFrame = new SettingsDescribeFrame( new Coordinates(midiBlackKnobX1, midiBlackKnobY1), "KNOB 1", "toggle-Slot on/off" ,"Activate Play slot. Switching from normal Sound and On-Shot sound");

    private final int midiBlackKnobX2 =midiBlackKnobX1;
    private final int midiBlackKnobY2= midiY + 96;
    private boolean midiBlackKnob2 = false;
    private final SettingsDescribeFrame midiBlackKnob2SettingsFrame = new SettingsDescribeFrame( new Coordinates(midiBlackKnobX2, midiBlackKnobY2), "KNOB 2", "toggle-Mixer on/off", "Activate mixer for each slot, and switch between mixer and list of sound added to slot. ");


    protected static SettingsWindow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SettingsWindow();
        }
        return INSTANCE;
    }

    public void update(){
        repaint();
    }


    private SettingsWindow() {
        Logger.init(this.getClass());
        setTitle("Settings");
        setVisible(frameVisible);
        setSize(with, height);
        setResizable(false);

        midiDisableButton.setStateButton();
        midiDisableButton.setToggle(Settings.getInstance().getMidiControllerSettings().isMidiControllerDisable());
        midiDisableButton.setToggleColorFullButton(true);
        midiDisableButton.setToggleColor(Color.red);

        Button midiButton = new Button(new Coordinates(0, 0), new Dimension(buttonWidth, buttonHeight), "Midi");
        midiButton.setStateButton();
        midiButton.setToggle(true);
        midiButton.setBackgroundColor(new Color(236, 236, 236, 255));
        midiButton.setToggleColorFullButton(true);


        Button plugin = new Button(new Coordinates(0, 0), new Dimension(buttonWidth + 10, buttonHeight), "Plugin");
        plugin.setStateButton();
        plugin.setBackgroundColor(new Color(236, 236, 236, 255));
        plugin.setToggleColorFullButton(true);



        List<AbstractComponent> buttonList = new ArrayList<>();
        buttonList.add(midiButton);
        buttonList.add(plugin);

        MultipleComponentMenuHorizontal multipleComponentMenuHorizontal =  new MultipleComponentMenuHorizontal(new Coordinates(0, 0), new Dimension(with, 80), buttonList);

        midiButton.addClickListener( ()-> {
            plugin.setToggle(false);
            repaint();
        });
        plugin.addClickListener( ()-> {
            midiButton.setToggle(false);
            repaint();
        });

        BufferedImage midiMk2Device;
        BufferedImage midiMk2BlackNoble;

        try {
            midiMk2Device = ImageIO.read(new File("src/main/resources/Image/settings_midiMK2.png"));
            midiMk2BlackNoble = ImageIO.read(new File("src/main/resources/Image/settings_midiMK2_blackNobe.png"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        CustomDropdown dropdown = new CustomDropdown(MidiController.getInstance(null).getMidiTransmittersNamesList(), new Coordinates(midiButton.getX(), 100), new Dimension(100,30 ) );





        JLabel label = new JLabel() {



            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setFont(font);

//                midiButton.draw(g2d);

                multipleComponentMenuHorizontal.draw(g2d);




                //MidiMk2
                if(midiButton.isToggle()){
                    dropdown.draw(g2d);

                    midiDisableButton.draw(g2d);

                    g2d.drawImage(midiMk2Device, midiX, midiY, midiWidth, midiHeight, null);

                    if(midiBlackKnob1){
                        g2d.drawImage(midiMk2BlackNoble, midiBlackKnobX1, midiBlackKnobY1, 35, 35, null);
                        midiBlackKnob1SettingsFrame.draw(g2d);

                    }
                    if(midiBlackKnob2){
                        g2d.drawImage(midiMk2BlackNoble, midiBlackKnobX2, midiBlackKnobY2, 35, 35, null);
                        midiBlackKnob2SettingsFrame.draw(g2d);
                    }
                }


            }


        };


        dropdown.addClickListener(new OnEvent() {
            @Override
            public void onEvent() {

            }
            @Override
            public void onEvent(String  transmitterName) {
                Settings.getInstance().setMidiTransmitterName(transmitterName);
                MidiController.getInstance(null).setTransmitter();
            }
        });


        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                multipleComponentMenuHorizontal.clickEvent(e);



                midiDisableButton.clickMouse(e, () -> {
                    Settings.getInstance().getMidiControllerSettings().setMidiControllerDisable(midiDisableButton.isToggle());
                    repaint();
                });


                dropdown.clickEvent(e);


            }

        });


        label.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();


                Rectangle rectangleKnob1 = new Rectangle(midiBlackKnobX1, midiBlackKnobY1, 40, 40);
                midiBlackKnob1 = rectangleKnob1.contains(mouseX, mouseY);

                Rectangle rectangleKnob2 = new Rectangle(midiBlackKnobX2, midiBlackKnobY2, 80, 80);
                midiBlackKnob2 = rectangleKnob2.contains(mouseX, mouseY);


                midiButton.hoverMouse(e, Color.ORANGE, () -> {
                    repaint();
                });

            }
        });



        add(label);

        /*
            Save settings when closing window
         */
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Settings.getInstance().saveSettings();
                Settings.getInstance().toggleVisible();
            }
        });
    }




    public void toggleFrameVisible() {
        this.frameVisible = !this.frameVisible;
        this.setVisible(frameVisible);
    }


}
