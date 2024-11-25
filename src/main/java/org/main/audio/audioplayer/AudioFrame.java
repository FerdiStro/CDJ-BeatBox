package org.main.audio.audioplayer;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class AudioFrame {


    private final byte[] channel_L;
    private final byte[] channel_R;


    public AudioFrame(byte[] channelL, byte[] channelR) {
        channel_L = channelL;
        channel_R = channelR;
    }



    public void muteChannel_L(){
        Arrays.fill(channel_L, (byte) 0);
    }

    public void muteChannel_R(){
        Arrays.fill(channel_R, (byte) 0);
    }



}
