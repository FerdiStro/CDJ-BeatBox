package org.main.audio.plugin.operation;


public interface Operation {

    String getName();


    void execute(byte [] buffer);


    void setValue(double value);

}
