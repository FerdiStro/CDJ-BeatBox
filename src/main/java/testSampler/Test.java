package testSampler;

import org.main.util.Coordinates;
import org.main.util.graphics.components.AbstractComponent;
import org.main.util.graphics.components.Shadow;
import org.main.util.graphics.components.button.Button;
import org.main.util.graphics.components.menu.MultipleComponentMenuHorizontal;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        button.toggle();


        Button button2 = new Button(new Coordinates(10, 100), new Dimension(150, 70), "test2");
        button2.setFancy(true);


        List<AbstractComponent> componentList =  new ArrayList<>();
        componentList.add(button);
        componentList.add(button2);

        MultipleComponentMenuHorizontal multipleComponentMenuHorizontal = new MultipleComponentMenuHorizontal(new Coordinates( 10, 100), new Dimension(200, 100),componentList );
        multipleComponentMenuHorizontal.setShadow(new Shadow( 4,  4, 30));



        JFrame frame = new JFrame();
        JLabel jLabel = new JLabel() {





            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                multipleComponentMenuHorizontal.draw((Graphics2D) g);





            }

        };


        frame.setBackground(Color.WHITE);
        frame.add(jLabel);
        frame.setSize(800, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


    }


}


