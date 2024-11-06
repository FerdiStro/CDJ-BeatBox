package org.main.util.graphics.components.button;


import lombok.Getter;
import org.main.util.Koordinate;
import org.main.util.graphics.components.AbstractComponent;

import java.awt.*;
import java.awt.event.MouseEvent;

//todo: change all buttons to this class
public class Button extends AbstractComponent {
    @Getter
    private boolean toggle = false;

    private final String name;

    private String state = "";
    private String onState = "";
    private String offState = "";
    private boolean stateButton = false;

    public Button(Koordinate koordinate, Dimension dimension, String name) {
        super (koordinate, dimension);
        this.name = name;
    }

    public void setStateButton(boolean stateButton, String onState, String offState) {
        this.stateButton = stateButton;
        this.onState = onState;
        this.offState = offState;
    }

    public void checkMouse(MouseEvent e, OnPress onPress){
        int mouseX = e.getX();
        int mouseY = e.getY();
        Rectangle rectToggle = new Rectangle(getX(), getY(), getDimension().width, getDimension().height);
        if (rectToggle.contains(mouseX, mouseY)) {
            if(stateButton){
                toggle();
            }
            onPress.onPress();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        if(stateButton){
            if(toggle){
                g2d.setColor(Color.ORANGE);
                state =  onState;
            }else{
                g2d.setColor(Color.BLACK);
                state =  offState;
            }
        }
        String tempName =  name + state;
        g2d.drawRect(getKoordinate().getX(), getKoordinate().getY(), getDimension().width, getDimension().height);
        g2d.setColor(Color.BLACK);
        g2d.drawString(tempName, getKoordinate().getX() + 2, getKoordinate().getY() + getDimension().height -5);
    }

    public void toggle(){
        this.toggle = !this.toggle;
    }
}
