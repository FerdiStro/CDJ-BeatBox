package org.main.settings.graphics;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CustomDropdown extends Component {
    private List<String> options;
    private boolean isDropdownOpen = false;
    private String selectedOption;
    private final int dropdownWidth;
    private final int dropdownHeight;
    private final int optionHeight;


    public CustomDropdown(List<String> options, int x , int y, Dimension dimension) {
        super(CustomDropdown.class, x, y);
        this.dropdownWidth = dimension.width;
        this.dropdownHeight = dimension.height;
        this.optionHeight = dimension.height;
        this.options = options;
        this.selectedOption = options.getFirst();
    }

    @Override
    public void clickEvent(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (mouseX >= getX() && mouseX <= getX() + dropdownWidth &&
                mouseY >= getY() && mouseY <= getY() + dropdownHeight) {
            isDropdownOpen = !isDropdownOpen;
        } else if (isDropdownOpen) {
            if (mouseX >= getX() && mouseX <= getX() + dropdownWidth &&
                    mouseY >= getY() + dropdownHeight && mouseY <= getY() + dropdownHeight + options.size() * optionHeight) {

                int index = (mouseY - getY() - dropdownHeight) / optionHeight;
                selectedOption = options.get(index);
                clickMethode(selectedOption);
            }
            isDropdownOpen = false;
        }
    }


    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(getX(), getY(), dropdownWidth, dropdownHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(getX(), getY(), dropdownWidth, dropdownHeight);
        g2d.drawString(selectedOption, getX() + 10, getY() + 20);

        if (isDropdownOpen) {
            for (int i = 0; i < options.size(); i++) {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(getX(), getY() + dropdownHeight + i * optionHeight, dropdownWidth, optionHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(getX(), getY() + dropdownHeight + i * optionHeight, dropdownWidth, optionHeight);
                g2d.drawString(options.get(i), getX() + 10, getY() + dropdownHeight + 20 + i * optionHeight);
            }
        }
    }
}
