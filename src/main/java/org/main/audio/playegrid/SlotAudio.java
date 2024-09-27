package org.main.audio.playegrid;


import org.main.Frame;
import org.main.audio.SHOT_TYPE;
import org.main.audio.library.TYPE;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import org.main.util.Logger;

import static java.lang.Thread.sleep;

public class SlotAudio {

    private final TYPE playType;
    private final File audioFile;
    private final String name;
    private float volume = 0.5f;

   public SlotAudio(File audioFile, TYPE playType){
       List<String> splitPath = Arrays.stream(audioFile.getPath().split("/")).toList();
       this.name = splitPath.get(splitPath.size()-1);
       this.audioFile = audioFile;
       this.playType = playType;
   }

    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        this.volume = volume;
    }

    private boolean isPlaying = true;


   //todo: add dynamic audio
    public synchronized  void  play(SHOT_TYPE shotType){
        Thread playThread = new Thread(() -> {
            try {
                if(isPlaying){
                    this.isPlaying = false;
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    AudioFormat format = audioStream.getFormat();
                    byte[] audioBytes = audioStream.readAllBytes();

                    adjustVolume(audioBytes, format, volume);


                    Clip clip = AudioSystem.getClip();
                    clip.open(format, audioBytes, 0, audioBytes.length);

                    /*
                        Play one beat
                     */
                    if(shotType == SHOT_TYPE.ONE_BEST){
                        clip.start();

                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                clip.close();
                                isPlaying = true;
                                Thread.currentThread().interrupt();

                            }
                        });
                    }
                    /*
                        Play two beats
                     */
                    if(shotType == SHOT_TYPE.TWO_BEAT){
                        double waitTimer = (1000  / (Frame.getInstance().getMasterTempo() /  60)) / 2;

                        Clip twoClip = AudioSystem.getClip();
                        twoClip.open(format, audioBytes, 0, audioBytes.length);

                        clip.start();

                        sleep((long) waitTimer);

                        twoClip.start();

                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                clip.close();
                            }
                        });

                        twoClip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                twoClip.close();
                                isPlaying = true;
                                Thread.currentThread().interrupt();

                            }
                        });
                    }

                    /*
                        Play four beats
                     */
                    if(shotType == SHOT_TYPE.FOUR_BEAT){
                        double waitTimer = (1000  / (Frame.getInstance().getMasterTempo() /  60)) / 4;

                        Clip twoClip = AudioSystem.getClip();
                        Clip treeClip = AudioSystem.getClip();
                        Clip fourClip = AudioSystem.getClip();

                        twoClip.open(format, audioBytes, 0, audioBytes.length);
                        treeClip.open(format, audioBytes, 0, audioBytes.length);
                        fourClip.open(format, audioBytes, 0, audioBytes.length);

                        clip.start();
                        sleep((long) waitTimer);
                        twoClip.start();
                        sleep((long) waitTimer);
                        treeClip.start();
                        sleep((long) waitTimer);
                        fourClip.start();

                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                clip.close();
                            }
                        });
                        twoClip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                twoClip.close();
                            }
                        });
                        treeClip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                treeClip.close();
                            }
                        });
                        fourClip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                fourClip.close();
                                isPlaying = true;
                                Thread.currentThread().interrupt();


                            }
                        });
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();

    }

    private void adjustVolume(byte[] audioBytes, AudioFormat format, float volume) {

       if (format.getSampleSizeInBits() == 16) {
            for (int i = 0; i < audioBytes.length; i += 2) {
                short sample = (short) ((audioBytes[i+1] << 8) | (audioBytes[i] & 0xFF));
                sample = (short) (sample * volume);
                if (sample > Short.MAX_VALUE) {
                    sample = Short.MAX_VALUE;
                } else if (sample < Short.MIN_VALUE) {
                    sample = Short.MIN_VALUE;
                }
                audioBytes[i] = (byte) (sample & 0xFF);
                audioBytes[i+1] = (byte) ((sample >> 8) & 0xFF);
            }
        }
        if (format.getSampleSizeInBits() == 24) {
            for (int i = 0; i < audioBytes.length; i += 3) {
                int sample = (audioBytes[i+2] << 16) | ((audioBytes[i+1] & 0xFF) << 8) | (audioBytes[i] & 0xFF);
                float sampleFloat = (float) sample / (float) 8388608;
                sampleFloat *= volume;
                sample = (int) (sampleFloat * 8388608);
                if (sample > 8388607) {
                    sample = 8388607;
                } else if (sample < -8388608) {
                    sample = -8388608;
                }
                audioBytes[i] = (byte) (sample & 0xFF);
                audioBytes[i+1] = (byte) ((sample >> 8) & 0xFF);
                audioBytes[i+2] = (byte) ((sample >> 16) & 0xFF);
            }
        }
    }

    public String getName() {
        return name;
    }

    public TYPE getPlayType() {
        return playType;
    }
}
