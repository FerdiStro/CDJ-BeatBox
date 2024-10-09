package org.main.util.graphics;

import lombok.Getter;
import org.deepsymmetry.cratedigger.pdb.RekordboxAnlz;
import org.main.util.Koordinate;
import org.main.util.Logger;

import java.awt.*;
import java.awt.event.MouseEvent;


@Getter
public abstract class AbstractComponent {

    private  int x;
    private  int y;
    private  Koordinate koordinate;

    public AbstractComponent( Koordinate koordinate) {
        Logger.init(getClass());
        this.x = koordinate.getX();
        this.y = koordinate.getY();
        this.koordinate = koordinate;
    }



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


    public void setDimension(Koordinate koordinate){
        this.x = koordinate.getX();
        this.y = koordinate.getY();
        this.koordinate =   koordinate;
    }

    public void setDimension(int x, int y){
        this.x = x;
        this.y = y;
        this.koordinate =   new Koordinate(x, y);
    }
}
