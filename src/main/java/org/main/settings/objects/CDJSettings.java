package org.main.settings.objects;

import lombok.Getter;
import lombok.Setter;

public class CDJSettings extends AbstractSettings {


    private static final String PATH = "src/main/resources/configs/cdjConfig.json";

    @Getter
    @Setter
    private boolean useCdj;
    private boolean useCdjInit ;

    @Getter
    private int maxAttempts;
    private int maxAttemptsInit;
    @Getter
    private int sleepTimer;
    private int sleepTimerInit;


    @Override
    public String getPATH() {
        return PATH;
    }

    @Override
    public void reset() {
        this.useCdj  = useCdjInit;
        this.maxAttempts = maxAttemptsInit;
        this.sleepTimer = sleepTimerInit;
    }




}
