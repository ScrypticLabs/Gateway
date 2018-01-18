/* 
* @File: CustomStage.java
* @Author: Abhi Gupta
* 
* This class creates a second stage to render a notification anywhere on the computer
* to indicate that a new message has been received even when the main app is in the background.
* Notifications are also used to provide the user with real-time results of whether they have
* been logged in, signed up, etc.
*/

package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomStage extends Stage {

    private final Location bottomRight;     // where the notifcation should be rendered on the screen

    /**
    * Constructor.
    * @param ap the pane that will contain the notifcation's "children"
    * @param style whether or not the stage should use the default close, minimize options
    */
    public CustomStage(Pane ap, StageStyle style) {
        initStyle(style);
        setSize(ap.getPrefWidth(), ap.getPrefHeight());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = screenBounds.getMinX() + screenBounds.getWidth() - ap.getPrefWidth()-20 - 2;
        double y = screenBounds.getMinY() + screenBounds.getHeight() - ap.getPrefHeight() - 2;
        y = screenBounds.getMinY()+20;
        bottomRight = new Location(x,y);
    }

    public Location getBottomRight() {
        return bottomRight;
    }

    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }


    // Get screen bounds
    public Location getOffScreenBounds() {
        Location loc = getBottomRight();
        return new Location(loc.getX() + this.getWidth(), loc.getY());
    }

    public void setLocation(Location loc) {
        setX(loc.getX());
        setY(loc.getY());
    }

    // Used to create animation of stage moving across the screen
    private SimpleDoubleProperty xLocationProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            setX(newValue);
        }

        @Override
        public double get() {
            return getX();
        }
    };

    public SimpleDoubleProperty xLocationProperty() {
        return xLocationProperty;
    }

    private SimpleDoubleProperty yLocationProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            setY(newValue);
        }

        @Override
        public double get() {
            return getY();
        }
    };

    public SimpleDoubleProperty yLocationProperty() {
        return yLocationProperty;
    }
}
