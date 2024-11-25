package org.main.audio.audioplayer;

import lombok.Getter;
import lombok.extern.java.Log;
import org.main.audio.PlayerGrid;
import org.main.audio.library.TYPE;
import org.main.audio.playegrid.Slot;
import org.main.audio.playegrid.SlotAudio;
import org.main.util.Logger;
import org.main.util.audio.AudioUtils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;


public class AudioPlayer {


    @Getter
    private int bpm = 120;
    @Getter
    private int beatGrid = 8;
    @Getter
    private double sampleRate = 44100.00;


    @Getter
    private AudioFormat format;


    private static AudioPlayer INSTANCE;


    public static AudioPlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AudioPlayer();
        }
        return INSTANCE;
    }

    private AudioPlayer() {
        this.format = new AudioFormat(new AudioFormat.Encoding("PCM_SIGNED"), (float) sampleRate, 24, 2, 6, (float) sampleRate, false);
        setFullBetAudioStream(sampleRate, beatGrid, bpm);
    }

    public void setFullBetAudioStream(double sampleRate, int beatGrid, int bpm) {
        this.bpm = bpm;
        this.beatGrid = beatGrid;
        this.sampleRate = sampleRate;

        this.format = new AudioFormat(new AudioFormat.Encoding("PCM_SIGNED"), (float) sampleRate, 24, 2, 6, (float) sampleRate, false);
        double fullBeatRange = (sampleRate * ((double) beatGrid / ((double) bpm / 60)) * format.getFrameSize());

        MutableByteArrayInputStream mutableByteArrayInputStream = new MutableByteArrayInputStream(new byte[(int) fullBeatRange]);

        fullBeatAudioStream = new AudioInputStream(mutableByteArrayInputStream, format, (long) fullBeatRange);
        fullBeatAudioStream.mark(Integer.MAX_VALUE);

    }


    @Getter
    private AudioInputStream fullBeatAudioStream;


    public synchronized void play() {
        this.playAudioInputStream(fullBeatAudioStream);
    }


    private Map<String, SlotAudio> slotAudioMap = new HashMap<>();

    public void removeAudio(SlotAudio slotAudio, int posInBeat) {
        String slotAudioMapName = slotAudio.getName() + CONST_SPLIT + posInBeat;
        slotAudioMap.remove(slotAudioMapName);
        if (slotAudioMap.isEmpty()) {
            clearAudio();

        } else {
            this.renderSlotAudioInStreamNew();
        }
    }

    public void clearAudio() {
        setFullBetAudioStream(sampleRate, beatGrid, bpm);
        updateAllObserver();

    }

    int count  = 0;

    private void renderSlotAudioInStreamNew() {
        count++;

        Thread addAudioThread = new Thread(() -> {
            int countThread  = count;
            setFullBetAudioStream(sampleRate, beatGrid, bpm);


            try {
                for (String slotAudioKey : slotAudioMap.keySet()) {
                    int pos = Integer.parseInt(slotAudioKey.split(getInstance().CONST_SPLIT)[1]);
                    SlotAudio slotAudio = slotAudioMap.get(slotAudioKey);

                    AudioInputStream audioStream = null;
                    try {
                        audioStream = AudioSystem.getAudioInputStream(slotAudio.getAudioFile());
                    } catch (UnsupportedAudioFileException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    audioStream.mark(Integer.MAX_VALUE);

                    if(countThread != count) return;
                    addAudio(audioStream, (double) pos / 2);
                }
                if(countThread != count) return;
                updateAllObserver();
            }catch (Exception ignore){}


        });

        addAudioThread.start();

    }

    private final String CONST_SPLIT = "%_%";

    public void addAudio(SlotAudio slotAudio, int posInBeat) {
        if(!slotAudio.getPlayType().equals(TYPE.ONESHOOT) && !slotAudioMap.containsKey(slotAudio.getName() + CONST_SPLIT + posInBeat)){
            slotAudioMap.put(slotAudio.getName() + CONST_SPLIT + posInBeat, slotAudio);
            renderSlotAudioInStreamNew();
        }else{
            updateAllObserver();
        }
    }


    public void addAudio(AudioInputStream audioInputStream, double posInSec) {
        if (audioInputStream != null) {
            try {
                double pauseFrames = (format.getSampleRate() * posInSec) * audioInputStream.getFormat().getFrameSize();

                int fullSize = (int) (pauseFrames + audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize());

                byte[] pauseSound = new byte[fullSize];

                byte[] audioBytes = null;

                try {
                    audioBytes = audioInputStream.readAllBytes();
                } catch (IOException e) {
                    Logger.error("Cant add Sound to Stream");
                }

                int b = 0;
                for (int i = 0; i != fullSize; i++) {


                    if (i >= pauseFrames) {
                        if (audioBytes != null && audioBytes.length != 0) {
                            pauseSound[i] = audioBytes[b];
                            b++;
                        }
                    } else {
                        pauseSound[i] = 0;
                    }
                }


                MutableByteArrayInputStream mutableByteArrayInputStream = new MutableByteArrayInputStream(pauseSound);
                AudioInputStream addedStream = new AudioInputStream(mutableByteArrayInputStream, format, fullSize);
                addedStream.mark(Integer.MAX_VALUE);
                this.fullBeatAudioStream = combineAudioInputStreams(this.fullBeatAudioStream, addedStream);


                try {
                    audioInputStream.reset();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
            }

            return;
        }
        Logger.error("No inputStream (SlotAudio selected)");
    }


    public static AudioInputStream getAudioInputStream(File file) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            audioInputStream.mark(Integer.MAX_VALUE);
            return audioInputStream;
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void playAudioInputStream(AudioInputStream audioInputStream) {
        try {
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

        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

        try {
            audioInputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public AudioInputStream combineAudioInputStreams(AudioInputStream audioInputStream_1, AudioInputStream audioInputStream_2) {
        if (audioInputStream_1.getFormat().getSampleSizeInBits() != 24 && audioInputStream_2.getFormat().getSampleSizeInBits() != 24) {
            Logger.notImplemented("Only 24 samples are supported");
            Logger.error("Only 24 samples are supported");
            return null;
        }

        try {
            byte[] audioBytes_1 = audioInputStream_1.readAllBytes().clone();
            byte[] audioBytes_2 = audioInputStream_2.readAllBytes().clone();

            AudioFrame[] frames_1 = AudioUtils.getFrames(audioBytes_1, audioBytes_1.length / format.getFrameSize());
            AudioFrame[] frames_2 = AudioUtils.getFrames(audioBytes_2, audioBytes_2.length / format.getFrameSize());


            int maxFrameRange = Integer.max(frames_1.length, frames_2.length);

            AudioFrame[] combinedFrames = new AudioFrame[maxFrameRange];

            int max_abs = 0;

            for (int i = 0; i != maxFrameRange; i++) {
                AudioFrame frameI_1 = null;
                AudioFrame frameI_2 = null;

                try {
                    frameI_1 = frames_1[i];
                } catch (Exception ignore) {
                }
                try {
                    frameI_2 = frames_2[i];
                } catch (Exception ignore) {
                }

                if (frameI_1 != null && frameI_2 != null) {

                    byte[] zChannelR = new byte[3];
                    byte[] zChannelL = new byte[3];

                    for (int k = 0; k != 3; k++) {
                        byte bR_1 = frameI_1.getChannel_R()[k];
                        byte bR_2 = frameI_2.getChannel_R()[k];

                        byte bL_1 = frameI_1.getChannel_L()[k];
                        byte bL_2 = frameI_2.getChannel_L()[k];

                        byte zR = (byte) Integer.max(Integer.min(bR_1 + bR_2, AudioUtils.getMAX24BIT()), AudioUtils.getMIN24BIT());
                        byte zL = (byte) Integer.max(Integer.min(bL_1 + bL_2, AudioUtils.getMAX24BIT()), AudioUtils.getMIN24BIT());

                        zChannelL[k] = zL;
                        zChannelR[k] = zR;

                    }

                    int scaleZL = AudioUtils.combineBytesBytes24Bit(format.isBigEndian(), zChannelR[0], zChannelR[1], zChannelR[2]);
                    int scaleZR = AudioUtils.combineBytesBytes24Bit(format.isBigEndian(), zChannelL[0], zChannelL[1], zChannelL[2]);

                    if (scaleZL > max_abs) {
                        max_abs = scaleZL;
                    }
                    if (scaleZR > max_abs) {
                        max_abs = scaleZR;
                    }

                    combinedFrames[i] = new AudioFrame(zChannelL, zChannelR);
                }
                if (frameI_1 == null && frameI_2 != null) {
                    combinedFrames[i] = new AudioFrame(frameI_2.getChannel_L(), frameI_2.getChannel_R());
                }
                if (frameI_1 != null && frameI_2 == null) {
                    combinedFrames[i] = new AudioFrame(frameI_1.getChannel_L(), frameI_1.getChannel_R());
                }
            }

            float scaleFactor = (float) AudioUtils.getMAX24BIT() / max_abs;

            AudioUtils.normalizeFrames(combinedFrames, scaleFactor, format.isBigEndian());

            byte[] combinedByteStream = AudioUtils.getCombined(combinedFrames);


            MutableByteArrayInputStream mutableByteArrayInputStream = new MutableByteArrayInputStream(combinedByteStream);

            return new AudioInputStream(mutableByteArrayInputStream, audioInputStream_1.getFormat(), maxFrameRange);

        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }



    public void loadPattern(PlayerGrid playerGrid){
        for(int i  = 0; i !=  playerGrid.getSlots().length; i++ ){
            for (SlotAudio selectedSound : playerGrid.getSlots()[i].getSelectedSounds()) {
                slotAudioMap.put(selectedSound.getName() + CONST_SPLIT + i, selectedSound);
            }
        }
        renderSlotAudioInStreamNew();
    }



    /*
        Observer
     */

    private List<UpdateAudioStream> updateAudioStreamListObserver = new ArrayList<>();

    public void addUpdateAudioStreamObserver(UpdateAudioStream updateAudioStream) {
        updateAudioStreamListObserver.add(updateAudioStream);
    }

    public void updateAllObserver() {
        for (UpdateAudioStream updateAudioStream : updateAudioStreamListObserver) {
            updateAudioStream.upDateAudioStream(fullBeatAudioStream);
        }
        Logger.debug("Update all UpdateAudioStream observer");
    }


}
