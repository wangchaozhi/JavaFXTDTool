package com.wangchaozhi;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class ScreenUtil {

    public static double getCenterXPosition(double windowWidth) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        return screenBounds.getMinX() + (screenBounds.getWidth() - windowWidth) / 2;
    }

    public static double getCenterYPosition(double windowHeight) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        return screenBounds.getMinY() + (screenBounds.getHeight()-100 - windowHeight) / 2;
    }
}
