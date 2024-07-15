package org.main.audio.playegrid;


import javax.sound.sampled.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SlotAudio {
    private final File audioFile;
    private final String name;
    private float volume = 0.5f;

   public SlotAudio(File audioFile){
       List<String> splitPath = Arrays.stream(audioFile.getPath().split("/")).toList();
       this.name = splitPath.get(splitPath.size()-1);
       this.audioFile = audioFile;
   }

    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        this.volume = volume;
    }

    public void play(){
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            byte[] audioBytes = audioStream.readAllBytes();

            adjustVolume(audioBytes, format, volume);

            Clip clip = AudioSystem.getClip();
            clip.open(format, audioBytes, 0, audioBytes.length);
            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
