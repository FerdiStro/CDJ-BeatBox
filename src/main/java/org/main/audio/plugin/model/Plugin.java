package org.main.audio.plugin.model;

import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.main.audio.plugin.operation.Operation;
import org.main.audio.plugin.operation.factory.OperationFactory;
import org.main.util.graphics.components.button.Button;


import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class Plugin {


    private String name;

    @Getter
    private List<StreamManipulating> streamManipulating;



    @Getter
    private int posPad;
    @Getter
    private int posMIDI;


    @Getter
    private transient Button button;


    public void init(){
        button = new Button(name);
        button.setFancy(true);


        for(StreamManipulating s : streamManipulating){
            Channel channelL = s.getChannel_l();
            Channel channelR = s.getChannel_r();

            channelL.setOperation(
                    OperationFactory.newBuilder()
                            .setName(channelL.getKind())
                            .setValue(channelL.getValue())
                            .build());

            channelR.setOperation(OperationFactory.newBuilder()
                    .setName(channelR.getKind())
                    .setValue(channelR.getValue())
                    .build());

            s.setChannel_r(channelR);
            s.setChannel_l(channelL);
        }
    }

    public List<StreamManipulating> getStreamManipulation(int i){
        List<StreamManipulating> retunrList = new ArrayList<>();;

        for(StreamManipulating s : streamManipulating){
            float endTime = s.getEndTime();
            if(endTime  > i ){
                if(s.getStartTime() < i ){
                    retunrList.add(s);

                }
            }
        }
        return retunrList;
    }



}
