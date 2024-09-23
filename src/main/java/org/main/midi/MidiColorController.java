package org.main.midi;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.card.service.lib.ActivateColorRequest;
import net.devh.boot.grpc.card.service.lib.ActivateColorResponse;
import net.devh.boot.grpc.card.service.lib.MidiServiceGrpc;
import org.main.Frame;
import org.main.util.Logger;

import javax.sound.midi.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

import static java.lang.Thread.sleep;

public class MidiColorController {

    private static MidiColorController INSTANCE;
    private MidiServiceGrpc.MidiServiceStub grpcStub;

    public static MidiColorController getInstance(){
        if(INSTANCE==null){
            INSTANCE =  new MidiColorController();
            Logger.init(INSTANCE.getClass());
        }
        return INSTANCE;
    }

    private Process process;
    private final Object processLock = new Object();

    private boolean grpcUP = false;


    private MidiColorController() {

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
                        System.out.println(line);
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

        setTransmitter();
    }

    private Receiver receiver;

    public void setReceiver(Receiver receiver){
        this.receiver = receiver;
    }

    private void setTransmitter(){
        Thread setTranmitterThread  = new Thread(new Runnable() {
            int count = 5;
            @Override
            public void run()  {
                try {
                  Transmitter transmitter =   getTransmitter();
                  transmitter.setReceiver(receiver);
                }catch (Exception e){
                    try {
                        System.out.println("Transmitter failed to start, retry in "+count+"s : " + e.getMessage());
                        sleep(count * 1000L);
                        System.out.println(count);
                        count = count + 2;
                        if(count < 60){
                            run();
                        }else{
                            System.out.println("To many attempts, no Midi-device found");
                        }
                    } catch (Exception treadProblem) {
                        treadProblem.printStackTrace();
                    }
                }
            }
        });
        setTranmitterThread.start();
    }

    private Transmitter getTransmitter() throws Exception{

        List<MidiDevice> devicesList = new ArrayList<>();

        for (MidiDevice.Info device : MidiSystem.getMidiDeviceInfo()) {
            if (device.getName().contains("Arturia MiniLab mkII")) {
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




    public Integer padMapper(int originalPad){
        return switch (originalPad) {
            case 36 -> 1;
            case 37 -> 2;
            case 38 -> 3;
            case 39 -> 4;
            case 40 -> 5;
            case 41 -> 6;
            case 42 -> 7;
            case 43 -> 8;
            default -> 0;
        };
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
