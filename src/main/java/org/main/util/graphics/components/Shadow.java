package org.main.util.graphics.components;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Shadow{
    @Getter
    private boolean isEnable;
    @Getter
    private final int offsetX;
    @Getter
    private final int offsetY;

    private final int transparency;


    public Shadow( int offsetX, int offsetY, int transparency) {
        this.isEnable = true;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.transparency =  transparency;
    }

    public void draw(Graphics2D g , AbstractComponent component){
        Color berforeColor = g.getColor();

        g.setColor(new Color(berforeColor.getRed(), berforeColor.getGreen(), berforeColor.getBlue(),  transparency));

        g.fillRect(component.getX() + offsetX, component.getY()  + component.getDimension().height , component.getDimension().width, offsetY);
        g.fillRect(component.getX()  + component.getDimension().width , component.getY() + offsetY ,  offsetX, component.getDimension().height -offsetY);

        g.setColor(berforeColor);

    }

}
