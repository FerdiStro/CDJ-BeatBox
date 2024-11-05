package org.main.util.graphics.components;

import lombok.Getter;
import lombok.Setter;
import org.main.util.Koordinate;
import org.main.util.Logger;

import java.awt.*;
import java.awt.event.MouseEvent;


@Getter
public abstract class AbstractComponent {

    private  int x;
    private  int y;
    private  Koordinate koordinate;
    @Setter
    private Dimension dimension = new Dimension(0,0);

    public AbstractComponent( Koordinate koordinate) {
        Logger.init(getClass());
        this.x = koordinate.getX();
        this.y = koordinate.getY();
        this.koordinate = koordinate;
    }

    public AbstractComponent( Koordinate koordinate, Dimension dimension) {
        Logger.init(getClass());
        this.x = koordinate.getX();
        this.y = koordinate.getY();
        this.koordinate = koordinate;
        this.dimension =  dimension;
    }

    public void draw(Graphics2D g2d) {
        g2d.setBackground(Color.black);
        g2d.fillRect(koordinate.getX(), koordinate.getY(), (int) dimension.getWidth(), (int) dimension.getHeight());
    }

    public void setX(int x){
        koordinate.setX(x);
    }

    public void setY(int y){
        koordinate.setY(y);
    }

    public void clickEvent(MouseEvent e){
    }

    public void clickMethode(String options){
        if(this.componentClickListener != null){
            this.componentClickListener.onOptionClicked(options);
        }else {
            Logger.error("Component ClickListener is null");
        }
    }

    public interface ComponentClickListener {
        void onOptionClicked(String options);
    }

    private ComponentClickListener componentClickListener;

    public void addClickListener(ComponentClickListener componentClickListener){
        this.componentClickListener = componentClickListener;
    }


    public void setKoordinate(Koordinate koordinate){
        this.x = koordinate.getX();
        this.y = koordinate.getY();
        this.koordinate =   koordinate;
    }

    public void setKoordinate(int x, int y){
        this.x = x;
        this.y = y;
        this.koordinate =   new Koordinate(x, y);
    }

    public void setRepositionAndSize(int x, int y, int width, int height ){
        setKoordinate(x, y);
        setDimension(new Dimension(width, height));
    }



}
