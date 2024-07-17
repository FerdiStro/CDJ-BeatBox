package org.main.audio.playegrid;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Slot implements ChangeListener {
    private List<SlotAudio> selectedSounds = new ArrayList<>();

    private boolean active = false;
    private double sliderValue = 0.5;

    //todo: load other slot audio with library
    private static final int VOLUME_SLIDER_WIDTH = 10;
    private static final int VOLUME_SLIDER_HEIGHT = 80;
    private static final int sliderHeight = 5;
    private static final int SLIDER_WIDTH = VOLUME_SLIDER_WIDTH -2;

    public int getVolumeSliderHeight(){
        return VOLUME_SLIDER_HEIGHT;
    }

    private boolean draggingSlider =  false;

    private int x, y;


    //todo: connect volumeSlider to  MINILAB MIDI keyboard
    public void drawVolumeSlider(Graphics2D g2d, int x, int y) {
        this.x =  x;
        this.y =  y;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, VOLUME_SLIDER_WIDTH, VOLUME_SLIDER_HEIGHT);

        int sliderY = y + (int) ((1 - sliderValue) * (VOLUME_SLIDER_HEIGHT - sliderHeight));

        g2d.setColor(Color.RED);
        g2d.fillRect(x + (VOLUME_SLIDER_WIDTH - SLIDER_WIDTH) / 2, sliderY, SLIDER_WIDTH, sliderHeight);
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
            for(SlotAudio audio: this.selectedSounds){
                audio.setVolume((float) sliderValue);
            }
        }
    }

    public void mouseReleased() {
        draggingSlider = false;
    }

    public void addSelectedSound(SlotAudio audio){
        this.active = true;

        boolean contains  = false;
        for(SlotAudio name : selectedSounds){
            if(name.getName().equals(audio.getName())){
                contains = true;
            }
        }
        if(!contains){
            this.selectedSounds.add(audio);
        }
    }
    public void toggleActive(){
        this.active  = !this.active;
    }


    public void play(){
        for(SlotAudio audio: selectedSounds){
            audio.play();
        }
    }

    public List<SlotAudio> getSelectedSounds() {
        return selectedSounds;
    }

    public boolean isActive() {
        return !selectedSounds.isEmpty() && this.active;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
            System.out.println("est");
    }
}