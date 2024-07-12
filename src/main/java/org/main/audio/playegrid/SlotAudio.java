package org.main.audio.playegrid;


import org.main.audio.LoadLibrary;

import javax.sound.sampled.*;

public class SlotAudio {
    private LoadLibrary soundLibrary = LoadLibrary.getInstance();

    private Clip clip;

    public void test(Clip clip) throws Exception{
            byte[] buffer = new byte[1024];
            int bytesRead;

            AudioInputStream ais = soundLibrary.getAudioStream();
            //todo: fix audio level
            while ((bytesRead = ais.read(buffer, 0, buffer.length)) != -1) {
                int level = calculateRMSLevel(buffer, bytesRead, soundLibrary.getAudioStream().getFormat());
                System.out.println("Audio Level: " + level);
                Thread.sleep(100);
            }

    }

    private int calculateRMSLevel(byte[] buffer, int bytesRead, AudioFormat format) {
        long sum = 0;
        int sampleSize = format.getSampleSizeInBits() / 8;
        boolean isBigEndian = format.isBigEndian();
        boolean isSigned = format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
        for (int i = 0; i < bytesRead; i += sampleSize) {
            int sample = 0;
            for (int j = 0; j < sampleSize; j++) {
                int v = buffer[i + j];
                if (isBigEndian) {
                    sample |= (v & 0xFF) << ((sampleSize - j - 1) * 8);
                } else {
                    sample |= (v & 0xFF) << (j * 8);
                }
            }
            if (isSigned) {
                int signShift = (8 * sampleSize) - 1;
                sample = sample << signShift >> signShift;
            }
            sum += sample * sample;
        }
        double rms = Math.sqrt(sum / (bytesRead / sampleSize));
        return (int) (20 * Math.log10(rms));
    }




    public void play(){
        if( soundLibrary!= null){
            clip = soundLibrary.getClip();

            clip.start();

            new Thread(() -> {
                try {
                    test(clip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            clip.addLineListener( event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

        }
    }

}
