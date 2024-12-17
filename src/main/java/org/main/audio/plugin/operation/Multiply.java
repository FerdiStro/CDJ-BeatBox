package org.main.audio.plugin.operation;

import org.main.util.Logger;

public class Multiply  implements Operation {

    private static final String name = "multiply";


    private double value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(byte [] buffer) {
        for(int i = 0; i < buffer.length; i++){
            buffer[i] *= value;
        }


    }

    @Override
    public void setValue(double value) {
            this.value = value;
    }


}
