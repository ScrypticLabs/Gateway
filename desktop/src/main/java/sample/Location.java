/* 
* @File: Location.java
* @Author: Abhi Gupta
* 
*/

package sample;

// Used in creating an animation for the notifications
public class Location {

    private double x, y;

    public Location(double xLoc, double yLoc) {
        this.x = xLoc;
        this.y = yLoc;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
