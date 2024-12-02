package org.main.util.graphics.components.button;


import lombok.Getter;
import lombok.Setter;
import org.main.util.Coordinates;
import org.main.util.Logger;
import org.main.util.graphics.DrawStringUtil;
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

public class Button extends AbstractComponent {


    @Getter
    private final String name;
    private final BufferedImage image;

    private  Font font;

    /**
     * {@code stateButton} is boolean. Can be set with {@code setStateButton()}  and {@code setStateButton(String onState, String offState)}<br>
     * When {@code true} button is in state mode. so have on (true) and off (false) state.
     */
    private boolean stateButton = false;
    /**
     * {@code toggle} is boolean. Shows current state as boolean. Can access by {@code @Setter} and {@code @Getter}
     */
    @Setter
    @Getter
    private boolean toggle = false;
    /**
     * {@code state} is String. Shows current state  as String (onState/offState string)
     */
    private String state = "";

    /**
     * {@code onState} is String. Rename button with state when toggle is true <br>
     *  Can be set in {@code setStateButton(String onState, String offState)}
     */
    private String onState = "";
    /**
     * {@code offState} is String. Rename button with state when toggle is false. <br>
     * Can be set in {@code setStateButton(String onState, String offState)}
     */
    private String offState = "";


    private static final AffineTransform affinetransform = new AffineTransform();
    private static final FontRenderContext frc = new FontRenderContext(affinetransform, true, true);


    private boolean hoverActive = false;

    @Setter
    private boolean toggleColorFullButton = false;


    @Setter
    private Color backgroundColor = Color.WHITE;
    @Setter
    private Color stringColor = Color.BLACK;
    @Setter
    private Color toggleColor = Color.ORANGE;

    /**
     * {@code hoverColor} is boolean. Is set on hovering int the {@code public void hoverMouse(MouseEvent e, Color hoverColor, OnEvent onHover)}-methode
     */
    private Color hoverColor = Color.PINK;

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

    public Button(String name){
        super(new Coordinates( 0, 0 ), new Dimension( 0, 0));
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

    @Setter
    private boolean fancy = false;

    /*
        Draw Button
     */
    @Override
    public void draw(Graphics2D g2d) {
        Color colorBefore = g2d.getColor();
        Font fontBefore = g2d.getFont();

        Font usedFont;

        if( this.font != null) {
             usedFont = this.font;
        }else{
            usedFont = fontBefore;
        }



        g2d.setColor(backgroundColor);
        g2d.setFont(usedFont);

        if (hoverActive) {
            g2d.setColor(hoverColor);
        }

        if (toggleColorFullButton && stateButton && toggle) {
            g2d.setColor(toggleColor);
        }

        g2d.fillRect(getX(), getY(), getDimension().width, getDimension().height);




        if (stateButton) {
            if (toggle) {
                g2d.setColor(toggleColor);
                state = onState;
            }
            if  (!toggle) {
                g2d.setColor(backgroundColor);
                state = offState;
            }
        }

        if(fancy){
            Color color = g2d.getColor();
            g2d.setColor(color.darker());
            g2d.fillRect(getX() + getDimension().width / 8, getY() +getDimension().height / 12 , getDimension().width - (getDimension().width / 4) , getDimension().height / 8 );
            g2d.setColor(color);
        }

        g2d.drawRect(getCoordinates().getX(), getCoordinates().getY(), getDimension().width, getDimension().height);

        if (image == null) {
            g2d.setColor(stringColor);
            String tempName = name + state;

            if(fancy){
                DrawStringUtil.drawStringWithMaxWidth(g2d, tempName,   getCoordinates().getX() , getCoordinates().getY() + getDimension().height /2  + usedFont.getSize()/ 2, getDimension().width, true);
            }

            if(!fancy){
                g2d.drawString(tempName, getCoordinates().getX(), getCoordinates().getY() + getDimension().height / 2 + usedFont.getSize() / 2);
            }
        }



        if (image != null) {
            g2d.drawImage(image, getCoordinates().getX(), getCoordinates().getY(), getDimension().width, getDimension().height, null);

        }

        if(Logger.debugGraphics){
            g2d.setColor(Color.BLACK);
            g2d.drawLine(getX() + getDimension().width /2, getY(), getX()  + getDimension().width /2, getY() +  getDimension().height);
            g2d.drawLine(getX(), getY() +  getDimension().height /2 , getX() +  getDimension().width, getY() + getDimension().height / 2);
        }


        g2d.setColor(colorBefore);
        g2d.setFont(fontBefore);
    }

    /**
     * Utils
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
