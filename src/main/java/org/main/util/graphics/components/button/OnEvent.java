package org.main.util.graphics.components.button;

import org.main.util.Logger;

public interface OnEvent {

    void onEvent();

    default void onEvent(String event) {
        Logger.notImplemented("onEvent with String not implemented");
    }
}
