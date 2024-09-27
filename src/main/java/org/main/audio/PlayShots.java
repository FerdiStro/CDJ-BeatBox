package org.main.audio;

import org.main.audio.playegrid.SlotAudio;

public class PlayShots {

    private final SlotAudio clipTitel;
    private final int padNum;
    private final SHOT_TYPE shotType;


    public PlayShots(SlotAudio clipTitel, int padNum, SHOT_TYPE shotType) {
        this.clipTitel = clipTitel;
        this.padNum = padNum;
        this.shotType = shotType;
    }

    public void play(){
        clipTitel.play(shotType);
    }

    public int getPadNum() {
        return padNum;
    }

    public SHOT_TYPE getShotType() {
        return shotType;
    }

//    public static PlayShots mapToPlayShot(int padNum, String clipTitel){
//        return  switch (padNum){
//            case 1 -> new PlayShots(clipTitel, );
//            default -> new PlayShots(clipTitel, );
//        };
//    }
}
