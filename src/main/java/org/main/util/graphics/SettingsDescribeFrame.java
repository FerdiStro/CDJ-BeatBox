package org.main.util.graphics;

import org.main.util.Koordinate;

import java.awt.*;

public class SettingsDescribeFrame extends AbstractComponent{

    private final String name;
    private final String description;
    private final String function;
    private final Koordinate koordinate;

    public SettingsDescribeFrame(Koordinate koordinate, String name,String function, String description) {
        super(koordinate);
        this.name = name;
        this.description = description;
        this.function = function;
        this.koordinate = koordinate;
    }


    public void draw(Graphics2D g) {

        g.drawLine(koordinate.getX() +  25, koordinate.getY() + 20, koordinate.getX() + 100, koordinate.getY() - 50);

        int x = koordinate.getX() + 100;
        int y = koordinate.getY() - 100;

        g.setColor(new Color(1f,0f,0f,.5f ));
        g.fillRect(x, y , 200, 350);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.BOLD,30));
        x = x + 5;
        g.drawString("Name: " + name, x, y + 30 );
        g.drawString("Function: " + function, x, y + 60 );
        g.drawString("Description: " + description, x, y + 90);

    }

}
