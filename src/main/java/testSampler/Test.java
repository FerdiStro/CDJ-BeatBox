package testSampler;

import testSampler.Audio.AudioFrame;
import testSampler.Audio.AudioPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Test {
    public final static String AUDIO_1 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/kick/KICK_04.wav";
    public final static String AUDIO_2 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/vox/Vocal_02_Feel The Madness.wav";
    public final static String OUTPUT_FILE = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/combined_output.wav";


    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        AudioInputStream audioInputStream_1 = AudioSystem.getAudioInputStream(new File(AUDIO_1));
        AudioInputStream audioInputStream_2 = AudioSystem.getAudioInputStream(new File(AUDIO_2));


        AudioPlayer audioPlayer =  new AudioPlayer();


        AudioInputStream combine = audioPlayer.combine(audioInputStream_1, audioInputStream_2);


        audioPlayer.play(combine);

//        audioPlayer.play(audioInputStream_1);
//        audioPlayer.play(audioInputStream_2);




        Thread.sleep(10000);

















    }







}


