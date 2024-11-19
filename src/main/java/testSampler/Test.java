package testSampler;

import testSampler.Audio.Audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Test {
    public final static String AUDIO_1 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/kick/KICK_04.wav";
    public final static String AUDIO_2 = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/vox/Vocal_02_Feel The Madness.wav";
    public final static String OUTPUT_FILE = "/home/ferdinands/Projects/Private/CDJ-BeatBox/src/main/resources/Sounds/Sound/combined_output.wav";


    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(AUDIO_2));

        Audio audio = new Audio(audioInputStream);
        audio.play();

        Thread.sleep(1000);














    }







}


