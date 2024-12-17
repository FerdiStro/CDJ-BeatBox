package org.main.audio.audioplayer;

import com.google.gson.internal.JavaVersion;
import org.main.audio.plugin.model.Plugin;
import org.main.audio.plugin.model.StreamManipulating;
import org.main.util.Logger;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.List;

import static org.main.util.audio.AudioUtils.getCombined;
import static org.main.util.audio.AudioUtils.getFrames;


public class MutableByteArrayInputStream extends ByteArrayInputStream {



    public MutableByteArrayInputStream(byte[] buf) {
        super(buf);
        streamSize = buf.length;
    }


    int count = 0 ;
    int streamSize = 0 ;



    public void modifyData(byte[] buffer, Plugin plugin) {


        AudioFrame[] frames = getFrames(buffer,buffer.length /  6);
        for(int i = 0 ; i != frames.length;  i ++ ){
            count += 6;
            AudioFrame frame = frames[i];



            int postProc = (int) ((double) count / (double) streamSize * 100);

            List<StreamManipulating> streamManipulatingList = plugin.getStreamManipulation(postProc);


            if(!streamManipulatingList.isEmpty()){

                for(StreamManipulating streamManipulating : streamManipulatingList){
                    frame.executeOperation(streamManipulating.getChannel_l().getOperation(), streamManipulating.getChannel_r().getOperation());
                }

            }


            //todo: modify here




//            frame.muteChannel_R();
//            frame.muteChannel_L();


        }

        byte[] modifiedBuffer = getCombined(frames);

        System.arraycopy(modifiedBuffer, 0, buffer, 0, buffer.length);


    }







}
