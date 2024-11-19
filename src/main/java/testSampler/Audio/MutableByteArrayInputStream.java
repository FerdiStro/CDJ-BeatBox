package testSampler.Audio;

import java.io.ByteArrayInputStream;
import java.util.Arrays;



public class MutableByteArrayInputStream extends ByteArrayInputStream {



    public MutableByteArrayInputStream(byte[] buf) {
        super(buf);
    }



    public void modifyData(byte[] buffer){
        AudioFrame[] frames = getFrames(buffer,buffer.length /  6);
        for(AudioFrame frame : frames){


            //todo: modify here


            frame.muteChannel_R();
            frame.muteChannel_L();


        }

        byte[] modifiedBuffer = getCombined(frames);

        System.arraycopy(modifiedBuffer, 0, buffer, 0, buffer.length);


    }


    public  AudioFrame[] getFrames(byte[] bytes, int size) {
        AudioFrame[] frames = new AudioFrame[size];
        for (int frameIndex = 0; frameIndex < size; frameIndex++) {
            int byteIndex = frameIndex * 6;
            byte[] channelLeft = Arrays.copyOfRange(bytes, byteIndex, byteIndex + 3);
            byte[] channelRight = Arrays.copyOfRange(bytes, byteIndex + 3, byteIndex + 6);
            frames[frameIndex] = new AudioFrame(channelLeft, channelRight);
        }
        return frames;
    }

    public byte[] getCombined(AudioFrame[] frames) {
        int totalBytes = frames.length * 6;
        byte[] resultBytes = new byte[totalBytes];
        int byteIndex = 0;
        for (AudioFrame frame : frames) {
            System.arraycopy(frame.getChannel_L(), 0, resultBytes, byteIndex, 3);byteIndex += 3;
            System.arraycopy(frame.getChannel_R(), 0, resultBytes, byteIndex, 3);
            byteIndex += 3;
        }
        return  resultBytes;
    }




}
