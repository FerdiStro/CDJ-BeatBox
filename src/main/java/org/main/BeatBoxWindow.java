package org.main;

import lombok.Getter;
import lombok.Setter;
import org.deepsymmetry.beatlink.data.*;
import org.main.audio.PlayShots;
import org.main.audio.SHOT_TYPE;
import org.main.audio.library.LibraryKind;
import org.main.audio.library.LoadLibrary;
import org.main.audio.PlayerGrid;
import org.main.audio.metadata.MetaDataFinder;
import org.main.audio.metadata.SlotAudioMetaData;
import org.main.audio.playegrid.ExtendedTrackMetaData;
import org.main.audio.playegrid.Slot;
import org.main.audio.playegrid.SlotAudio;
import org.main.midi.MidiController;
import org.main.settings.Settings;
import org.main.util.Koordinate;
import org.main.util.Logger;
import org.main.util.MidiByteMapper;
import org.main.util.graphics.StringTruncationUtil;

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


public class BeatBoxWindow extends JFrame {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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



    private  int metaDataX;
    private  int metaDataY;
    private  int metaDataHeight;
    private  int metaDataWidth;

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

    private int toggleSwitchX;
    private int toggleSwitchY;
    private int toggleWith;
    private int toggleHeight;
    private boolean toggleSwitchActive = false;


    private boolean toggleVolumeSlider = false;
    private final HashMap<String, Koordinate> kordSlotList = new HashMap<>();
    private final HashMap<String, Koordinate> kordSlotMarkList = new HashMap<>();
    private final HashMap<String, Koordinate> kordSlotRemoveList = new HashMap<>();


    private int settingsSize;


    private int settingsButtonX = xBeat;
    private int settingsButtonY = yBeat + settingsSize;


    private List<PlayShots> shotList = new ArrayList<>();


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


    private BeatBoxWindow() {
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
                BeatBoxWindow.getInstance().resizeFrame(size);
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

                            if(recommendations != null ){

                                for(int i =  0 ; i != recommendations.size(); i ++){
                                    SlotAudioMetaData slotAudio = metadataFinder.getMetaData(recommendations.get(i));

                                    g2d.drawString(slotAudio.getShortName(), x + (int)(slotAudio.getShortName().length() * getWidth() * (i + 1) * 0.01), (int) (metaDataY + getHeight() * 0.25 + fontSize));
                                }

//                                if(trackMetadata.getButton() == null){
//                                    trackMetadata.setButton(new Button(new Koordinate(x, (int) (metaDataY + getHeight() * 0.25 + fontSize)), new Dimension(settingsSize, settingsSize)));
//                                }
//                                trackMetadata.getButton().draw(g2d);
                            }





                            /*
                               Other meta-data
                             */
                            StringTruncationUtil.drawStringWithMaxWidth(g2d, trackMetadata.getTitle(), (int) (x + getX() * 0.02), (int) (metaDataY + getY() * 0.2 + fontSize ) , metaDataWidth - fontSize * 3 );

                            BeatBoxWindow.this.setBackground(g2d, masterDeviceId, playerNumber);
                            g2d.drawString(playerNumber.toString(), x + metaDataWidth - fontSize, metaDataY + fontSize);
                            g2d.setColor(Color.BLACK);

                            int y =  (int) (metaDataY + getHeight() * 0.01 + fontSize);

                            try {
                                AlbumArt latestArtFor = ArtFinder.getInstance().getLatestArtFor(playerNumber);


                                g2d.drawImage(latestArtFor.getImage(), (int) (x + getWidth() * 0.008), y , getHeight()/5, getHeight()/5, null);
                            } catch (Exception e) {
                                //ignore some Times picture miss
                            }


                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
                            dfs.setDecimalSeparator('.');
                            df.setDecimalFormatSymbols(dfs);
                            x =  (int) (x + getWidth() * 0.008 +  (double) getHeight() /5 );

                            g2d.drawString(df.format(trackMetadata.getTempo() / 100.0), x, y + fontSize);
                            g2d.drawString(trackMetadata.getArtist(), x , y + fontSize * 2 );
                            g2d.drawString(trackMetadata.getKey(), x , y + fontSize * 3);

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
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "01");
                        g2d.setColor(Color.RED);
                    } else if (toggleSwitchActive) {
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "11");
                        g2d.setColor(Color.BLACK);
                    } else {
                        g2d.setColor(Color.BLACK);
                        BeatBoxWindow.this.midiController.switchColorAsync(i + 1, "7F");
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
                                int kordY = (playGridY + sizeBeat + (int) (getHeight() * 0.1)) + (fontSize*2 * (j+1)) ;

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

                                    g2d.fillRect(x, kordY, playGridSize, fontSize * 2);
                                    g2d.setColor(Color.BLACK);

                                    if(metadataFinder.getMetaData(slot.getSelectedSounds().get(j).getName()) !=  null){
                                        StringTruncationUtil.drawStringWithMaxWidth(g2d,metadataFinder.getMetaData(slot.getSelectedSounds().get(j).getName()).getShortName(), x, kordY + fontSize, playGridSize);
                                    }else{
                                        StringTruncationUtil.drawStringWithMaxWidth(g2d,slot.getSelectedSounds().get(j).getName(), x, kordY  + fontSize, playGridSize);
                                    }

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
                    Rectangle kordListRect = new Rectangle(koordinate.getX(), koordinate.getY(), playGridSize, fontSize * 2);

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
                    Rectangle kordListRect = new Rectangle(koordinate.getX(), koordinate.getY(), playGridSize, fontSize * 2);
                    if (kordListRect.contains(mouseX, mouseY)) {
                        kordSlotMarkList.put(kordName, koordinate);
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
                        toggleSwitchActive = !toggleSwitchActive;
                    }

                    if (sm.getData1() == 115) {
                        toggleVolumeSlider = !toggleVolumeSlider;
                    }

                    if (sm.getData1() >= 36 && sm.getData1() <= 43) {
                        midiController.switchColorAsync(padNum, "01");

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

    private final ScheduledExecutorService beatPlayer = Executors.newScheduledThreadPool(1);

    public void setCounterBeat(int counterBeat) {
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

        //todo: set other important fonts with this size
        fontSizeImportant =  fontSize + 20;


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
        metaDataHeight = controlPanelHeight;
        metaDataWidth = controlPanelWith;

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
            Meta-data
         */
        metaDataX = settingsButtonX + (int) (dimension.width * 0.005) ;
        metaDataY = settingsButtonY + (int) (dimension.height * 0.1);


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

    public void setMetaData(Map<Integer, TrackMetadata>  trackMetadata){
        HashMap<Integer, ExtendedTrackMetaData> mappedMetaData = new HashMap<>();
        for (Integer key : trackMetadata.keySet()) {
            mappedMetaData.put(key, new ExtendedTrackMetaData(trackMetadata.get(key)));
        }
        this.metaData =  mappedMetaData;
    }

}
