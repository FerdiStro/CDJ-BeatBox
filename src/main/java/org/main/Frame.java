package org.main;

import org.deepsymmetry.beatlink.data.*;
import org.main.audio.library.LibraryKind;
import org.main.audio.library.LoadLibrary;
import org.main.audio.PlayerGrid;
import org.main.audio.playegrid.Slot;
import org.main.midi.MidiColorController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public  class Frame extends JFrame {

    private final static int screenWidth =  1200;
    private final static int screenHeight =  700;

    private final int xBeat     =  15;
    private final int yBeat     =  10;
    private int counterBeat = 0;
    private final int sizeBeat  = 10 ;

    private final int masterX = 100;
    private final int masterY = 20;
    private double masterTempo = 0.0;

    private Map<Integer, TrackMetadata> metaData;
    private final  int metaDataX = 15;
    private final  int metaDataY = 50;
    private final  int metaDataHeight = 200;
    private final  int metaDataWidth = 400;

    private final  int playGridX = 15;
    private final  int playGridY = metaDataHeight +  80;
    private final  int playGridSize =  80;
    private int playerGridCounterBeat = 0;
    private boolean playOnBeat = false;
    private int     checkBeat = 0 ;

    private final int libraryX = 680 + playGridSize + 20;
    private final int libraryY = playGridY;
    private final int libraryWidth = 400;
    private final int libraryHeight = 300;

    private int masterDevicdId = 0;

    private final JLabel jLabel;

    private static Frame INSTANCE;

    private final LoadLibrary soundLibrary = LoadLibrary.getInstance();

    private final MidiColorController midiColorController = MidiColorController.getInstance();

    public static Frame getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Frame();
        }
        return INSTANCE;
    }

    private boolean setupString = true;


    private  Frame(){
        PlayerGrid playerGrid = PlayerGrid.getInstance();

        for(LibraryKind libraryKind : soundLibrary.getFolderView()){
            JScrollPane tree = libraryKind.getTree();
            tree.setBounds(libraryX, libraryY, libraryWidth, libraryHeight);
            add(tree);
        }



        jLabel =  new JLabel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                soundLibrary.updateVis();


                Frame.this.setBackground(g2d, counterBeat, 1);
                g2d.fillOval(xBeat, yBeat , sizeBeat, sizeBeat);


                Frame.this.setBackground(g2d,counterBeat, 2);
                g2d.fillOval(xBeat*2, yBeat , sizeBeat, sizeBeat);

                Frame.this.setBackground(g2d,counterBeat, 3);
                g2d.fillOval(xBeat*3 , yBeat , sizeBeat, sizeBeat);

                Frame.this.setBackground(g2d,counterBeat, 4);
                g2d.fillOval(xBeat*4 , yBeat , sizeBeat, sizeBeat);

                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("%.2f", masterTempo), masterX, masterY );

                if(setupString){
                    g2d.drawString("Set-UP Waiting", masterX +  100, masterY );
                }


                if(metaData != null){
                    for(Integer playerNumber : metaData.keySet()){
                        TrackMetadata trackMetadata = metaData.get(playerNumber);
                        int x  = metaDataX * playerNumber +  (metaDataWidth * playerNumber - metaDataWidth );

                        if(trackMetadata != null){
                            setupString = false;
                            g2d.setColor(Color.LIGHT_GRAY);
                            g2d.fillRect(x , metaDataY, metaDataWidth, metaDataHeight );
                            g2d.setColor(Color.BLACK);

                            g2d.setFont(new Font("Arial", Font.BOLD, 12));
                            g2d.drawString(trackMetadata.getTitle(), x + 5, metaDataY + 20 );

                            Frame.this.setBackground(g2d, masterDevicdId, playerNumber);
                            g2d.drawString(playerNumber.toString(), x + metaDataWidth - 20, metaDataY +  20);
                            g2d.setColor(Color.BLACK);

                            try {
                                AlbumArt latestArtFor = ArtFinder.getInstance().getLatestArtFor(playerNumber);
                                g2d.drawImage(latestArtFor.getImage(), x  +  5, metaDataY +  30, 100, 100, null);
                            }catch (Exception e){
                                //ignore some Times picture miss
                            }



                            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
                            dfs.setDecimalSeparator('.');
                            df.setDecimalFormatSymbols(dfs);
                            g2d.drawString(df.format(trackMetadata.getTempo() / 100.0), x + 110, metaDataY +  40 );



                            g2d.drawString(trackMetadata.getArtist().label, x + 110, metaDataY + 60);
                            g2d.drawString(trackMetadata.getKey().label, x + 110   , metaDataY + 80);





                        }
                       }
                }
                playOnBeat =  false;
                for(int i = 0 ;  i != playerGrid.getSlots().length; i++){




                    Slot slot = playerGrid.getSlots()[i];

                    int tempI =  i  + 1;

                    int x  = playGridX * tempI +  (playGridSize * tempI - playGridSize );


                    if(slot.isActive()){
                        g2d.setColor(Color.RED);

                    }else{
                        g2d.setColor(Color.BLACK);
                    }

                    g2d.drawString("" + tempI, x, playGridY);
                    g2d.fillRect(x   , playGridY + 4,  playGridSize, playGridSize);

                    if(playerGridCounterBeat == tempI ){
                        g2d.setColor(Color.ORANGE);
                        g2d.fillOval(x + playGridSize / 2 - sizeBeat, playGridY + playGridSize + 10 , sizeBeat, sizeBeat);
                        g2d.setColor(Color.BLACK);

                    }
                    if(checkBeat != playerGridCounterBeat) {
                        playOnBeat = true;
                        checkBeat =  playerGridCounterBeat;
                    }

                    if(playerGridCounterBeat == tempI  && playOnBeat && slot.isActive()){
                        slot.play();
                        CompletableFuture<Void> future = midiColorController.switchColorAsync(2, "10");

                    }

                    /*
                        Volume-Slider
                     */
                    slot.drawVolumeSlider(g2d, x ,playGridY +  120 );
                    g2d.setColor(Color.BLACK);

                    x =  x + 15;
                    int y = playGridY + playGridSize + 30 + sizeBeat;
                    g2d.drawString("-  10db", x, y  + 5);
                    g2d.drawString("-   0db", x, y +  slot.getVolumeSliderHeight() /2 + 5 );
                    g2d.drawString("  -10db", x, y +  slot.getVolumeSliderHeight());

                    x =  x - 15;
                    //todo remove sound, better looking and func
                    for(int j = 0; j  != slot.getSelectedSounds().size(); j++){
                        g2d.drawString(slot.getSelectedSounds().get(j).getName()  , x  , (y  + playGridSize ) +  20  * (j+1) + 20);
                    }



                }



                /*
                    Sound Library
                */
                for(int i = 0; i != soundLibrary.getFolderView().size(); i ++){
                    LibraryKind libraryKind = soundLibrary.getFolderView().get(i);

                    if(libraryKind.isSelected()) {
                        g2d.setColor(Color.ORANGE);
                    }else{
                        g2d.setColor(Color.WHITE);
                    }

                    g2d.fillRect(libraryX +  100 * i, libraryY - 20, 100 , 20);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(libraryKind.getName(), libraryX + 1  +  100 * i, libraryY - 4 );

                }


                g2d.fillRect(libraryX, libraryY, libraryWidth, libraryHeight);



            }
        };

        jLabel.addMouseListener(new MouseAdapter(){
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

                for(int i = 0; i != soundLibrary.getFolderView().size(); i ++){
                    Rectangle rect = new Rectangle(libraryX +  100 * i, libraryY - 20, 100 , 20);
                    if (rect.contains(mouseX, mouseY)){
                        soundLibrary.setSelectedLibrary(i);
                        repaint();
                        break;
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

        jLabel.setSize(screenWidth, screenHeight);
        add(jLabel);






        setSize(screenWidth,screenHeight);
        setLayout(null);
        setVisible(true);

    }

    private void setBackground(Graphics2D g2d,int original,  int equal){
        if(original == equal){
            g2d.setColor(Color.BLACK);
        }else{
            g2d.setColor(Color.RED);
        }
    }

    public void setMasterTempo(double masterTempo) {
        this.masterTempo = masterTempo;
        jLabel.repaint();
    }

    public void setCounterBeat(int counterBeat) {
        this.counterBeat = counterBeat;
        jLabel.repaint();



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
}
