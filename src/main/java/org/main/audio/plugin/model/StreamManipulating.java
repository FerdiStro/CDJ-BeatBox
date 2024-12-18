package org.main.audio.plugin.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StreamManipulating {
    private float startTime;
    private float endTime;

    private Channel channel_r;
    private Channel channel_l;

}
