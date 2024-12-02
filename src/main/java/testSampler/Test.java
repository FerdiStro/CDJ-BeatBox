package testSampler;

import org.main.util.Coordinates;
import org.main.util.graphics.components.button.Button;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Test {
    public final static String AUDIO_1 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/kick/KICK_04.wav";
    public final static String AUDIO_3 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/vox/Vocal_04_Revolution_140bpm.wav";
    public final static String AUDIO_2 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/vox/Vocal_02_Feel The Madness.wav";
    public final static String OUTPUT_FILE = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/combined_output.wav";


    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
//        AudioInputStream audioInputStream_1 = AudioSystem.getAudioInputStream(new File(AUDIO_1));
//        AudioInputStream audioInputStream_2 = AudioSystem.getAudioInputStream(new File(AUDIO_2));
//        AudioInputStream audioInputStream_3 = AudioSystem.getAudioInputStream(new File(AUDIO_3));


//        AudioPlayer audioPlayer =  new AudioPlayer();


//        AudioInputStream combine = audioPlayer.combine(audioInputStream_2, audioInputStream_1);
//        audioPlayer.play(combine);
//
//        audioPlayer.play(audioInputStream_3);
//        audioPlayer.play(audioInputStream_1);

        Button button = new Button(new Coordinates(10, 100), new Dimension(100, 60), "test");
        button.setFancy(true);

        button.setStateButton();
//        button.setToggleColorFullButton(true);
        button.toggle();


        Frame frame = new Frame();
        JLabel jLabel = new JLabel() {





            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                button.draw((Graphics2D) g);


            }
        };

        frame.add(jLabel);
        frame.setSize(200, 300);
        frame.setVisible(true);



    }


}


