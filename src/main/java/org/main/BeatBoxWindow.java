package org.main;

import lombok.Getter;
import lombok.Setter;
import org.deepsymmetry.beatlink.data.*;


import org.main.audio.library.LibraryKind;
import org.main.audio.library.LoadLibrary;
import org.main.audio.PlayerGrid;
import org.main.audio.library.TYPE;
import org.main.audio.metadata.MetaDataFinder;
import org.main.audio.metadata.SlotAudioMetaData;
import org.main.audio.pattern.PatternManager;
import org.main.audio.pattern.PlayPattern;
import org.main.audio.playegrid.ExtendedTrackMetaData;
import org.main.audio.playegrid.Slot;
import org.main.audio.playegrid.SlotAudio;
import org.main.midi.MidiController;
import org.main.settings.Settings;
import org.main.util.Coordinates;
import org.main.util.Logger;
import org.main.util.midi.MidiByteMapper;
import org.main.util.graphics.StringTruncationUtil;
import org.main.util.graphics.components.audio.Amplitude;
import org.main.util.graphics.components.button.Button;
import testSampler.Audio.AudioPlayer;

import javax.sound.midi.*;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class BeatBoxWindow extends JFrame {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Color backgroundTime = Color.white;


    private int fontSize = 0;
    private int fontSizeImportant = 0;

    private static int screenWidth = 1200;
    private static int screenHeight = 700;

    private int xBeat;
    private int yBeat;
    private int counterBeat = 0;
    private int sizeBeat;

    private int masterX;
    private int masterY;

    @Getter
    private double masterTempo = 0.0;

    private int setUpStringX;

    private Map<Integer, ExtendedTrackMetaData> metaData;


    private int metaDataX;
    private int metaDataY;
    private int metaDataHeight;
    private int metaDataWidth;

    private int playGridX;
    private int playGridY;
    private int playGridSize;
    @Setter
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


    private Button playerGridButton = new Button(new Coordinates(0, 0), new Dimension(200, 10), "toggle-slot");
    private Button volumeSliderButton = new Button(new Coordinates(0, 0), new Dimension(200, 10), "togglePlayerGrid");
    private Button clearSlotsButton = new Button(new Coordinates(0, 0), new Dimension(100, 100), "clear");


    private Button patternSaveButton = new Button(new Coordinates(100, 100), new Dimension(100, 100), "save");
    private Button patternLoadButton = new Button(new Coordinates(100, 200), new Dimension(100, 100), "loadPattern");


    private Button settingsButton = new Button(new Coordinates(0, 0), new Dimension(100, 100), new File("src/main/resources/Image/settings_button.png"));


    private final HashMap<String, Coordinates> kordSlotList = new HashMap<>();
    private final HashMap<String, Coordinates> kordSlotMarkList = new HashMap<>();
    private final HashMap<String, Coordinates> kordSlotRemoveList = new HashMap<>();


    private List<SlotAudio> shotList = new ArrayList<>();


    @Setter
    private int masterDeviceId = 0;

    private final JLabel jLabel;

    private static BeatBoxWindow INSTANCE;

    private final LoadLibrary soundLibrary = LoadLibrary.getInstance();

    private final MidiController midiController = MidiController.getInstance(null);

    private final MetaDataFinder metadataFinder = MetaDataFinder.getInstance();


    public synchronized static BeatBoxWindow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BeatBoxWindow();
            Logger.init(INSTANCE.getClass());
        }
        return INSTANCE;
    }

    @Setter
    private boolean setupString = true;


    public final static String AUDIO_1 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/kick/KICK_04.wav";
    public final static String AUDIO_3 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/vox/Vocal_04_Revolution_140bpm.wav";
    public final static String AUDIO_2 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/vox/Vocal_02_Feel The Madness.wav";
    private  Amplitude amplitude;



    private BeatBoxWindow() {
        setLayout(null);



        /*
            New sound System tests
         */



        AudioInputStream audioInputStream_1 = AudioPlayer.getAudioInputStream(new File(AUDIO_1));
        AudioInputStream audioInputStream_2 = AudioPlayer.getAudioInputStream(new File(AUDIO_2));

        AudioInputStream audioInputStream_3 = AudioPlayer.getAudioInputStream(new File(AUDIO_3));



        AudioPlayer audioPlayer = AudioPlayer.getInstance();



        audioPlayer.addAudio(audioInputStream_1, 1.0);
        audioPlayer.addAudio(audioInputStream_1, 1.5);
        audioPlayer.addAudio(audioInputStream_1, 2.0);









//        AudioInputStream combine = audioPlayer.combine(fullBeatStream, audioInputStream_1);


        amplitude = new Amplitude(new Coordinates(0, 0), new Dimension(0, 0), audioPlayer.getFullBeatAudioStream());
        audioPlayer.addUpdateAudioStreamObserver(amplitude);




//        audioPlayer.play(audioInputStream_2);








        /*
            test.....
         */


        playerGridButton.setStateButton("-ON", "-OFF");
        volumeSliderButton.setStateButton("-ON", "-OFF");

        patternLoadButton.setBackgroundColor(Color.LIGHT_GRAY);
        patternSaveButton.setBackgroundColor(Color.LIGHT_GRAY);


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
                BeatBoxWindow.getInstance().resizeFrame(size);
            }
        });

        PatternManager patternManager = PatternManager.getInstance();



        jLabel = new JLabel() {
            boolean active =  false;
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

                g2d.setColor(backgroundTime);
                g2d.fillRect(0, 0, getWidth(), getHeight());


                soundLibrary.updateVis();



                /*
                    todo: update SoundSytem
                 */
                if(playerGridCounterBeat == 6 && !active){
                    audioPlayer.addAudio(audioInputStream_1, 0.0);
                    audioPlayer.addAudio(audioInputStream_1, 0.5);
                    active = true;
                }

                amplitude.draw(g2d);
                if(playerGridCounterBeat == 1){
//                    audioPlayer.play();
                }







                //beat
                BeatBoxWindow.this.setBackground(g2d, counterBeat, 1);
                g2d.fillOval(xBeat + sizeBeat / 2, yBeat, sizeBeat, sizeBeat);

                BeatBoxWindow.this.setBackground(g2d, counterBeat, 2);
                g2d.fillOval(xBeat + sizeBeat + sizeBeat / 2 * 2, yBeat, sizeBeat, sizeBeat);

                BeatBoxWindow.this.setBackground(g2d, counterBeat, 3);
                g2d.fillOval(xBeat + sizeBeat * 2 + sizeBeat / 2 * 3, yBeat, sizeBeat, sizeBeat);

                BeatBoxWindow.this.setBackground(g2d, counterBeat, 4);
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
                    Meta-data
                 */
                if (metaData != null) {
                    for (Integer playerNumber : metaData.keySet()) {

                        ExtendedTrackMetaData trackMetadata = metaData.get(playerNumber);
                        int x = metaDataX * playerNumber + (metaDataWidth * playerNumber - metaDataWidth);


                        if (trackMetadata != null) {
                            setupString = false;

                            g2d.setColor(Color.LIGHT_GRAY);
                            g2d.fillRect(x, metaDataY, metaDataWidth, metaDataHeight);
                            g2d.setColor(Color.BLACK);

                            /*
                                Recommendations
                             */
                            List<String> recommendations = metadataFinder.findRecommendations(trackMetadata.getTitle());

                            if (recommendations != null) {

                                for (int i = 0; i != recommendations.size(); i++) {
                                    SlotAudioMetaData slotAudio = metadataFinder.getMetaData(recommendations.get(i));

                                    g2d.drawString(slotAudio.getShortName(), x + (int) (slotAudio.getShortName().length() * getWidth() * (i + 1) * 0.01), (int) (metaDataY + getHeight() * 0.25 + fontSize));
                                }

//                                if(trackMetadata.getButton() == null){
//                                    trackMetadata.setButton(new Button(new Koordinate(x, (int) (metaDataY + getHeight() * 0.25 + fontSize)), new Dimension(settingsSize, settingsSize)));
//                                }
//                                trackMetadata.getButton().draw(g2d);
                            }





                            /*
                               Other meta-data
                             */
                            StringTruncationUtil.drawStringWithMaxWidth(g2d, trackMetadata.getTitle(), (int) (x + getX() * 0.02), (int) (metaDataY + getY() * 0.2 + fontSize), metaDataWidth - fontSize * 3);

                            BeatBoxWindow.this.setBackground(g2d, masterDeviceId, playerNumber);
                            g2d.drawString(playerNumber.toString(), x + metaDataWidth - fontSize, metaDataY + fontSize);
                            g2d.setColor(Color.BLACK);

                            int y = (int) (metaDataY + getHeight() * 0.01 + fontSize);

                            try {
                                AlbumArt latestArtFor = ArtFinder.getInstance().getLatestArtFor(playerNumber);


                                g2d.drawImage(latestArtFor.getImage(), (int) (x + getWidth() * 0.008), y, getHeight() / 5, getHeight() / 5, null);
                            } catch (Exception e) {
                                //ignore some Times picture miss
                            }


                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
                            dfs.setDecimalSeparator('.');
                            df.setDecimalFormatSymbols(dfs);
                            x = (int) (x + getWidth() * 0.008 + (double) getHeight() / 5);

                            g2d.drawString(df.format(trackMetadata.getTempo() / 100.0), x, y + fontSize);
                            g2d.drawString(trackMetadata.getArtist(), x, y + fontSize * 2);
                            g2d.drawString(trackMetadata.getKey(), x, y + fontSize * 3);

                        }
                    }
                }
                playOnBeat = false;

                /*
                    ToggleSwitch
                */

                g2d.setColor(Color.lightGray);
                g2d.fillRect(controlPanelX, controlPanelY, controlPanelWith, controlPanelHeight);

                /*
                    Buttons
                 */

                playerGridButton.draw(g2d);
                volumeSliderButton.draw(g2d);
                clearSlotsButton.draw(g2d);

                settingsButton.draw(g2d);

                if (volumeSliderButton.isToggle()) {
                    patternSaveButton.draw(g2d);
                    patternLoadButton.draw(g2d);
                }


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
                    if (slot.isActive() && !playerGridButton.isToggle()) {
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "01");
                        g2d.setColor(Color.RED);
                    } else if (playerGridButton.isToggle()) {
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "11");
                        g2d.setColor(Color.BLACK);
                    } else {
                        g2d.setColor(Color.BLACK);
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "7F");
                    }

                    g2d.drawString("" + tempI, x, playGridY);
                    g2d.fillRect(x, playGridY + 4, playGridSize, playGridSize);

                    if (playerGridButton.isToggle()) {
                        g2d.setColor(Color.WHITE);
                        switch (tempI) {
                            case 1:
                                g2d.drawString(String.valueOf(TYPE.ONE_BEST.getBeatHit()), x + fontSize, playGridY + playGridSize - fontSize);
                                break;
                            case 2:
                                g2d.drawString(String.valueOf(TYPE.TWO_BEAT.getBeatHit()), x + fontSize, playGridY + playGridSize - fontSize);
                                break;
                            case 3:
                                g2d.drawString(String.valueOf(TYPE.FOUR_BEAT.getBeatHit()), x + fontSize, playGridY + playGridSize - fontSize);
                                break;
                        }
                    }

                    g2d.setColor(Color.BLACK);

                    if (playerGridCounterBeat == tempI) {
                        g2d.setColor(Color.ORANGE);
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "14");

                        if (tempI == 1) {
                            BeatBoxWindow.this.midiController.switchColorAsync(playerGrid.getSlots().length, "7F");
                        }

                        g2d.fillOval(x + playGridSize / 2 - sizeBeat, playGridY + playGridSize + 10, sizeBeat, sizeBeat);
                        g2d.setColor(Color.BLACK);
                    }
                    if (checkBeat != playerGridCounterBeat) {
                        playOnBeat = true;
                        checkBeat = playerGridCounterBeat;
                    }

                    if (playerGridCounterBeat == tempI && playOnBeat && slot.isActive() && !playerGridButton.isToggle()) {
                        slot.play();
                    }

                    /*
                        Volume-Slider
                        todo: find better place for volume Slider
                     */
//                    if (volumeSliderButton.isToggle()) {
//                        slot.drawVolumeSlider(g2d, x, playGridY + sizeBeat * 8 + (int) (getWidth() * 0.02), getSize());
//                    }

                    /*
                       Sound-list
                     */
                    if (!volumeSliderButton.isToggle()) {
                        List<SlotAudio> removeSlotAudio = new ArrayList<>();

                        /*
                            Player grid list
                         */
                        if (!playerGridButton.isToggle()) {
                            for (int j = 0; j != slot.getSelectedSounds().size(); j++) {
                                int kordY = (playGridY + sizeBeat + (int) (getHeight() * 0.1)) + (fontSize * 2 * (j + 1));

                                Coordinates coordinates = new Coordinates(x, kordY);

                                if (kordSlotRemoveList.get(coordinates.getName()) != null) {
                                    SlotAudio slotAudio = slot.getSelectedSounds().get(j);
                                    removeSlotAudio.add(slotAudio);
                                    kordSlotRemoveList.remove(coordinates.getName());
                                } else {
                                    kordSlotList.put(coordinates.getName(), coordinates);

                                    if (kordSlotMarkList.get(coordinates.getName()) != null) {
                                        g2d.setColor(Color.ORANGE);
                                    } else {
                                        g2d.setColor(Color.LIGHT_GRAY);
                                    }

                                    g2d.fillRect(x, kordY, playGridSize, fontSize * 2);
                                    g2d.setColor(Color.BLACK);

                                    if (metadataFinder.getMetaData(slot.getSelectedSounds().get(j).getName()) != null) {
                                        StringTruncationUtil.drawStringWithMaxWidth(g2d, metadataFinder.getMetaData(slot.getSelectedSounds().get(j).getName()).getShortName(), x, kordY + fontSize, playGridSize);
                                    } else {
                                        StringTruncationUtil.drawStringWithMaxWidth(g2d, slot.getSelectedSounds().get(j).getName(), x, kordY + fontSize, playGridSize);
                                    }

                                }

                            }
                        }
                        /*
                            One-shot list
                        */
                        if (playerGridButton.isToggle()) {
                            int kordY = playGridY + playGridSize + fontSize * 3;
                            g2d.drawString("PlayList: ", playGridX, kordY);

                            for (int j = 0; j != shotList.size(); j++) {
                                int kordX = playGridX + sizeBeat + (int) (getHeight() * 0.08) + fontSize * (j + 1) + fontSize * 2;

                                g2d.drawString(String.valueOf(shotList.get(j).getPlayType().getBeatHit()), kordX, kordY);

                            }

                        }


                        if (!removeSlotAudio.isEmpty()) {
                            for (SlotAudio slotAudio : removeSlotAudio) {
                                slot.getSelectedSounds().remove(slotAudio);
                            }
                        }

                        /*
                            Sound Recommendation
                         */
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
                playerGridButton.clickMouse(e, () -> repaint());
                volumeSliderButton.clickMouse(e, () -> repaint());
                clearSlotsButton.clickMouse(e, () -> {
                    playerGrid.clearSlots();
                    repaint();
                });


                /*
                    Pattern Manager
                 */
                if (volumeSliderButton.isToggle()) {
                    patternSaveButton.clickMouse(e, () -> {
                        patternManager.savePattern(new PlayPattern("test", playerGrid.getSlots()));
                        repaint();

                    });

                    patternLoadButton.clickMouse(e, () -> {
                        patternManager.loadPattern(playerGrid, "test");
                        repaint();
                    });
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
                        if (playerGridButton.isToggle()) {
                            if (tempI == 1) {
                                shotList.add(new SlotAudio(soundLibrary.getSelectedSlotAudio(), TYPE.ONE_BEST));
                            }
                            if (tempI == 2) {
                                shotList.add(new SlotAudio(soundLibrary.getSelectedSlotAudio(), TYPE.TWO_BEAT));
                            }
                            if (tempI == 3) {
                                shotList.add(new SlotAudio(soundLibrary.getSelectedSlotAudio(), TYPE.FOUR_BEAT));
                            }
                        } else {
                            slot.addSelectedSound(soundLibrary.getSelectedSlotAudio());
                            soundLibrary.getSelectedSound().stopRreListen();
                        }
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
                settingsButton.clickMouse(e, () -> {
                    toggleSettingWindow();
                    repaint();
                });

                /*
                   Sound-list
                */
                for (String kordName : kordSlotList.keySet()) {
                    Coordinates coordinates = kordSlotList.get(kordName);
                    Rectangle kordListRect = new Rectangle(coordinates.getX(), coordinates.getY(), playGridSize, fontSize * 2);

                    if (kordListRect.contains(mouseX, mouseY)) {
                        kordSlotRemoveList.put(kordName, coordinates);
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

                clearSlotsButton.hoverMouse(e, Color.ORANGE, () -> repaint());
                patternLoadButton.hoverMouse(e, Color.ORANGE, () -> repaint());
                patternSaveButton.hoverMouse(e, Color.ORANGE, () -> repaint());
                settingsButton.hoverMouse(e, new Color(182, 182, 182), () -> repaint());


                /*
                   Sound-list
                */
                int mouseX = e.getX();
                int mouseY = e.getY();

                for (String kordName : kordSlotList.keySet()) {
                    Coordinates coordinates = kordSlotList.get(kordName);
                    Rectangle kordListRect = new Rectangle(coordinates.getX(), coordinates.getY(), playGridSize, fontSize * 2);
                    if (kordListRect.contains(mouseX, mouseY)) {
                        kordSlotMarkList.put(kordName, coordinates);
                    } else {
                        kordSlotMarkList.remove(kordName);
                    }
                }
                repaint();
            }
        });

        MidiController midiController = MidiController.getInstance(null);
        midiController.setReceiver(new Receiver() {
            long timeMidi = 0;

            @Override
            public void send(MidiMessage message, long timeStamp) {
                if (message instanceof ShortMessage && timeStamp > timeMidi) {
                    timeMidi = timeStamp + 000_000_700_000;

                    ShortMessage sm = (ShortMessage) message;

                    Logger.info(String.valueOf(sm.getData1()));
                    int padNum = MidiByteMapper.padMapper(sm.getData1());

                    if (sm.getData1() == 113) {
                        playerGridButton.toggle();
                    }

                    if (sm.getData1() == 115) {
                        volumeSliderButton.toggle();
                    }

                    if (sm.getData1() >= 36 && sm.getData1() <= 43) {
                        midiController.switchColorAsync(padNum, "01");

                        if (soundLibrary.getSelectedSound().getSelectedTitel() != null) {
                            if (!playerGridButton.isToggle()) {
                                playerGrid.getSlots()[padNum - 1].addSelectedSound(soundLibrary.getSelectedSlotAudio());
                            }

                            if (playerGridButton.isToggle()) {
                                /*
                                    Play slot in beat
                                 */
                                if (sm.getData1() == 36) {
                                    shotList.add(new SlotAudio(soundLibrary.getSelectedSlotAudio(), TYPE.ONE_BEST));
                                }
                                if (sm.getData1() == 37) {
                                    shotList.add(new SlotAudio(soundLibrary.getSelectedSlotAudio(), TYPE.TWO_BEAT));
                                }
                                if (sm.getData1() == 38) {
                                    shotList.add(new SlotAudio(soundLibrary.getSelectedSlotAudio(), TYPE.FOUR_BEAT));
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

        if (Settings.getInstance().getBeatBoxWindowSettings().isBeatBoxWindowFullScreen() || Settings.getInstance().getBeatBoxWindowSettings().isBeatBoxWindowFullScreenBorderLess()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            if (Settings.getInstance().getBeatBoxWindowSettings().isBeatBoxWindowFullScreenBorderLess()) {
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

//    private final ScheduledExecutorService beatPlayer = Executors.newScheduledThreadPool(1);

    public void setCounterBeat(int counterBeat) {
        try {
            for (SlotAudio playShots : shotList) {
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

        //todo: set other important fonts with this size
        fontSizeImportant = fontSize + 20;


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
        int settingsSize = (int) (Math.min(dimension.width, dimension.height) * 0.05);
        settingsButton.setRepositionAndSize(xBeat, yBeat - settingsSize / 3 + settingsSize, settingsSize, settingsSize);

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
        metaDataHeight = controlPanelHeight;
        metaDataWidth = controlPanelWith;



        /*
            New Player style
         */
        amplitude.setRepositionAndSize(playGridX, 200, 800 , 100);


        /*
            Toggle-switch
        */
        controlPanelX = libraryX;
        controlPanelY = metaDataY;
        controlPanelWith = (int) (dimension.width * 0.3);
        controlPanelHeight = (int) (dimension.height * 0.3);

        playerGridButton.setRepositionAndSize(controlPanelX + (int) (dimension.width * 0.005), metaDataY + (int) (dimension.height * 0.005), (int) (dimension.width * 0.05) + 8 * fontSize, (int) (dimension.height * 0.03));
        volumeSliderButton.setRepositionAndSize(playerGridButton.getX(), playerGridButton.getY() + playerGridButton.getDimension().height + (int) (dimension.height * 0.005), playerGridButton.getDimension().width, playerGridButton.getDimension().height);
        clearSlotsButton.setRepositionAndSize(playerGridButton.getX(), playerGridButton.getY() + 2 * (playerGridButton.getDimension().height + (int) (dimension.height * 0.01)), playerGridButton.getDimension().width / 3, playerGridButton.getDimension().height * 2);

        /*
            Patterns
         */
        patternLoadButton.setRepositionAndSize(playGridX, (playGridY + sizeBeat + (int) (getHeight() * 0.1)) + (fontSize * 2), playGridSize, playGridSize);
        patternSaveButton.setRepositionAndSize(playGridX, (playGridY + (2 * (sizeBeat + (int) (getHeight() * 0.1)) + (fontSize * 2))), playGridSize, playGridSize);

        /*
            Meta-data
         */
        metaDataX = settingsButton.getX() + (int) (dimension.width * 0.005);
        metaDataY = settingsButton.getY() + (int) (dimension.height * 0.1);

        /*
            Library
         */
        for (LibraryKind libraryKind : soundLibrary.getFolderView()) {
            libraryKind.getTree().setBounds(libraryX, libraryY, libraryWidth, libraryHeight);

        }
        soundLibrary.updateFont(fontSizeImportant);

          /*
            Repaint
         */
        repaint();
    }


    public void toggleSettingWindow() {
        Settings.getInstance().toggleVisible();
    }

    public void setMetaData(Map<Integer, TrackMetadata> trackMetadata) {
        HashMap<Integer, ExtendedTrackMetaData> mappedMetaData = new HashMap<>();
        for (Integer key : trackMetadata.keySet()) {
            mappedMetaData.put(key, new ExtendedTrackMetaData(trackMetadata.get(key)));
        }
        this.metaData = mappedMetaData;
    }

}
