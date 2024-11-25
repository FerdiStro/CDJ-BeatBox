package org.main.util.graphics.components.audio;

import org.main.util.audio.AudioUtils;
import org.main.util.Coordinates;
import org.main.audio.audioplayer.UpdateAudioStream;
import org.main.util.graphics.components.AbstractComponent;
import org.main.audio.audioplayer.AudioFrame;
import org.main.audio.audioplayer.AudioPlayer;

import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.io.IOException;

public class Amplitude extends AbstractComponent implements UpdateAudioStream {

    private  AudioInputStream inputStream;
    private int[] framesAmplitude;

    private int playPos = 0 ;

    public Amplitude(Coordinates coordinates, Dimension dimension, AudioInputStream inputStream) {
        super(coordinates, dimension);
        generateFramesAmplitude(inputStream);

    }


    private void generateFramesAmplitude(AudioInputStream audioInputStream) {
        this.inputStream = audioInputStream;
        try {

            byte[] clonedStream  = inputStream.readAllBytes().clone();

            AudioFrame[] frames = AudioUtils.getFrames(clonedStream, clonedStream.length / inputStream.getFormat().getFrameSize());


            framesAmplitude = new int[frames.length];

            for(int i =0 ; i != frames.length; i++) {

                byte[] channelR = frames[i].getChannel_R();
                byte[] channelL = frames[i].getChannel_L();

                int amplitudeR = AudioUtils.combineBytesBytes24Bit(inputStream.getFormat().isBigEndian(), channelR[0], channelR[1], channelR[2]);
                int amplitudeL = AudioUtils.combineBytesBytes24Bit(inputStream.getFormat().isBigEndian(), channelL[0], channelL[1], channelL[2]);

                int monoAmplitude = (amplitudeL + amplitudeR) / 2;
                framesAmplitude[i] = monoAmplitude;
            }
        } catch (IOException ignored) {
            //ignore
        }

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void draw(Graphics2D g) {
        int x = getX();
        int y = getY();

        double height = getDimension().getHeight();
        double width = getDimension().getWidth();


        double max = Math.min(framesAmplitude.length , AudioPlayer.getInstance().getSampleRate() * 4);


        g.drawRect(x, y , (int) width, (int) height);



        for (int i = 0; i < max - 1; i++) {
            int x1 = (int) ((i / (double) max) * width) + x;
            int x2 = (int) (((i + 1) / (double)max) * width) + x;


            int y1 =  (int) (height / 2 - (framesAmplitude[i] * height / 2 / AudioUtils.getMAX24BIT())) + y;
            int y2 =  (int) (height / 2 - (framesAmplitude[i+1] * height / 2 / AudioUtils.getMAX24BIT())) + y;
            g.drawLine(x1, y1, x2, y2);



            if(i % AudioPlayer.getInstance().getSampleRate() == 0){
                g.setColor(Color.ORANGE);
                g.drawLine(  (int) ((i /  max) * width) + x, y ,  (int) ((i / max) * width) + x, (int) (y +  height));
                g.setColor(Color.BLACK);

            }

            if(i % (AudioPlayer.getInstance().getSampleRate()/2) == 0){
                g.setColor(Color.ORANGE);
                g.drawLine(  (int) ((i /  max) * width) + x , (int) (y  + height/1.3),  (int) ((i / max) * width) + x, (int) ( y  +  height / 4));
                g.setColor(Color.BLACK);

            }








            if(i == playPos){
                g.setColor(Color.RED);
                g.drawLine(i +  x, y, i + x, (int) height + y);
                g.setColor(Color.BLACK);
            }


        }






    }


    @Override
    public void upDateAudioStream(AudioInputStream audioInputStream) {
            generateFramesAmplitude(audioInputStream);
    }
}
