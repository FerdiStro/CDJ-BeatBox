package org.main;

import org.deepsymmetry.beatlink.*;
import org.deepsymmetry.beatlink.data.*;
import org.main.midi.MidiColorController;

import javax.sound.midi.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    static Map<Integer, TrackMetadata> metaData =  new HashMap<>();


    public static void main(String[] args) throws SocketException {

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        Frame frame = Frame.getInstance();



        BeatFinder.getInstance().addBeatListener(new BeatListener() {
            @Override
            public void newBeat(Beat beat) {
                if(!metaData.containsKey(beat.getDeviceNumber())){
                    metaData.put(beat.getDeviceNumber(), null);
                }
            }
        });

        BeatFinder.getInstance().start();;

        VirtualCdj cdj = VirtualCdj.getInstance();
        cdj.setDeviceNumber( (byte)  4);
        cdj.setDeviceName("Beat-Box");
        VirtualCdj.getInstance().start();

        cdj.addMasterListener(new MasterListener() {
            int count = 1 ;
            int bigCount = 1;


            @Override
            public void masterChanged(DeviceUpdate update) {
                frame.setMasterDevicdId(update.getDeviceNumber());
            }

            @Override
            public void tempoChanged(double tempo) {
                frame.setMasterTempo(tempo);
            }

            @Override
            public void newBeat(Beat beat) {
                String currentTime = sdf.format(new Date());

                frame.setCounterBeat(count);
                count = (count % 4) + 1;
                frame.setPlayerGridCounterBeat(bigCount);
                bigCount = (bigCount % 8) + 1;
            }
        });

        try {
            MetadataFinder.getInstance().start();
            ArtFinder.getInstance().start();
            WaveformFinder.getInstance().start();
            BeatGridFinder.getInstance().start();
        } catch (Exception e) {
            throw new IllegalStateException();
        }

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateMetaData();
                frame.setMetaData(metaData);



            }
        }, 0, 1000);



        frame.setVisible(true);

    }


    private static  void updateMetaData(){
        MetadataFinder metadataFinder = MetadataFinder.getInstance();
        for(Integer playerNumber : metaData.keySet()){
            TrackMetadata latestMetadataFor = metadataFinder.getLatestMetadataFor(playerNumber);
            metaData.put(playerNumber, latestMetadataFor);;
        }
    }
}