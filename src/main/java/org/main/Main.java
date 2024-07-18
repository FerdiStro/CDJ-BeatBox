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


//                System.out.println("Current time: " + currentTime + ", Beat: " + count);
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

        try {
            List<MidiDevice> devicesList = new ArrayList<>();

            for (MidiDevice.Info device : MidiSystem.getMidiDeviceInfo()) {
                if (device.getName().contains("Arturia MiniLab mkII")) {
                    devicesList.add(MidiSystem.getMidiDevice(device));
                }
            }

//            devicesList.forEach(midiDevice -> {
//                try {
//                    midiDevice.open();
//                } catch (MidiUnavailableException e) {
//                    throw new RuntimeException(e);
//                }
//            });




//                transmitter.setReceiver(new Receiver() {
//                    @Override
//                    public void send(MidiMessage message, long timeStamp) {
//                        if (message instanceof ShortMessage) {
//                            ShortMessage sm = (ShortMessage) message;
//                            System.out.println("Command: " + sm.getCommand() + " Channel: " + sm.getChannel() +
//                                    " Data1: " + sm.getData1() + " Data2: " + sm.getData2());
//                        }
//                    }
//                    @Override
//                    public void close() {
//                    }
//                });


        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private static  void updateMetaData(){
        MetadataFinder metadataFinder = MetadataFinder.getInstance();
        for(Integer playerNumber : metaData.keySet()){
            TrackMetadata latestMetadataFor = metadataFinder.getLatestMetadataFor(playerNumber);
            metaData.put(playerNumber, latestMetadataFor);;
        }
    }
}