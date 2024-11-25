package org.main.util.audio;

import org.main.audio.audioplayer.AudioFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public  class AudioUtils {

    static final int MAX_24BIT = (1 << 23) - 1;
    static final int MIN_24BIT = -(1 << 23);


    public static int getMAX24BIT(){
        return MAX_24BIT;
    }

    public static int getMIN24BIT(){
        return MIN_24BIT;
    }


    public static   AudioFrame[] getFrames(byte[] bytes, int size) {
        AudioFrame[] frames = new AudioFrame[size];
        for (int frameIndex = 0; frameIndex < size; frameIndex++) {
            int byteIndex = frameIndex * 6;
            byte[] channelLeft = Arrays.copyOfRange(bytes, byteIndex, byteIndex + 3);
            byte[] channelRight = Arrays.copyOfRange(bytes, byteIndex + 3, byteIndex + 6);
            frames[frameIndex] = new AudioFrame(channelLeft, channelRight);
        }
        return frames;
    }

    public  static byte[] getCombined(AudioFrame[] frames) {
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

    public static void normalizeFrames(AudioFrame[] frames, float scaleFactor,  boolean isBigEndian) {
        for(AudioFrame combinedFrame : frames) {

            byte[] channelR = combinedFrame.getChannel_R();
            byte[] channelL = combinedFrame.getChannel_L();

            int combinedR = combineBytesBytes24Bit(isBigEndian, channelR[0], channelR[1], channelR[2]);
            int combinedL = combineBytesBytes24Bit(isBigEndian, channelL[0], channelL[1], channelL[2]);

            if ((combinedR & 0x800000) != 0) {
                combinedR |= 0xFF000000;
            }
            if ((combinedL & 0x800000) != 0) {
                combinedL |= 0xFF000000;
            }

            combinedR = Math.round(combinedR * scaleFactor);
            combinedL = Math.round(combinedL * scaleFactor);

            combinedR = Math.max(Math.min(combinedR, MAX_24BIT), MIN_24BIT);
            combinedL = Math.max(Math.min(combinedL, MAX_24BIT), MIN_24BIT);


            channelR[0] = (byte)(combinedR & 0xFF);
            channelR[1] = (byte) ((combinedR >> 8) & 0xFF);
            channelR[2] = (byte) ((combinedR >> 16) & 0xFF);

            channelL[0] = (byte)(combinedL & 0xFF);
            channelL[1] = (byte) ((combinedL >> 8) & 0xFF);
            channelL[2] = (byte) ((combinedL >> 16) & 0xFF);

        }
    }




    public static int combineBytesBytes24Bit(boolean isBidEnding, byte b1, byte b2, byte b3) {
        if(isBidEnding){
            return combineBytesBigEndian24Bit(b1, b2, b3);
        }
        return combineBytesLittleEndian24Bit(b1, b2, b3);
    }

    private static int combineBytesLittleEndian24Bit(byte b1, byte b2, byte b3) {
        int result = (b1 & 0xFF) |
                ((b2 & 0xFF) << 8) |
                ((b3 & 0xFF) << 16);

        if ((b3 & 0x80) != 0) {
            result |= 0xFF000000;
        }
        return result;
    }

    private   static int combineBytesBigEndian24Bit(byte b1, byte b2, byte b3) {
        int result = ((b1 & 0xFF) << 16) |
                ((b2 & 0xFF) << 8) |
                (b3 & 0xFF);
        if ((b1 & 0x80) != 0) {
            result |= 0xFF000000;
        }
        return result;
    }


    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }



}
