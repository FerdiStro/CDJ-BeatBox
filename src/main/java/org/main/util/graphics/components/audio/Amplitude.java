package org.main.util.graphics.components.audio;

import lombok.Setter;
import org.main.util.audio.AudioUtils;
import org.main.util.Coordinates;
import org.main.audio.audioplayer.UpdateAudioStream;
import org.main.util.graphics.components.AbstractComponent;
import org.main.audio.audioplayer.AudioFrame;
import org.main.audio.audioplayer.AudioPlayer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Amplitude extends AbstractComponent implements UpdateAudioStream {

    private AudioInputStream inputStream;
    private int[] framesAmplitude;

    @Setter
    private int playPos = 0;

    public Amplitude(Coordinates coordinates, Dimension dimension, AudioInputStream inputStream) {
        super(coordinates, dimension);
        generateFramesAmplitude(inputStream);

    }


    private void generateFramesAmplitude(AudioInputStream audioInputStream) {
        this.inputStream = audioInputStream;
        try {

            byte[] clonedStream = inputStream.readAllBytes().clone();

            AudioFrame[] frames = AudioUtils.getFrames(clonedStream, clonedStream.length / inputStream.getFormat().getFrameSize());


            framesAmplitude = new int[frames.length];

            for (int i = 0; i != frames.length; i++) {

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
        waveFormBufferChange = true;
    }


    private boolean waveFormBufferChange;
    @Setter
    private boolean waveFormBufferChangeRender;
    private BufferedImage waveformImage;
    private boolean wirteToFile = true;

    private void renderWaveBufferNew(int width, int height) {
        if (waveFormBufferChange) {
            waveformImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = waveformImage.createGraphics();
            g.setColor(Color.black);

            double max = Math.min(framesAmplitude.length, AudioPlayer.getInstance().getSampleRate() * 4);

            for (int i = 0; i < max - 1; i++) {
                int x1 = (int) ((i / (double) max) * width);
                int x2 = (int) (((i + 1) / (double) max) * width);


                int y1 = (height / 2 - (framesAmplitude[i] * height / 2 / AudioUtils.getMAX24BIT()));
                int y2 = (height / 2 - (framesAmplitude[i + 1] * height / 2 / AudioUtils.getMAX24BIT()));
                g.drawLine(x1, y1, x2, y2);

                if (i == 0 || i == max - 2 || i  ==  max / 2   ) {
                    g.setColor(Color.ORANGE);
                    g.drawLine((int) ((i / max) * width), 0, (int) ((i / max) * width), (int) (+height));
                    g.setColor(Color.BLACK);

                }

                if (i % (AudioPlayer.getInstance().getSampleRate() / 2) == 0) {
                    g.setColor(Color.ORANGE);
                    g.drawLine((int) ((i / max) * width), (int) (height / 1.3), (int) ((i / max) * width), (int) +height / 4);
                    g.setColor(Color.BLACK);

                }


            }
            g.dispose();

            if (wirteToFile) {
                try {
                    File outputfile = new File("saved.png");
                    ImageIO.write(waveformImage, "png", outputfile);
                } catch (IOException ignore) {
                }
            }
            waveFormBufferChange = false;
            waveFormBufferChangeRender = false;
        }
    }

    @Override
    public void draw(Graphics2D g) {

        int x = getX();
        int y = getY();

        double height = getDimension().getHeight();
        double width = getDimension().getWidth();


        g.drawImage(waveformImage, x, y, null);




        if (waveFormBufferChangeRender) {
            g.setColor(new Color(230, 80, 25, 168));
            g.fillRect(x, y, (int) width, (int) height);

            g.setColor(Color.BLACK);
            g.drawString("Render", x, y );

        }

        g.setColor(Color.RED);
        if(playPos == 8 ){
            g.drawLine(x, y , x, (int) (y + width) / 3);

        }
            g.drawLine((int) (x + playPos % AudioPlayer.getInstance().getSampleRate() * width / 8), y , (int) (x + playPos % AudioPlayer.getInstance().getSampleRate() * width / 8 ), (int) (y + width) / 3);


        g.setColor(Color.BLACK);

        renderWaveBufferNew((int) width, (int) height);
    }


    @Override
    public void upDateAudioStream(AudioInputStream audioInputStream) {
        waveFormBufferChangeRender = true;
        generateFramesAmplitude(audioInputStream);
    }
}
