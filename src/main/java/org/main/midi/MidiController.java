package org.main.midi;



import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.card.service.lib.ActivateColorRequest;
import net.devh.boot.grpc.card.service.lib.ActivateColorResponse;
import net.devh.boot.grpc.card.service.lib.MidiServiceGrpc;
import org.main.settings.Settings;
import org.main.settings.objects.MidiControllerSettings;
import org.main.util.Logger;

import javax.sound.midi.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

import static java.lang.Thread.sleep;

public class MidiController {

    private static MidiController INSTANCE;
    private MidiServiceGrpc.MidiServiceStub grpcStub;

    private static  MidiControllerSettings settings;



    public static MidiController getInstance(MidiControllerSettings settings) {
        if(INSTANCE==null){
            MidiController.settings = settings;
            INSTANCE =  new MidiController();
            Logger.init(INSTANCE.getClass());
        }
        return INSTANCE;
    }





    private MidiController() {
        startGrpcColor();
        setTransmitter();
    }


    /*
        Transmitter
    */
    public void setTransmitter(){
        String transmitterName = settings.getMidiControllerName();

        Logger.debug("Transmitter set with name: " + transmitterName);


        if(transmitterName == null || transmitterName.isEmpty()){
            List<String> midiTransmittersNamesList = getMidiTransmittersNamesList();
            if(midiTransmittersNamesList == null || midiTransmittersNamesList.isEmpty()){
                Logger.error("No MidiTransmitter found");
                return;
            }
        }


        Thread setTranmitterThread  = new Thread(new Runnable() {
            int count = 5;
            @Override
            public void run()  {
                try {

                    MidiController.transmitter = getOpenTransmitter();
                    if(MidiController.transmitter != null){
                        MidiController.transmitter.setReceiver(receiver);
                    }
                }catch (Exception e){
                    try {
                        Logger.debug("Transmitter failed to start, retry in "+count+"s : " + e.getMessage());
                        sleep(count * 1000L);
                        Logger.debug(String.valueOf(count));
                        count = count + 2;
                        if(count < 60){
                            run();
                        }else{
                            Logger.error("To many attempts, no Midi-device found");
                        }
                    } catch (Exception treadProblem) {
                        treadProblem.printStackTrace();
                    }
                }
            }
        });
        setTranmitterThread.start();
    }



    private static Transmitter transmitter;
    private static Receiver receiver;


    public void setReceiver(Receiver receiver){
        MidiController.receiver = receiver;
        if(transmitter != null){
            transmitter.setReceiver(receiver);
        }
    }

    /*
        Transmitter
     */
    public List<String> getMidiTransmittersNamesList(){
        List<String> midiTransmittersNamesList = new ArrayList<>();

        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        for(MidiDevice.Info deviceInfo : midiDeviceInfo){
            try {
                MidiDevice midiDevice = MidiSystem.getMidiDevice(deviceInfo);
                if(midiDevice.getMaxReceivers()  >= 0 ){
                    midiTransmittersNamesList.add(deviceInfo.getName());
                }
            } catch (MidiUnavailableException e) {
                Logger.error("Error while reading Midi List in Settings.class");
                throw new RuntimeException(e);
            }
        }
        if(midiTransmittersNamesList.isEmpty()){
            midiTransmittersNamesList.add("NO-Midi");
        }
        return midiTransmittersNamesList;
    }


    private Transmitter getOpenTransmitter() throws Exception{
        List<MidiDevice> devicesList = new ArrayList<>();
        for (MidiDevice.Info device : MidiSystem.getMidiDeviceInfo()) {
            if (device.getName().contains(settings.getMidiControllerName())) {
                devicesList.add(MidiSystem.getMidiDevice(device));
            }
        }
        devicesList.forEach(midiDevice -> {
            try {
                midiDevice.open();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }
        });
        return devicesList.get(1).getTransmitter();
    }

    /*
        Grpc-Color Switch
     */
    private Process process;
    private final Object processLock = new Object();

    private boolean grpcUP = false;

    private void makeRequestWithRetry(ActivateColorRequest request, int errorAttempts) {
        if(grpcUP){
            grpcStub.activateColor(request, new StreamObserver<>() {

                @Override
                public void onNext(ActivateColorResponse response) {
                }

                @Override
                public void onError(Throwable t) {
                    int e = errorAttempts + 1;

                    try {
                        if(errorAttempts < 5){
                            sleep(5);
                            makeRequestWithRetry(request, e);
                        }
                    } catch (Exception ignore) {
                    }
                }

                @Override
                public void onCompleted() {
                }
            });
        }
    }

    private void startGrpcColor() {
        Thread thread = getGrpcThread();
        thread.start();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        grpcStub = MidiServiceGrpc.newStub(channel);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (processLock) {
                if (process != null && process.isAlive()) {
                    process.destroy();
                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Process terminated by shutdown hook.");
                }
            }
        }));

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    for (Integer key : requestQue.keySet()) {
                        ActivateColorRequest activateColorRequest = requestQue.get(key);
                        makeRequestWithRetry(activateColorRequest, 0);
                    }
                }catch (Exception ignore){}
            }
        }, 0, 10);
    }

    private Thread getGrpcThread() {
        String binaryPath = "src/main/java/org/main/midi/color/binSendToMiniLabMk2";

        Runnable binaryRunner = new Runnable() {
            @Override
            public void run() {
                ProcessBuilder processBuilder = new ProcessBuilder(binaryPath);

                try {
                    synchronized (processLock) {
                        process = processBuilder.start();
                    }


                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    grpcUP = true;


                    while ((line = reader.readLine()) != null) {
//                        System.out.println(line);
                    }

                    int exitCode = process.waitFor();
                    System.out.println("Python MIDI-Server exited with code: " + exitCode);
                    grpcUP = false;

                } catch (IOException | InterruptedException e) {
                    Logger.error(e.toString());
                }
            }
        };


        Thread thread = new Thread(binaryRunner);
        return thread;
    }

    private HashMap<Integer, ActivateColorRequest> requestQue =  new HashMap<>();


    public void switchColorAsync(int pad, String color) {
        ActivateColorRequest request = ActivateColorRequest.newBuilder()
                .setPad(String.valueOf(pad))
                .setColor(color)
                .build();
        requestQue.put(pad, request);
    }


}
