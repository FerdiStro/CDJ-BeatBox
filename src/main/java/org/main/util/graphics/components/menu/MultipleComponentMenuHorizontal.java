package org.main.util.graphics.components.menu;

import lombok.Setter;
import org.apache.commons.math3.analysis.function.Max;
import org.main.util.Coordinates;
import org.main.util.Logger;
import org.main.util.graphics.components.AbstractComponent;
import org.main.util.graphics.components.Shadow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MultipleComponentMenuHorizontal extends AbstractComponent {


    private List<AbstractComponent> componentList;

    @Setter
    private Shadow shadow = null;
    @Setter
    private Color backgroundColor = Color.BLACK;

    public MultipleComponentMenuHorizontal(Coordinates coordinates, Dimension dimension, List<AbstractComponent> components) {
        super(coordinates, dimension);
        this.componentList = components;
    }



    int scrollPost = 0 ;


    BufferedImage bufferedMenu = null;

    public void update(){
//        BufferedImage bufferedImage =  new BufferedImage(getDimension().width + shadow.getOffsetX(), getDimension().height + shadow.getOffsetY(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage bufferedImage =  new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bufferedImage.createGraphics();

        int beforeX  = getX();
        int beforeY  = getY();



        setX(0);
        setY(0);

        int x = getX();
        int y = getY();

        int height =  getDimension().height;
        int width =  getDimension().width;

        int blankSpaceX = width / 8;
        int blankSpaceY = height/ 8;


//        g.setClip(0, 0, width +  10, height+10);


        //Background
        if(backgroundColor != null){
            g.setColor(backgroundColor);
        }
        g.fillRect(x,y, width, height);




        //drawComponent
        for(int i = 0 ; i !=  componentList.size(); i++){
            AbstractComponent component = componentList.get(i);

            if(i == 0){
                int maxHeight = Math.min(height - 2 * blankSpaceY, component.getDimension(). height);
                int space = Math.max(blankSpaceY, (height - maxHeight) /2);

                component.setCoordinates( x + blankSpaceX  , y  +  space );
                component.setDimension(new Dimension((int) component.getDimension().getWidth(), maxHeight));
            }else{
                AbstractComponent beforeComponent = componentList.get(i - 1);

                int maxHeight = Math.min(height - 2 * blankSpaceY, beforeComponent.getDimension(). height);
                int space = Math.max(blankSpaceY, (height - maxHeight) /2);

                component.setCoordinates(beforeComponent.getX() + beforeComponent.getDimension().width + blankSpaceY, y + space);
                component.setDimension(new Dimension((int) component.getDimension().getWidth(), maxHeight));

            }

            component.draw(g);
        }






        Logger.drawMiddle(g, this, Color.white);


        //Shadow
        if(shadow != null){
            if(shadow.isEnable()){
                shadow.draw(g, this);
            }
        }

        g.dispose();

        try {
            File outputfile = new File("saved_menu.png");
            ImageIO.write(bufferedImage, "png", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        this.bufferedMenu = bufferedImage;

        setX(beforeX);
        setY(beforeY);
    }

    @Override
    public void draw(Graphics2D g2d) {

        if(bufferedMenu == null){
            update();
        }
        g2d.drawImage(bufferedMenu, getX(), getY(), null);



    }
}
