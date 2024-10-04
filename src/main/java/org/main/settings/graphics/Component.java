package org.main.settings.graphics;

import org.main.util.Koordinate;
import org.main.util.Logger;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class Component {

    public Component(Class<? extends Component> className, int x , int y){
        Logger.info(className.getName());
        this.x = x;
        this.y = y;
    }

    private  int x;
    private  int y;

    public void draw(Graphics2D g2d) {}


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

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }

    public void setDimension(Koordinate koordinate){
        this.x = koordinate.getX();
        this.y = koordinate.getY();
    }

    public void setDimension(int x, int y){
        this.x = x;
        this.y = y;
    }
}
