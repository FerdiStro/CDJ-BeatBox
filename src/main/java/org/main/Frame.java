package org.main;

import org.deepsymmetry.beatlink.data.*;
import org.main.audio.LoadLibrary;
import org.main.audio.PlayerGrid;
import org.main.audio.playegrid.Slot;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public  class Frame extends JFrame {

    private final static int screenWidth =  1000;
    private final static int screenHeight =  600;

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

    private int masterDevicdId = 0;

    private JLabel jLabel;

    public static Frame getInstance(){
        return new Frame();
    }

    private boolean setupString = true;

    private  Frame(){
        PlayerGrid playerGrid = PlayerGrid.getInstance();


        jLabel =  new JLabel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

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


                            AlbumArt latestArtFor = ArtFinder.getInstance().getLatestArtFor(playerNumber);

                            g2d.drawImage(latestArtFor.getImage(), x  +  5, metaDataY +  30, 100, 100, null);

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
                    }

                }

            }
        };

        jLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                for (int i = 0; i != playerGrid.getSlots().length; i++) {
                    Slot slot = playerGrid.getSlots()[i];
                    int tempI = i + 1;
                    int x = playGridX * tempI + (playGridSize * tempI - playGridSize);

                    Rectangle rect = new Rectangle(x + 2, playGridY, playGridSize, playGridSize);
                    if (rect.contains(mouseX, mouseY)) {
                        slot.toggleActive();
                        repaint();
                        break;
                    }
                }
            }
        });




        jLabel.setSize(screenWidth, screenHeight);

        add(jLabel);


        setSize(screenWidth,screenHeight);
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
