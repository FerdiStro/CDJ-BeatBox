package org.main.util.graphics;

import org.main.util.Coordinates;
import org.main.util.graphics.components.AbstractComponent;

import java.awt.*;

public class SettingsDescribeFrame extends AbstractComponent {

    private final String name;
    private final String description;
    private final String function;
    private final Coordinates coordinates;

    public SettingsDescribeFrame(Coordinates coordinates, String name, String function, String description) {
        super(coordinates);
        this.name = name;
        this.description = description;
        this.function = function;
        this.coordinates = coordinates;
    }


    public void draw(Graphics2D g) {

        g.drawLine(coordinates.getX() +  25, coordinates.getY() + 20, coordinates.getX() + 100, coordinates.getY() - 50);

        int x = coordinates.getX() + 100;
        int y = coordinates.getY() - 100;

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
