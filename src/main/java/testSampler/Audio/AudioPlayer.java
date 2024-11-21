package testSampler.Audio;

import org.main.util.Logger;
import testSampler.Util;

import javax.sound.sampled.*;
import java.io.IOException;


public class AudioPlayer {

    public void play(AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(format);
        sourceLine.start();

        byte[] audioBytes = audioInputStream.readAllBytes();
        MutableByteArrayInputStream mutableStream = new MutableByteArrayInputStream(audioBytes);

        int frameSize = format.getFrameSize();
        byte[] buffer = new byte[frameSize * 1024];



        Thread thread = new Thread(() -> {
                int bytesRead;

                while (true) {
                    try {
                        if (!((bytesRead = mutableStream.read(buffer)) != -1)) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    int validBytes = (bytesRead / frameSize) * frameSize;
                    if (validBytes > 0) {

                        mutableStream.modifyData(buffer);

                        sourceLine.write(buffer, 0, validBytes);
                    }
                }
                sourceLine.drain();
                sourceLine.close();
        });
        thread.start();

    }


    public AudioInputStream combine(AudioInputStream audioInputStream_1, AudioInputStream audioInputStream_2) {
       if(!audioInputStream_1.getFormat().equals(audioInputStream_1.getFormat())) return null;


       try {
           byte[] audioBytes_1 = audioInputStream_1.readAllBytes().clone();
           byte[] audioBytes_2 = audioInputStream_2.readAllBytes().clone();

           AudioFrame[] frames_1 = Util.getFrames(audioBytes_1, audioBytes_1.length / audioInputStream_1.getFormat().getFrameSize());
           AudioFrame[] frames_2 = Util.getFrames(audioBytes_2, audioBytes_2.length / audioInputStream_1.getFormat().getFrameSize());


           int maxFrameRange = Integer.max(frames_1.length, frames_2.length);

           AudioFrame[] combinedFrames = new AudioFrame[maxFrameRange];

           for(int i = 0; i != maxFrameRange ; i ++){
               AudioFrame frameI_1 = null;
               AudioFrame frameI_2 = null;

               try {
                   frameI_1 = frames_1[i];
               }catch (Exception ignore){}
               try {
                   frameI_2 = frames_2[i];
               }catch (Exception ignore){}



               if(frameI_1 != null && frameI_2 != null){

                   byte[] zChannelR = new byte[3];
                   byte[] zChannelL = new byte[3];

                   for(int k  = 0 ; k != 3;  k++){
                       byte bR_1 = frameI_1.getChannel_R()[k];
                       byte bR_2 = frameI_2.getChannel_R()[k];

                       byte bL_1 = frameI_1.getChannel_L()[k];
                       byte bL_2 = frameI_2.getChannel_L()[k];

                       byte zR =  (byte)  Integer.max(Integer.min(bR_1 + bR_2, 2^(int)audioInputStream_1.getFormat().getSampleRate()), - 2^(int)audioInputStream_1.getFormat().getSampleRate()-1);
                       byte zL =  (byte)   Integer.max(Integer.min(bL_1 + bL_2, 2^(int)audioInputStream_1.getFormat().getSampleRate()), - 2^(int)audioInputStream_1.getFormat().getSampleRate()-1);

                       zChannelL[k] = zL;
                       zChannelR[k] = zR;
                   }

                   combinedFrames[i]  =  new AudioFrame( zChannelL, zChannelR);

               }
               if(frameI_1 == null && frameI_2 != null ){
                   combinedFrames[i]  =  new AudioFrame( frameI_2.getChannel_L(), frameI_2.getChannel_R());

               }
               if(frameI_1 != null && frameI_2 == null ){
                   combinedFrames[i]  =  new AudioFrame( frameI_1.getChannel_L(), frameI_1.getChannel_R());

               }
           }

           Logger.info("Audio stream has been started.");

           byte[] combinedByteStream = Util.getCombined(combinedFrames);

           MutableByteArrayInputStream mutableByteArrayInputStream = new MutableByteArrayInputStream(combinedByteStream);

           return new AudioInputStream(mutableByteArrayInputStream, audioInputStream_1.getFormat(),maxFrameRange);

       }catch (Exception e){
           Logger.error(e.getMessage());
       }
        return null;
    }






}
