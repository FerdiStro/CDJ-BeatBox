package org.main.audio;

public enum SHOT_TYPE {
    ONE_BEST(1),
    TWO_BEAT(2),
    FOUR_BEAT(4);

    private int beatHit;


    SHOT_TYPE(int beatHit){
        this.beatHit = beatHit;
    }

    public int getBeatHit(){
        return this.beatHit;
    }

}
