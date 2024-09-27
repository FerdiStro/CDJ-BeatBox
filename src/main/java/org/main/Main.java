package org.main;

import org.deepsymmetry.beatlink.*;
import org.deepsymmetry.beatlink.data.*;
import org.main.settings.CDJSettings;
import org.main.settings.Settings;
import org.main.settings.SettingsFrame;
import org.main.util.Logger;

import javax.swing.*;

import java.awt.*;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

import static java.lang.Thread.sleep;


public class Main {

    static Map<Integer, TrackMetadata> metaData = new HashMap<>();


    public static void main(String[] args) throws SocketException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {



        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Toolkit.getDefaultToolkit().sync();

        Frame frame = Frame.getInstance();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        setUp();



    }


    private static void setUp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");


        Frame frame = Frame.getInstance();
        CDJSettings cdjSettings = Settings.getInstance().getCdjSettings();

        if (cdjSettings.isUseCdj()) {
            Thread findeDevice = new Thread(new Runnable() {

                int attempts = 0;
                final int sleepTimer = cdjSettings.getSleepTimer();

                @Override
                public void run() {
                    Logger.debug("Attempting to connect to CDJ: " + attempts);
                    try {
                        DeviceFinder.getInstance().start();
                        sleep(sleepTimer);
                        if (DeviceFinder.getInstance().getCurrentDevices().isEmpty()) {
                            attempts++;
                            if (attempts < cdjSettings.getMaxAttempts()) {
                                run();
                            } else {
                                Logger.debug("Max attempts reached: " + attempts);
                                cdjSettings.setUseCDJ(false);
                                setUp();
                            }
                        } else {
                            BeatFinder.getInstance().addBeatListener(new BeatListener() {
                                @Override
                                public void newBeat(Beat beat) {
                                    if (!metaData.containsKey(beat.getDeviceNumber())) {
                                        metaData.put(beat.getDeviceNumber(), null);
                                    }
                                }
                            });
                            BeatFinder.getInstance().start();
                            VirtualCdj cdj = VirtualCdj.getInstance();
                            cdj.setDeviceNumber((byte) 4);
                            cdj.setDeviceName("Beat-Box");
                            VirtualCdj.getInstance().start();

                            cdj.addMasterListener(new MasterListener() {
                                int count = 1;
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
                                Logger.info("testy");

                                MetadataFinder.getInstance().start();
                                ArtFinder.getInstance().start();
                                WaveformFinder.getInstance().start();
                                BeatGridFinder.getInstance().start();
                            } catch (Exception e) {
                                System.out.println("illegal");
                                throw new IllegalStateException();
                            }

                            new Timer().scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    updateMetaData();
                                    frame.setMetaData(metaData);
                                    Settings.getInstance().update();
                                }
                            }, 0, 1000);
                        }
                    } catch (SocketException e) {
                        Logger.error("Failed to start connection to CDJ");
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            findeDevice.start();
        }else{

            /*
                Beat without cdj
             */
            Thread beatGiver = new Thread(new Runnable() {
                int count = 1;
                int bigCount = 1;

                int bpm =  120;


                // bpm / 60 = bps ;  bps/1000  = bpms


                @Override
                public void run() {
                    Logger.debug("Start offline BeatBox with "+ bpm + " BPM" ) ;

                    frame.setMasterTempo(bpm);

                    /*
                        Update Seting-frame
                    */
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            Settings.getInstance().update();
                        }
                    }, 0, 100);

                    while(true){
                        try {
                            int sleepTimer  =  60000  /  bpm;

                            sleep(sleepTimer);


                            frame.setSetupString(false);

                            frame.setCounterBeat(count);
                            count = (count % 4) + 1;
                            frame.setPlayerGridCounterBeat(bigCount);
                            bigCount = (bigCount % 8) + 1;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }
            });

            beatGiver.start();

        }
    }


    private static void updateMetaData() {
        MetadataFinder metadataFinder = MetadataFinder.getInstance();
        for (Integer playerNumber : metaData.keySet()) {
            TrackMetadata latestMetadataFor = metadataFinder.getLatestMetadataFor(playerNumber);
            metaData.put(playerNumber, latestMetadataFor);
            ;
        }
    }
}