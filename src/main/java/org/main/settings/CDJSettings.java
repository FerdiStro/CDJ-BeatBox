package org.main.settings;

public class CDJSettings {

    protected CDJSettings() {

    }

    private boolean useCdj =  true;

    private final int maxAttempts =  1;
    private final int sleepTimer  =  50;


    public boolean isUseCdj() {
        return useCdj;
    }

    public void setUseCDJ(boolean useCDJ) {
        this.useCdj = useCDJ;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getSleepTimer() {
        return sleepTimer;
    }
}
