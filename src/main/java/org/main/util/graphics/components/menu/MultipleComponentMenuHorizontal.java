package org.main.util.graphics.components.menu;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.function.Max;
import org.main.util.Coordinates;
import org.main.util.Logger;
import org.main.util.graphics.components.AbstractComponent;
import org.main.util.graphics.components.Shadow;
import org.main.util.graphics.components.button.Button;
import org.main.util.graphics.components.button.OnEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MultipleComponentMenuHorizontal extends AbstractComponent {


    private Map<Rectangle, AbstractComponent> componentList =  new LinkedHashMap<>();

    @Setter
    private Shadow shadow = null;
    @Setter
    private Color backgroundColor = Color.BLACK;

    public MultipleComponentMenuHorizontal(Coordinates coordinates, Dimension dimension, List<AbstractComponent> components) {
        super(coordinates, dimension);
        for(AbstractComponent component : components){
            componentList.put(new Rectangle(component.getDimension()), component);
        }
    }



    @Override
    public void clickEvent(MouseEvent e){

        rightScroll.clickMouse(e, () -> {
            scrollPost += scrollSteps;
            updateOnDraw = true;
            super.updateAllObserver();
        });

        leftScroll.clickMouse(e, () -> {
            scrollPost -= scrollSteps;
            updateOnDraw = true;
            super.updateAllObserver();
        });

        int x = e.getX();
        int y = e.getY();

        for(Rectangle hitBox : componentList.keySet()){
            if(hitBox.contains(x- getX(), y - getY())){
                AbstractComponent abstractComponent = componentList.get(hitBox);
                abstractComponent.clickEvent(e);
                updateOnDraw = true;
                super.updateAllObserver();
            }
        }
    }

    BufferedImage bufferedMenu = null;

    public void update(){
        BufferedImage bufferedImage =  new BufferedImage(getDimension().width + shadow.getOffsetX(), getDimension().height + shadow.getOffsetY(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bufferedImage.createGraphics();

        Color beforeColor = g.getColor();


        int locX = 0;
        int locY = 0;

        int height =  getDimension().height;
        int width =  getDimension().width;

        int blankSpaceX = width / 8 ;
        int blankSpaceY = height/ 8 ;



        //Background
        if(backgroundColor != null){
            g.setColor(backgroundColor);
        }
        g.fillRect(locX,locY, width, height);


        AtomicReference<AbstractComponent> previous = new AtomicReference<>(null);
        Map<Rectangle, AbstractComponent> buffer =  new LinkedHashMap<>();

        Map<Rectangle, Coordinates> beforeCoordinates = new HashMap<>();


        componentList.values().forEach( component -> {


            if(previous.get() == null){
                int maxHeight = Math.min(height - 2 * blankSpaceY, component.getDimension(). height);
                int space = Math.max(blankSpaceY, (height - maxHeight) /2);

                int tempX = locX + blankSpaceX;
                int tempY = locY  +  space;

                component.setCoordinates( tempX - scrollPost , tempY );
                component.setDimension(new Dimension((int) component.getDimension().getWidth(), maxHeight));


            }else{


                int maxHeight = Math.min(height - 2 * blankSpaceY, previous.get().getDimension(). height);
                int space = Math.max(blankSpaceY, (height - maxHeight) /2);


                int tempY = locY  +  space;
                int tempX = previous.get().getX() + previous.get().getDimension().width + blankSpaceY;

                Dimension tempDimension = new Dimension((int) component.getDimension().getWidth(), maxHeight);

                component.setCoordinates(tempX, tempY);
                component.setDimension(tempDimension);


            }

            Rectangle rectToggle = new Rectangle(component.getX(), component.getY() , component.getDimension().width,component.getDimension().height);
            buffer.put(rectToggle, component);


            int x = component.getX() + getX();
            int y = component.getY() + getY();

            beforeCoordinates.put(rectToggle, new Coordinates(x, y));

            component.draw(g);
            previous.set(component);
        });
        componentList = buffer;



        componentList.forEach((rectangle, component) -> {
            component.setCoordinates(beforeCoordinates.get(rectangle));
        });











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

        g.setColor(beforeColor);
        updateOnDraw = false;
    }


    boolean updateOnDraw = true;

    int scrollPost = 0 ;


    Button rightScroll = new Button(">");
    Button leftScroll =  new Button("<");

    @Setter
    @Getter
    private int buttonSize = Math.min(getDimension().width / 7 , getDimension().height / 7);

    @Getter
    @Setter
    private int scrollSteps = 30;

    @Override
    public void addClickListener(OnEvent componentClickListener){
        super.addClickListener(componentClickListener);
        rightScroll.addClickListener(componentClickListener);
        leftScroll.addClickListener(componentClickListener);
    }


        @Override
    public void draw(Graphics2D g2d) {

        if(updateOnDraw){
            update();
        }{
            g2d.drawImage(bufferedMenu, getX(), getY(), null);

            rightScroll.setRepositionAndSize(getX(), getY() + getDimension().height - buttonSize,buttonSize, buttonSize);
            rightScroll.draw(g2d);

            leftScroll.setRepositionAndSize(getX() + getDimension().width - buttonSize, getY() + getDimension().height - buttonSize,buttonSize, buttonSize);
            leftScroll.draw(g2d);
        }




    }

}
