package org.main.audio.plugin.operation;

import org.main.util.Logger;

public class None implements Operation {
    @Override
    public String getName() {
        return "none";
    }

    @Override
    public void execute(byte[] buffer) {

    }

    @Override
    public void setValue(double value) {

    }
}
