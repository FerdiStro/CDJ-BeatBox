package testSampler.Audio;

import org.main.util.Logger;

import javax.sound.sampled.*;
import java.io.IOException;


public class Audio {

    private  AudioInputStream audioInputStream;

    private MutableByteArrayInputStream mutableStream;


    public Audio(AudioInputStream audioInputStream){
        this.audioInputStream = audioInputStream;
    }


    public void play() throws LineUnavailableException, IOException {
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(format);
        sourceLine.start();

        byte[] audioBytes = audioInputStream.readAllBytes();
        MutableByteArrayInputStream mutableStream = new MutableByteArrayInputStream(audioBytes);

        int frameSize = format.getFrameSize();
        byte[] buffer = new byte[frameSize * 1024];

        int bytesRead;
        while ((bytesRead = mutableStream.read(buffer)) != -1) {
            int validBytes = (bytesRead / frameSize) * frameSize;
            if (validBytes > 0) {

                mutableStream.modifyData(buffer);

                sourceLine.write(buffer, 0, validBytes);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }






    public void muteLeft() {
        if (mutableStream != null) {
//            mutableStream.muteLeft();
        } else {
            Logger.info("AudioStream has not been started yet.");
        }
    }

}
