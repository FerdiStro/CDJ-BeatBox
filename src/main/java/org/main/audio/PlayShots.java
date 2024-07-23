package org.main.audio;

import org.main.audio.playegrid.SlotAudio;

public class PlayShots {
    private final SlotAudio clipTitel;
    private final int padNum;

    public PlayShots(SlotAudio clipTitel, int padNum){

        this.clipTitel = clipTitel;
        this.padNum = padNum;
    }

    public void play(){
        clipTitel.play();
    }

    public int getPadNum() {
        return padNum;
    }


//    public static PlayShots mapToPlayShot(int padNum, String clipTitel){
//        return  switch (padNum){
//            case 1 -> new PlayShots(clipTitel, );
//            default -> new PlayShots(clipTitel, );
//        };
//    }
}
