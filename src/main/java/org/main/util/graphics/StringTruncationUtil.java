package org.main.util.graphics;

import java.awt.*;

public class StringTruncationUtil {

    public static void drawStringWithMaxWidth(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);

        if (textWidth > maxWidth) {
            String ellipsis = "...";
            int ellipsisWidth = fontMetrics.stringWidth(ellipsis);

            int availableWidth = maxWidth - ellipsisWidth;
            int charIndex = 0;

            while (charIndex < text.length() && fontMetrics.stringWidth(text.substring(0, charIndex + 1)) <= availableWidth) {
                charIndex++;
            }

            text = text.substring(0, charIndex) + ellipsis;
        }

        g.drawString(text, x, y);
    }
}
