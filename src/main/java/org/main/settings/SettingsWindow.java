package org.main.settings;


import org.main.midi.MidiController;
import org.main.util.graphics.components.AbstractComponent;
import org.main.util.graphics.components.menu.CustomDropdown;
import org.main.util.graphics.SettingsDescribeFrame;
import org.main.util.Koordinate;
import org.main.util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SettingsWindow extends JFrame {
    private static SettingsWindow INSTANCE;
    private boolean frameVisible = false;

    private final int with = 700;
    private final int height = 700;


    private final int buttonWidth =  100;
    private final int buttonHeight = 50;





    /*
        MIDI-MK2
     */

    private boolean visMidiMk2 = true;
    private final int buttonMidiMk2X = 20;
    private final int buttonMidiMk2Y = 20;
    private final String buttonMidiMk2Title = "Midi";

    private final int midiX  = 40;
    private final int midiY  = 200;
    private final int midiHeight = 400;
    private final int midiWidth = 650;

    private final int midiBlackKnobX1 = midiX + 147;
    private final int midiBlackKnobY1= midiY + 43;
    private boolean midiBlackKnob1 = false;
    private final SettingsDescribeFrame midiBlackKnob1SettingsFrame = new SettingsDescribeFrame( new Koordinate(midiBlackKnobX1, midiBlackKnobY1), "KNOB 1", "toggle-Slot on/off" ,"Activate Play slot. Switching from normal Sound and On-Shot sound");

    private final int midiBlackKnobX2 =midiBlackKnobX1;
    private final int midiBlackKnobY2= midiY + 96;
    private boolean midiBlackKnob2 = false;
    private final SettingsDescribeFrame midiBlackKnob2SettingsFrame = new SettingsDescribeFrame( new Koordinate(midiBlackKnobX1, midiBlackKnobY1), "KNOB 2", "toggle-Mixer on/off", "Activate mixer for each slot, and switch between mixer and list of sound added to slot. ");


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

        BufferedImage midiMk2Device;
        BufferedImage midiMk2BlackNoble;

        try {
            midiMk2Device = ImageIO.read(new File("src/main/resources/Image/settings_midiMK2.png"));
            midiMk2BlackNoble = ImageIO.read(new File("src/main/resources/Image/settings_midiMK2_blackNobe.png"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CustomDropdown dropdown = new CustomDropdown(MidiController.getInstance(null).getMidiTransmittersNamesList(), new Koordinate(buttonMidiMk2X, 100), new Dimension(100,30 ) );

        dropdown.addClickListener(new AbstractComponent.ComponentClickListener() {
            @Override
            public void onOptionClicked(String  transmitterName) {
                Settings.getInstance().setMidiTransmitterName(transmitterName);
                MidiController.getInstance(null).setTransmitter();
            }
        });



        JLabel label = new JLabel() {



            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setFont(new Font("Arial", Font.PLAIN, 30));


                g2d.setColor(Color.ORANGE);
                g2d.fillRect(buttonMidiMk2X, buttonMidiMk2Y, buttonWidth, buttonHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawString(buttonMidiMk2Title, buttonMidiMk2X + 2 ,buttonMidiMk2Y + g.getFont().getSize());



                //MidiMk2
                if(visMidiMk2){
                    dropdown.draw(g2d);

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



        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                dropdown.clickEvent(e);

                Rectangle buttonMidiMk2 = new Rectangle(buttonMidiMk2X, buttonMidiMk2Y, buttonWidth, buttonHeight);
                if(buttonMidiMk2.contains(mouseX, mouseY)){
                    visMidiMk2 = !visMidiMk2;
                }
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
