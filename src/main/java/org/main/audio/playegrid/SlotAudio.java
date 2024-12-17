package org.main.audio.playegrid;


import lombok.Getter;
import org.main.BeatBoxWindow;
import org.main.audio.library.TYPE;
import org.main.audio.metadata.SlotAudioMetaData;
import org.main.audio.plugin.PluginManager;
import org.main.audio.plugin.model.Plugin;
import org.main.util.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class SlotAudio {

    @Getter
    private final TYPE playType;
    @Getter
    private final transient File audioFile;
    @Getter
    private final String name;
    private float volume = 0.5f;


    private SlotAudioMetaData audioMetaData;

    public SlotAudio(SlotAudio slotAudio, TYPE playType) {
        this.playType = playType;
        this.audioFile = slotAudio.getAudioFile();
        this.name = slotAudio.getName();
    }

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



   //todo: add dynamic audio
    public synchronized  void  play(){
        Thread playThread = new Thread(() -> {
            try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    AudioFormat format = audioStream.getFormat();
                    byte[] audioBytes = audioStream.readAllBytes();

                    adjustVolume(audioBytes, format, volume);


                    Clip clip = AudioSystem.getClip();
                    clip.open(format, audioBytes, 0, audioBytes.length);

                    /*
                        Play one beat
                     */
                    if(playType == TYPE.ONE_BEST || playType == TYPE.SOUND || playType == TYPE.ONESHOOT){
                        clip.start();

                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                clip.close();
                                Thread.currentThread().interrupt();

                            }
                        });
                    }
                    /*
                        Play two beats
                     */
                    if(playType == TYPE.TWO_BEAT){
                        double waitTimer = (1000  / (BeatBoxWindow.getInstance().getMasterTempo() /  60)) / 2;

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
                                Thread.currentThread().interrupt();

                            }
                        });
                    }

                    /*
                        Play four beats
                     */
                    if(playType == TYPE.FOUR_BEAT){
                        double waitTimer = (1000  / (BeatBoxWindow.getInstance().getMasterTempo() /  60)) / 4;

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
                                Thread.currentThread().interrupt();


                            }
                        });
                    }


                    if(playType == TYPE.PLUGIN_TYPE){
                        Logger.notImplemented("Plugin not implement on old SlotAudio system. Change to AudioPlayer ");

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

    private static final int MIDI_ONE_BEAT_ID = 36;
    private static final int MIDI_TWO_BEAT_ID = 37;
    private static final int MIDI_FOUR_BEAT_ID = 38;

    public static  SlotAudio getWithType(SlotAudio slotAudio, int i){
        if (i == MIDI_ONE_BEAT_ID || i == 1) {
            return new SlotAudio(slotAudio, TYPE.ONE_BEST);
        }
        if (i == MIDI_TWO_BEAT_ID || i == 2) {
            return new SlotAudio(slotAudio, TYPE.TWO_BEAT);
        }
        if (i == MIDI_FOUR_BEAT_ID || i == 3) {
            return new SlotAudio(slotAudio, TYPE.FOUR_BEAT);
        }
        PluginManager pluginManager = PluginManager.getInstance();

        for (Plugin plugin : pluginManager.getPlugins()) {

            if(i == plugin.getPosMIDI() ||  i == plugin.getPosPad() ){
                return new SlotAudio(slotAudio, TYPE.PLUGIN_TYPE);
            }
        }


        Logger.error("Unknown slot audio type");
        return null;
    }

}
