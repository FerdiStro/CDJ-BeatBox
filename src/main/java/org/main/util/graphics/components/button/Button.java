package org.main.util.graphics.components.button;


import lombok.Getter;
import lombok.Setter;
import org.main.util.Coordinates;
import org.main.util.Logger;
import org.main.util.graphics.components.AbstractComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

//todo: change all buttons to this class
@Getter
@Setter
public class Button extends AbstractComponent {

    private boolean toggle = false;

    private final String name;
    private final BufferedImage image;

    private String state = "";
    private String onState = "";
    private String offState = "";
    private boolean stateButton = false;

    private Font font;
    private static final AffineTransform affinetransform = new AffineTransform();
    private static final FontRenderContext frc = new FontRenderContext(affinetransform, true, true);


    private boolean hoverActive = false;
    private boolean toggleColorFullButton = false;

    private Color hoverColor = Color.PINK;
    private Color backgroundColor = Color.WHITE;
    private Color stringColor = Color.BLACK;
    private Color toggleColor = Color.ORANGE;

    /**
     * Creates default  button with {@code koordinate} and {@code Dimension}.  Give  name with {@code Name }-param
     *
     * @param coordinates coordinates with {@code int} x and y  {@code int} coordinates
     * @param dimension   dimension with {@code int} width and height  {@code int}
     * @param name        name which  displayed on button a {@code String}
     */
    public Button(Coordinates coordinates, Dimension dimension, String name) {
        super(coordinates, dimension);
        this.name = name;
        image = null;
    }

    /**
     * Creates button with {@code koordinate} and {@code Dimension}.  Use {@code File} mapped to {@code BufferedImage} instead of {@code Name}.
     *
     * @param coordinates coordinates with {@code int} x and y  {@code int} coordinates
     * @param dimension   dimension with {@code int} width and height  {@code int}
     * @param file        file which need to refer to valid {@code BufferedImage }, displayed on button
     */
    public Button(Coordinates coordinates, Dimension dimension, File file) {
        super(coordinates, dimension);
        this.name = file.getName();
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            Logger.error("Error while reading Settings-Image");
        }
        this.image = image;
    }

    /**
     * Creates button with {@code koordinate} and {@code name}. Dimension set to size of {@code name} dependent on {@code font}.
     *
     * @param coordinates coordinates with {@code int} x and y  {@code int} coordinates
     * @param font        valid font
     * @param name        name of button - a {@code String}
     **/
    public Button(Coordinates coordinates, Font font, String name) {
        super(coordinates, new Dimension((int) (font.getStringBounds(name, frc).getWidth()), (int) (font.getStringBounds(name, frc).getHeight())));
        this.name = name;
        image = null;
        this.font = font;
    }

    /*
        Mouse Calculation
     */
    public void hoverMouse(MouseEvent e, Color hoverColor, OnEvent onHover) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        Rectangle rectToggle = new Rectangle(getX(), getY(), getDimension().width, getDimension().height);
        if (rectToggle.contains(mouseX, mouseY)) {
            this.hoverColor = hoverColor;
            hoverActive = true;
            if (onHover != null) {
                onHover.onEvent();
            }
        } else {
            hoverActive = false;
        }
    }

    public void clickMouse(MouseEvent e, OnEvent onClick) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        Rectangle rectToggle = new Rectangle(getX(), getY(), getDimension().width, getDimension().height);
        if (rectToggle.contains(mouseX, mouseY)) {

            Color beforeChange = hoverColor;
            hoverColor = backgroundColor;
            Timer repaintColorTimer = new Timer();
            repaintColorTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    hoverColor = beforeChange;
                    repaintColorTimer.cancel();
                }
            }, 100, 1000);

            if (stateButton) {
                toggle();
            }
            if (onClick != null) {
                onClick.onEvent();
            }
        }
    }

    /*
        Draw Button
     */
    @Override
    public void draw(Graphics2D g2d) {
        Color colorBefore = g2d.getColor();
        Font fontBefore = g2d.getFont();

        if (font == null) {
            font = fontBefore;
        }

        g2d.setColor(backgroundColor);
        g2d.setFont(font);

        if (hoverActive) {
            g2d.setColor(hoverColor);
        }
        if (toggleColorFullButton && toggle) {
            g2d.setColor(toggleColor);
        }
        g2d.fillRect(getX(), getY(), getDimension().width, getDimension().height);


        if (stateButton) {
            if (toggle) {
                g2d.setColor(toggleColor);

                state = onState;
            } else {
                g2d.setColor(backgroundColor);
                state = offState;
            }
        }

        g2d.drawRect(getCoordinates().getX(), getCoordinates().getY(), getDimension().width, getDimension().height);

        if (image == null) {
            g2d.setColor(stringColor);
            String tempName = name + state;
            g2d.drawString(tempName, getCoordinates().getX() + 2, getCoordinates().getY() + getDimension().height / 2 + font.getSize() / 2);
        }


        if (image != null) {
            g2d.drawImage(image, getCoordinates().getX(), getCoordinates().getY(), getDimension().width, getDimension().height, null);

        }

        g2d.setColor(colorBefore);
        g2d.setFont(fontBefore);
    }

    /*
        Utils
     */

    public void setStateButton(String onState, String offState) {
        this.stateButton = true;
        this.onState = onState;
        this.offState = offState;
    }

    public void setStateButton() {
        this.stateButton = true;
    }

    public void toggle() {
        this.toggle = !this.toggle;
    }
}
