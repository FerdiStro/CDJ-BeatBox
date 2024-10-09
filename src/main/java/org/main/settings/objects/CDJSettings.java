package org.main.settings.objects;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CDJSettings extends AbstractSettings {


    private static final String PATH = "src/main/resources/configs/cdjConfig.json";


    private boolean useCdj;
    private boolean useCdjInit ;

    private int maxAttempts;
    private int maxAttemptsInit;
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
