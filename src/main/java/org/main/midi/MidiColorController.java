package org.main.midi;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.card.service.lib.ActivateColorRequest;
import net.devh.boot.grpc.card.service.lib.ActivateColorResponse;
import net.devh.boot.grpc.card.service.lib.MidiServiceGrpc;
import net.devh.boot.grpc.card.service.lib.MidiServiceGrpc.MidiServiceStub;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class MidiColorController {

    private static MidiColorController INSTANCE;
    private MidiServiceGrpc.MidiServiceStub grpcStub;

    public static MidiColorController getInstance(){
        if(INSTANCE==null){
            INSTANCE =  new MidiColorController();
        }
        return INSTANCE;
    }

    private Process process;
    private final Object processLock = new Object();


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
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }

                    int exitCode = process.waitFor();
                    System.out.println("Process exited with code: " + exitCode);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
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
                        makeRequestWithRetry(activateColorRequest, key);
                    }
                }catch (Exception ignore){}
            }
        }, 0, 10);

    }

    private void makeRequestWithRetry(ActivateColorRequest request, int pad) {
        grpcStub.activateColor(request, new StreamObserver<>() {
            @Override
            public void onNext(ActivateColorResponse response) {
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
                makeRequestWithRetry(request, pad);
            }

            @Override
            public void onCompleted() {
            }
        });
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
