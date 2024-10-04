package org.main;

import org.deepsymmetry.beatlink.data.*;
import org.main.audio.PlayShots;
import org.main.audio.SHOT_TYPE;
import org.main.audio.library.LibraryKind;
import org.main.audio.library.LoadLibrary;
import org.main.audio.PlayerGrid;
import org.main.audio.playegrid.Slot;
import org.main.audio.playegrid.SlotAudio;
import org.main.midi.MidiColorController;
import org.main.settings.Settings;
import org.main.settings.SettingsFrame;
import org.main.util.Koordinate;
import org.main.util.Logger;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Frame extends JFrame {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private int fontSize = 0;

    private static int screenWidth = 1200;
    private static int screenHeight = 700;

    private int xBeat;
    private int yBeat;
    private int counterBeat = 0;
    private int sizeBeat;

    private int masterX;
    private int masterY;

    private double masterTempo = 0.0;

    private int setUpStringX;

    private Map<Integer, TrackMetadata> metaData;
    private final int metaDataX = 15;
    private final int metaDataY = 50;
    private final int metaDataHeight = 200;
    private final int metaDataWidth = 400;

    private int playGridX;
    private int playGridY;
    private int playGridSize;
    private int playerGridCounterBeat = 0;
    private boolean playOnBeat = false;
    private int checkBeat = 0;

    private int libraryX;
    private int libraryButtonWidth;
    private int libraryButtonHeight;
    private int libraryStringMaxWidth;
    private int libraryY = playGridY;
    private int libraryWidth;
    private int libraryHeight;


    private int controlPanelX;
    private int controlPanelY;
    private int controlPanelWith;
    private int controlPanelHeight;

    private int toggleSwitchX;
    private int toggleSwitchY;
    private int toggleWith;
    private int toggleHeight;
    private boolean toggleSwitchActive = false;


    private boolean toggleVolumeSlider = false;
    private HashMap<String, Koordinate> kordSlotList = new HashMap<>();
    private HashMap<String, Koordinate> kordSlotMarkList = new HashMap<>();
    private HashMap<String, Koordinate> kordSlotRemoveList = new HashMap<>();


    private int settingsSize;


    private int settingsButtonX = xBeat;
    private int settingsButtonY = yBeat + settingsSize;


    private List<PlayShots> shotList = new ArrayList<>();


    private int masterDevicdId = 0;

    private final JLabel jLabel;

    private static Frame INSTANCE;

    private final LoadLibrary soundLibrary = LoadLibrary.getInstance();

    private final MidiColorController midiColorController = MidiColorController.getInstance();

    public synchronized static Frame getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Frame();
            Logger.init(INSTANCE.getClass());
        }
        return INSTANCE;
    }

    private boolean setupString = true;


    private Frame() {
        setLayout(null);


        PlayerGrid playerGrid = PlayerGrid.getInstance();
        for (LibraryKind libraryKind : soundLibrary.getFolderView()) {
            JScrollPane tree = libraryKind.getTree();
            tree.setBounds(libraryX, libraryY, libraryWidth, libraryHeight);
            add(tree);
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                Frame.getInstance().resizeFrame(size);
            }
        });

        jLabel = new JLabel() {

            boolean resizeFirst = true;

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Font font = new Font("Arial", Font.PLAIN, fontSize);
                if (resizeFirst) {
                    resizeFrame(getSize());
                    resizeFirst = false;
                }
                Graphics2D g2d = (Graphics2D) g;

                g2d.setFont(font);

                soundLibrary.updateVis();


                //beat
                Frame.this.setBackground(g2d, counterBeat, 1);
                g2d.fillOval(xBeat + sizeBeat / 2, yBeat, sizeBeat, sizeBeat);

                Frame.this.setBackground(g2d, counterBeat, 2);
                g2d.fillOval(xBeat + sizeBeat + sizeBeat / 2 * 2, yBeat, sizeBeat, sizeBeat);

                Frame.this.setBackground(g2d, counterBeat, 3);
                g2d.fillOval(xBeat + sizeBeat * 2 + sizeBeat / 2 * 3, yBeat, sizeBeat, sizeBeat);

                Frame.this.setBackground(g2d, counterBeat, 4);
                g2d.fillOval(xBeat + sizeBeat * 3 + sizeBeat / 2 * 4, yBeat, sizeBeat, sizeBeat);

                /*
                    Master-Tempo (bpm)
                */
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("%.2f", masterTempo), masterX, masterY);

                /*
                    Set-up Text
                 */
                if (setupString) {
                    g2d.drawString("Set-UP Waiting", setUpStringX, masterY);
                }

                /*
                    Settings-Button
                */
                g2d.drawRect(settingsButtonX, settingsButtonY, settingsSize, settingsSize);
                try {
                    g2d.drawImage(ImageIO.read(new File("src/main/resources/Image/settings_button.png")), settingsButtonX, settingsButtonY, settingsSize, settingsSize, null);
                } catch (IOException e) {
                    Logger.error("Error while reading Settings-Image");
                    throw new RuntimeException(e);
                }

                /*
                    Meta-data
                 */
                if (metaData != null) {
                    for (Integer playerNumber : metaData.keySet()) {
                        TrackMetadata trackMetadata = metaData.get(playerNumber);
                        int x = metaDataX * playerNumber + (metaDataWidth * playerNumber - metaDataWidth);

                        if (trackMetadata != null) {
                            setupString = false;

                            g2d.setColor(Color.LIGHT_GRAY);
                            g2d.fillRect(x, metaDataY, metaDataWidth, metaDataHeight);
                            g2d.setColor(Color.BLACK);

                            g2d.drawString(trackMetadata.getTitle(), x + 5, metaDataY + 20);

                            Frame.this.setBackground(g2d, masterDevicdId, playerNumber);
                            g2d.drawString(playerNumber.toString(), x + metaDataWidth - 20, metaDataY + 20);
                            g2d.setColor(Color.BLACK);

                            try {
                                AlbumArt latestArtFor = ArtFinder.getInstance().getLatestArtFor(playerNumber);
                                g2d.drawImage(latestArtFor.getImage(), x + 5, metaDataY + 30, 100, 100, null);
                            } catch (Exception e) {
                                //ignore some Times picture miss
                            }


                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
                            dfs.setDecimalSeparator('.');
                            df.setDecimalFormatSymbols(dfs);
                            g2d.drawString(df.format(trackMetadata.getTempo() / 100.0), x + 110, metaDataY + 40);


                            g2d.drawString(trackMetadata.getArtist().label, x + 110, metaDataY + 60);
                            g2d.drawString(trackMetadata.getKey().label, x + 110, metaDataY + 80);

                        }
                    }
                }
                playOnBeat = false;

                /*
                    ToggleSwitch
                */

                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(controlPanelX, controlPanelY, controlPanelWith, controlPanelHeight);

                String tempToggleSwitch = "";
                if (toggleSwitchActive) {
                    g2d.setColor(Color.ORANGE);
                    tempToggleSwitch = "ON";
                } else {
                    g2d.setColor(Color.BLACK);
                    tempToggleSwitch = "OFF";

                }
                g2d.drawRect(toggleSwitchX, toggleSwitchY, toggleWith, toggleHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawString("toggle-slot -" + tempToggleSwitch, toggleSwitchX + 2, toggleSwitchY + toggleHeight - 5);

                /*
                    PlayerGrid
                 */

                for (int i = 0; i != playerGrid.getSlots().length; i++) {


                    Slot slot = playerGrid.getSlots()[i];

                    int tempI = i + 1;

                    int x = playGridX * tempI + (playGridSize * tempI - playGridSize);



                    /*
                        Slot
                    */
                    if (slot.isActive() && !toggleSwitchActive) {
                        midiColorController.switchColorAsync(i + 1, "01");
                        g2d.setColor(Color.RED);
                    } else if (toggleSwitchActive) {
                        midiColorController.switchColorAsync(i + 1, "11");
                        g2d.setColor(Color.BLACK);
                    } else {
                        g2d.setColor(Color.BLACK);
                        midiColorController.switchColorAsync(i + 1, "7F");
                    }

                    g2d.drawString("" + tempI, x, playGridY);
                    g2d.fillRect(x, playGridY + 4, playGridSize, playGridSize);

                    if(toggleSwitchActive){
                        g2d.setColor(Color.WHITE);
                        switch (tempI){
                            case 1:
                                g2d.drawString(String.valueOf(SHOT_TYPE.ONE_BEST.getBeatHit()), x + fontSize , playGridY+ playGridSize - fontSize);
                                break;
                            case 2:
                                g2d.drawString(String.valueOf(SHOT_TYPE.TWO_BEAT.getBeatHit()), x + fontSize, playGridY+ playGridSize - fontSize);
                                break;
                            case 3:
                                g2d.drawString(String.valueOf(SHOT_TYPE.FOUR_BEAT.getBeatHit()), x + fontSize, playGridY+ playGridSize - fontSize);
                                break;
                        }
                    }

                    g2d.setColor(Color.BLACK);


                    if (playerGridCounterBeat == tempI) {
                        g2d.setColor(Color.ORANGE);
                        midiColorController.switchColorAsync(i + 1, "14");

                        if (tempI == 1) {
                            midiColorController.switchColorAsync(playerGrid.getSlots().length, "7F");
                        }

                        g2d.fillOval(x + playGridSize / 2 - sizeBeat, playGridY + playGridSize + 10, sizeBeat, sizeBeat);
                        g2d.setColor(Color.BLACK);
                    }
                    if (checkBeat != playerGridCounterBeat) {
                        playOnBeat = true;
                        checkBeat = playerGridCounterBeat;
                    }


                    if (playerGridCounterBeat == tempI && playOnBeat && slot.isActive() && !toggleSwitchActive) {
                        slot.play();
                    }

                    /*
                        Volume-Slider
                     */
                    if (toggleVolumeSlider) {
                        slot.drawVolumeSlider(g2d, x, playGridY + sizeBeat * 8 + (int) (getWidth() * 0.02), getSize());
                    }


                    /*
                       Sound-list
                     */
                    if (!toggleVolumeSlider) {
                        List<SlotAudio> removeSlotAudio = new ArrayList<>();

                        /*
                            Player grid list
                         */
                        if(!toggleSwitchActive){
                            for (int j = 0; j != slot.getSelectedSounds().size(); j++) {
                                int kordY = playGridY + sizeBeat + (int) (getHeight() * 0.08) + fontSize * (j + 1) + fontSize * 2;
                                Koordinate koordinate = new Koordinate(x, kordY);

                                if (kordSlotRemoveList.get(koordinate.getName()) != null) {
                                    SlotAudio slotAudio = slot.getSelectedSounds().get(j);
                                    removeSlotAudio.add(slotAudio);
                                    kordSlotRemoveList.remove(koordinate.getName());
                                }else{
                                    kordSlotList.put(koordinate.getName(), koordinate);

                                    if (kordSlotMarkList.get(koordinate.getName()) != null) {
                                        g2d.setColor(Color.ORANGE);
                                    } else {
                                        g2d.setColor(Color.LIGHT_GRAY);
                                    }


                                    g2d.fillRect(x, kordY, playGridSize, fontSize);
                                    g2d.setColor(Color.BLACK);
                                    g2d.drawString(slot.getSelectedSounds().get(j).getName(), x, playGridY + sizeBeat + (int) (getHeight() * 0.08) + fontSize * (j + 1) + fontSize * 3);
                                }

                            }
                        }
                        /*
                            One-shot list
                        */
                        if(toggleSwitchActive){
                            int kordY = playGridY + playGridSize + fontSize*3;
                            g2d.drawString("PlayList: ", playGridX , kordY);

                            for(int j = 0; j !=  shotList.size(); j ++){
                                int kordX = playGridX + sizeBeat + (int) (getHeight() * 0.08) + fontSize * (j + 1) + fontSize * 2;

                                g2d.drawString(String.valueOf(shotList.get(j).getShotType().getBeatHit()), kordX , kordY);

                            }
                        }


                        if(!removeSlotAudio.isEmpty()) {
                            for (SlotAudio slotAudio : removeSlotAudio) {
                                slot.getSelectedSounds().remove(slotAudio);
                            }
                        }
                    }

                    //                    //todo remove sound, better looking and func


                }



                /*
                    Sound Library
                */
                for (int i = 0; i != soundLibrary.getFolderView().size(); i++) {
                    LibraryKind libraryKind = soundLibrary.getFolderView().get(i);

                    if (libraryKind.isSelected()) {
                        g2d.setColor(Color.ORANGE);
                    } else {
                        g2d.setColor(Color.WHITE);
                    }

                    g2d.fillRect(libraryX + libraryButtonWidth * i, libraryY - libraryButtonHeight, libraryButtonWidth, libraryButtonHeight);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(libraryKind.getName(), libraryX + 1 + libraryButtonWidth * i, libraryY - 4);


                    if (libraryStringMaxWidth < libraryKind.getName().length()) {
                        libraryStringMaxWidth = libraryKind.getName().length();
                    }
                }
//                g2d.drawRect(libraryX + libraryButtonWidth + libraryButtonWidth +  libraryButtonWidth , libraryY - 100, 1000, 2000 );


                g2d.fillRect(libraryX, libraryY, libraryWidth, libraryHeight);


            }
        };

        jLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for (Slot slot : playerGrid.getSlots()) {
                    slot.mouseReleased();
                }
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                for (Slot slot : playerGrid.getSlots()) {
                    slot.mousePressed(e.getX(), e.getY());
                }

                int mouseX = e.getX();
                int mouseY = e.getY();




                /*
                    Toggle beat-button
                 */
                Rectangle rectToggle = new Rectangle(toggleSwitchX, toggleSwitchY, toggleWith, toggleHeight);

                if (rectToggle.contains(mouseX, mouseY)) {
                    toggleSwitchActive = !toggleSwitchActive;
                    repaint();
                }
                /*
                    Beat grid
                 */
                for (int i = 0; i != playerGrid.getSlots().length; i++) {
                    Slot slot = playerGrid.getSlots()[i];
                    int tempI = i + 1;
                    int x = playGridX * tempI + (playGridSize * tempI - playGridSize);

                    Rectangle rect = new Rectangle(x + 2, playGridY, playGridSize, playGridSize);
                    if (rect.contains(mouseX, mouseY)) {

                        slot.addSelectedSound(soundLibrary.getSelectedSlotAudio());
                        soundLibrary.getSelectedSound().stopRreListen();
                        repaint();
                        break;
                    }
                }
                /*
                    Sound-library
                 */
                for (int i = 0; i != soundLibrary.getFolderView().size(); i++) {

                    Rectangle rect = new Rectangle(libraryX + libraryButtonWidth * i, libraryY - libraryButtonHeight, libraryButtonWidth, libraryButtonHeight);
                    if (rect.contains(mouseX, mouseY)) {
                        soundLibrary.setSelectedLibrary(i);
                        repaint();
                        break;
                    }
                }
                /*
                    Settings-button
                 */
                Rectangle settingsButtonRect = new Rectangle(settingsButtonX, settingsButtonY, settingsSize, settingsSize);
                if (settingsButtonRect.contains(mouseX, mouseY)) {
                    toggleSettingWindow();
                    repaint();
                }

                /*
                   Sound-list
                */
                for (String kordName : kordSlotList.keySet()) {
                    Koordinate koordinate = kordSlotList.get(kordName);
                    Rectangle kordListRect = new Rectangle(koordinate.getX(), koordinate.getY(), playGridSize, fontSize);

                    if (kordListRect.contains(mouseX, mouseY)) {
                        kordSlotRemoveList.put(kordName, koordinate);
                    }
                }

            }
        });

        jLabel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                for (Slot slot : playerGrid.getSlots()) {
                    slot.mouseDragged(e.getY());
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                /*
                   Sound-list
                */
                for (String kordName : kordSlotList.keySet()) {
                    Koordinate koordinate = kordSlotList.get(kordName);
                    Rectangle kordListRect = new Rectangle(koordinate.getX(), koordinate.getY(), playGridSize, fontSize);
                    if (kordListRect.contains(mouseX, mouseY)) {
                        kordSlotMarkList.put(kordName, koordinate);
                    } else {
                        kordSlotMarkList.remove(kordName);
                    }
                }
                repaint();
            }

        });


        MidiColorController midiColorController = MidiColorController.getInstance();
        midiColorController.setReceiver(new Receiver() {
            long timeMidi = 0;

            @Override
            public void send(MidiMessage message, long timeStamp) {
                if (message instanceof ShortMessage && timeStamp > timeMidi) {
                    timeMidi = timeStamp + 000_000_700_000;

                    ShortMessage sm = (ShortMessage) message;

                    Logger.info(String.valueOf(sm.getData1()));
                    int padNum = midiColorController.padMapper(sm.getData1());

                    if (sm.getData1() == 113) {
                        toggleSwitchActive = !toggleSwitchActive;
                    }

                    if (sm.getData1() == 115) {
                        toggleVolumeSlider = !toggleVolumeSlider;
                    }

                    if (sm.getData1() >= 36 && sm.getData1() <= 43) {
                        midiColorController.switchColorAsync(padNum, "01");

                        if (soundLibrary.getSelectedSound().getSelectedTitel() != null) {
                            if (!toggleSwitchActive) {
                                playerGrid.getSlots()[padNum - 1].addSelectedSound(soundLibrary.getSelectedSlotAudio());
                            }

                            if (toggleSwitchActive) {
                                /*
                                    Play slot in beat
                                 */
                                if(sm.getData1() == 36){
                                    shotList.add(new PlayShots(soundLibrary.getSelectedSlotAudio(), padNum, SHOT_TYPE.ONE_BEST));
                                }
                                if(sm.getData1() == 37){
                                    shotList.add(new PlayShots(soundLibrary.getSelectedSlotAudio(), padNum, SHOT_TYPE.TWO_BEAT));
                                }
                                if(sm.getData1() == 38){
                                    shotList.add(new PlayShots(soundLibrary.getSelectedSlotAudio(), padNum, SHOT_TYPE.FOUR_BEAT));
                                }
                            }

                        }
                    }
                }
                repaint();
            }

            @Override
            public void close() {
            }
        });


//        jLabel.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//                System.out.println(e.toString());
//                super.keyTyped(e);
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                System.out.println(e.toString());
//
//                super.keyPressed(e);
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                System.out.println(e.toString());
//
//                super.keyReleased(e);
//            }
//        });
//
//        jLabel.setFocusable(true);
//        jLabel.requestFocusInWindow();

        if (Settings.getInstance().isFullScreen() || Settings.getInstance().isFullScreenBorderLess()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            if (Settings.getInstance().isFullScreenBorderLess()) {
                setUndecorated(true);
            }
            screenWidth = getWidth();
            screenHeight = getHeight();

        } else {
            jLabel.setSize(screenWidth, screenHeight);
            setSize(screenWidth, screenHeight);

        }


        add(jLabel);

        setLayout(null);
        setVisible(true);
    }


    private void setBackground(Graphics2D g2d, int original, int equal) {
        if (original == equal) {
            g2d.setColor(Color.BLACK);
        } else {
            g2d.setColor(Color.RED);
        }
    }

    public void setMasterTempo(double masterTempo) {
        this.masterTempo = masterTempo;
        jLabel.repaint();
    }
    public double getMasterTempo() {
        return masterTempo;
    }

    private final ScheduledExecutorService beatPlayer = Executors.newScheduledThreadPool(1);

    public void setCounterBeat(int counterBeat) {
        long halfBeatMillis = (long) (60000 / (2 * masterTempo));
        try {
            for (PlayShots playShots : shotList) {
                playShots.play();
                shotList.remove(playShots);
            }
        } catch (Exception ignore) {
        }

        this.counterBeat = counterBeat;
        jLabel.repaint();

    }


    public void resizeFrame(Dimension dimension) {
        fontSize = Math.min(dimension.width, dimension.height) / 40;
        jLabel.setBounds(0, 0, dimension.width, dimension.height);

        /*
            Beat-dot
         */
        xBeat = (int) (dimension.width * 0.002);
        yBeat = (int) (dimension.height * 0.02);
        sizeBeat = (int) (Math.min(dimension.width, dimension.height) * 0.02);

        /*
            Master-Tempo (bpm)
         */
        masterX = (int) Math.max(xBeat + sizeBeat * 3 + sizeBeat / 2 * 4 + sizeBeat, dimension.width * 0.088);
        masterY = (int) (dimension.height * 0.02 + sizeBeat);

        /*
           Set-up Text
         */
        setUpStringX = (int) (masterX + dimension.width * 0.05);

        /*
            Settings-Button
         */
        settingsSize = (int) (Math.min(dimension.width, dimension.height) * 0.05);
        settingsButtonX = xBeat;
        settingsButtonY = yBeat - settingsSize / 3 + settingsSize;

        /*
                todo: meta-data
                todo: font size library
         */

        /*
            Library
         */

        libraryX = Math.max(playGridX * (PlayerGrid.getInstance().getSlots().length + 1) + (playGridSize * (PlayerGrid.getInstance().getSlots().length + 1) - playGridSize), dimension.width - libraryWidth);
        libraryButtonWidth = (int) (Math.max(libraryStringMaxWidth * fontSize, dimension.width * 0.08));
        libraryButtonHeight = (int) (Math.min(fontSize + 2, Math.max(fontSize, dimension.width * 0.02)));
        libraryY = playGridY;
        libraryWidth = (int) (dimension.width * 0.3);
        libraryHeight = (int) (dimension.height * 0.4);

        /*
            PlayerGrid | Slots
         */
        playGridX = (int) (dimension.width * 0.018);
        playGridSize = (int) (Math.min(dimension.width - libraryWidth, dimension.height) * 0.118);
        playGridY = (int) (dimension.height * 0.5);

        /*
            Toggle-switch
        */
        controlPanelX = libraryX;
        controlPanelY = metaDataY;
        controlPanelWith = (int) (dimension.width * 0.3);
        controlPanelHeight = (int) (dimension.height * 0.3);
        toggleSwitchX = controlPanelX + (int) (dimension.width * 0.005);
        toggleSwitchY = metaDataY + (int) (dimension.height * 0.005);
        toggleWith = (int) (dimension.width * 0.05) + 8 * fontSize;
        toggleHeight = (int) (dimension.height * 0.03);

        /*
            Repaint
         */
        for (LibraryKind libraryKind : LoadLibrary.getInstance().getFolderView()) {
            libraryKind.getTree().setBounds(libraryX, libraryY, libraryWidth, libraryHeight);
        }
        repaint();
    }


    public void setSetupString(boolean setupString) {
        this.setupString = setupString;
    }

    public void setMetaData(Map<Integer, TrackMetadata> metaData) {
        this.metaData = metaData;
    }

    public void setMasterDevicdId(int masterDevicdId) {
        this.masterDevicdId = masterDevicdId;
    }

    public void setPlayerGridCounterBeat(int playerGridCounterBeat) {
        this.playerGridCounterBeat = playerGridCounterBeat;
    }

    public void toggleSettingWindow() {
        Settings.getInstance().toggleVisible();
    }


}
