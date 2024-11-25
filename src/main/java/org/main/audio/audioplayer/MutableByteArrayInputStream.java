package org.main.audio.audioplayer;

import java.io.ByteArrayInputStream;

import static org.main.util.audio.AudioUtils.getCombined;
import static org.main.util.audio.AudioUtils.getFrames;


public class MutableByteArrayInputStream extends ByteArrayInputStream {



    public MutableByteArrayInputStream(byte[] buf) {
        super(buf);
    }



    public void modifyData(byte[] buffer){
        AudioFrame[] frames = getFrames(buffer,buffer.length /  6);
        for(AudioFrame frame : frames){


            //todo: modify here


//            frame.muteChannel_R();
//            frame.muteChannel_L();


        }

        byte[] modifiedBuffer = getCombined(frames);

        System.arraycopy(modifiedBuffer, 0, buffer, 0, buffer.length);


    }







}
