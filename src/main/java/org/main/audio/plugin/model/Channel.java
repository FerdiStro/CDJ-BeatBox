package org.main.audio.plugin.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main.audio.plugin.operation.Operation;

@Getter
@Setter
@NoArgsConstructor
public class Channel {

    private String kind;
    private float value;
    private transient Operation operation;

}
