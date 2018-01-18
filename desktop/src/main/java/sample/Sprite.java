/**
 * @File: Sprite.java
 * @Author: Abhi Gupta
 * @Description: This class contains all of the basics properties and methods to move, animate and render a sprite on to the screen
 *               via a scene graph or GraphicsContext. Every moving object in the game at its core is a sprite despite lacking some
 *               of the animations.
 */

package sample;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

public class Sprite {
    protected Image image;
    protected double positionX;
    protected double positionY;
    protected double velocityX;
    protected double velocityY;
    protected double width;
    protected double height;

    /**
     * Constructor - creates a new sprite with no velocity and set at (0,0) by default
     */
    public Sprite() {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    public void setImage(Image i) {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    public void setImage(String filename) {
        Image i = new Image(filename);
        setImage(i);
    }

    public void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
    }

    public void setVelocity(double x, double y) {
        velocityX = x;
        velocityY = y;
    }

    public void setVelocityX(double x) {velocityX = x;}

    public void addVelocity(double x, double y) {
        velocityX += x;
        velocityY += y;
    }

    public double getX() {
        return positionX;
    }

    public double getY() {
        return positionY;
    }

    public double getWidth() {return width;}

    public double getHeight() {return height;}

    /**
     * Updates the sprite's x and y position based on its current velocity and time (the longer the user plays the game, the faster the sprite accelerates)
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    /**
     * Updates the sprite's x and y position based only on its current velocity
     */
    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }

    public Image getImage() { return image; }

    public void render(GraphicsContext gc) {
         gc.drawImage( image, positionX, positionY );
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX,positionY,width,height);
    }

    public boolean intersects(Sprite s) {
        return s.getBoundary().intersects( this.getBoundary() );
    }

    public String toString() {
        return " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]";
    }

    /**
     * Resets the sprite's variables
     */
    public void newGame() {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }
}