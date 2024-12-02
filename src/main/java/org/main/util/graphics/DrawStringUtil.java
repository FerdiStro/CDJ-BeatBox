package org.main.util.graphics;

import org.main.util.Logger;

import java.awt.*;

public class DrawStringUtil {

    public static void drawStringWithMaxWidth(Graphics2D g, String text, int x, int y, int maxWidth, boolean inMiddle) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);


        if(Logger.debugGraphics){
            g.drawRect( x + maxWidth / 2  - textWidth /2 , y - g.getFont().getSize(), textWidth, g.getFont().getSize());
        }

        if (textWidth > maxWidth) {
            String ellipsis = "...";
            int ellipsisWidth = fontMetrics.stringWidth(ellipsis);

            int availableWidth = maxWidth - ellipsisWidth;
            int charIndex = 0;

            while (charIndex < text.length() && fontMetrics.stringWidth(text.substring(0, charIndex + 1)) <= availableWidth) {
                charIndex++;
            }

            text = text.substring(0, charIndex) + ellipsis;
        }else{
            if(inMiddle){
                if(textWidth/2 + maxWidth / 2 < maxWidth){
                    x = x + maxWidth / 2  - textWidth /2 ;
                }
            }
        }

        g.drawString(text, x, y);


    }
}
