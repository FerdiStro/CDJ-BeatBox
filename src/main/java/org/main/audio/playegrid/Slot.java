package org.main.audio.playegrid;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.main.audio.library.LoadLibrary;
import org.main.audio.library.TYPE;
import org.main.util.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Slot implements ChangeListener, Cloneable {

    @Getter
    private List<SlotAudio> selectedSounds = new ArrayList<>();

    private transient boolean active = false;
    private transient double sliderValue = 0.5;

    //todo: load other slot audio with library
    private transient int VOLUME_SLIDER_WIDTH ;
    private transient int VOLUME_SLIDER_HEIGHT;
    private transient int sliderHeight;
    private transient int SLIDER_WIDTH;

    public int getVolumeSliderHeight(){
        return VOLUME_SLIDER_HEIGHT;
    }

    private transient boolean draggingSlider =  false;

    private transient int x, y;


    //todo: connect volumeSlider to  MINILAB MIDI keyboard
    public void drawVolumeSlider(Graphics2D g2d, int x, int y , Dimension dimension) {
        this.x =  x;
        this.y =  y;

        VOLUME_SLIDER_WIDTH  = (int) (dimension.width * 0.012);
        VOLUME_SLIDER_HEIGHT = (int) (dimension.height * 0.20);

        sliderHeight = (int) (dimension.width * 0.01);
        SLIDER_WIDTH = VOLUME_SLIDER_WIDTH - (int) (dimension.width * 0.004)  ;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, VOLUME_SLIDER_WIDTH, VOLUME_SLIDER_HEIGHT);


        g2d.setColor(Color.RED);

        int sliderY = y + (int) ((1 - sliderValue) * (VOLUME_SLIDER_HEIGHT - sliderHeight));
        g2d.fillRect((  x + VOLUME_SLIDER_WIDTH - SLIDER_WIDTH / 2)  -SLIDER_WIDTH,  sliderY  , SLIDER_WIDTH, sliderHeight);

        g2d.drawString("-  10db", x + VOLUME_SLIDER_WIDTH, y );
        g2d.drawString("-   0db", x  + VOLUME_SLIDER_WIDTH, y + VOLUME_SLIDER_HEIGHT / 2);
        g2d.drawString("-  10db", x + VOLUME_SLIDER_WIDTH, y + VOLUME_SLIDER_HEIGHT);
    }

    public void mousePressed(int mouseX, int mouseY) {
        int sliderY = y + (int) ((1 - sliderValue) * (VOLUME_SLIDER_HEIGHT - sliderHeight));

        if (mouseX >= x && mouseX <= x + VOLUME_SLIDER_WIDTH && mouseY >= sliderY && mouseY <= sliderY + sliderHeight) {
            draggingSlider = true;
        }
    }

    public void mouseDragged(int mouseY) {
        if (draggingSlider) {
            double percent = (double) (mouseY - y) / (VOLUME_SLIDER_HEIGHT - sliderHeight);
            sliderValue = Math.max(0.0, Math.min(1.0, 1 - percent));
            System.out.println(sliderValue);
            for(SlotAudio audio: this.selectedSounds){
                audio.setVolume((float) sliderValue);
            }
        }
    }

    public void mouseReleased() {
        draggingSlider = false;
    }

    public void refreshSelectedSounds(){
        if(selectedSounds != null && !selectedSounds.isEmpty()){
            for(int i = 0 ; i != selectedSounds.size(); i++){
                SlotAudio outOfCache = LoadLibrary.getInstance().loadSoundInCache(selectedSounds.get(i));
                if(selectedSounds != null){
                    selectedSounds.set(i, outOfCache);
                    active = true;
                }else {
                    Logger.error("Sound out ot pattern does not exist");
                }
            }
        }
    }

    public void addSelectedSound(SlotAudio audio){
        this.active = true;
        boolean contains  = false;
        for(SlotAudio name : selectedSounds){
            if (name.getName().equals(audio.getName())) {
                contains = true;
                break;
            }
        }
        if(!contains){
            this.selectedSounds.add(audio);
        }
    }

    public void play(){
        try {
            for(SlotAudio audio: selectedSounds){
                audio.play();
                if(audio.getPlayType().equals(TYPE.ONESHOOT)){
                    this.getSelectedSounds().remove(audio);
                }
            }
        }catch (Exception e){
            //ignore
        }
    }

    public boolean isActive() {
        return !selectedSounds.isEmpty() && this.active;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
            System.out.println("est");
    }

    @Override
    public Slot clone() {
        try {
            Slot clone = (Slot) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}