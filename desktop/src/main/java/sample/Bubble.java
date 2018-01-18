/* 
* @File: Bubble.java
* @Author: Abhi Gupta
* 
* This class creates and maps a text bubble on to a message natively in JavaFX.
*/

package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;

public class Bubble extends Group implements MessagesViewController {
    private Font font = new Font("avenirnext-regular",13);
    private double textWidth;
    private String message;
    private MessageType type;
    private List<String> textLines;     // message seperated into lines based on their relative length
    private final int TEXT_SPACING = 17;
    private int CELL_SPACING = 10;

    private double bubbleWidth = 8;     // offsets from sides of bubble
    private double bubbleHeight = 8;
    private double POS_X;
    private double BUBBLE_MAX_WIDTH;    // max pixel length allowed for text

    private Rectangle bubble;           // the actual bubble

    /**
    * Constructor.
    * Creates a new bubble given the message and whether it is an outgoing or incoming text
    */
    public Bubble(String message, MessageType type, boolean point, double maxWidth) {
        this.message = message;
        this.type = type;
        BUBBLE_MAX_WIDTH = maxWidth;
        createBubble(point);
    }

    /**
    * Creates the UI of the bubble
    */
    private void createBubble(boolean point) {
        wrapText();
        createBackground(point);
    }

    /**
    * Renders the truncated text on to multiple labels and assigns the children to the root node
    */
    private void wrapText() {
        textLines = new LinkedList<String>();
        truncate(message);      // split text
        int y = 18;
        if (textLines.size() > 1) {
            bubbleWidth = BUBBLE_MAX_WIDTH;
        } else {
            bubbleWidth = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(textLines.get(0), font)+20;     // gets pixel length of the longest line (text)
        }
        // handles whether the message should be rendered on the left or right of screen
        double tmp = BUBBLE_MAX_WIDTH == MessagesViewController.BUBBLE_MAX_WIDTH ? ViewController.SCREEN_WIDTH-(bubbleWidth+8) : (ViewController.SCREEN_WIDTH-TableViewController.CELL_WIDTH)-(bubbleWidth+8);

        // renders each line of text;
        POS_X = type == MessageType.RECEIVER ? 2 : tmp;
        for (String line : textLines) {
            Text text = new Text(line);
            text.setFont(font);
            text.setFill(Colour.SEMI_WHITE);
            text.setX(POS_X+6);
            text.setY(y);
            y += TEXT_SPACING;
            getChildren().add(text);
        } bubbleHeight += y;

    }

    /**
    * Takes the text of the message and recursively splits it into multiple lines based on the maximum pixel length that can be occupied
    */
    private void truncate(String text) {
        textWidth = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(text, font);
        if (textWidth > BUBBLE_MAX_WIDTH) {
            BreakIterator bi = BreakIterator.getWordInstance();     // gets a portion of a string at even intervals (does not splice words)
            bi.setText(text);
            int maxLength = pixelWidthToStringLength(text,BUBBLE_MAX_WIDTH);    // converts the font pixel width to character length
            int nextWordAt = bi.preceding(maxLength);
            String line = text.substring(0,nextWordAt);             // the line of text after truncating the message
            textLines.add(line);
            truncate(text.substring(nextWordAt));                   // each line of the leftover message is again trunacted until every line fits in the pixel length space
        } else {
            textLines.add(text);
        }
    }

    /**
    * Converts font pixel width to native string length
    */
    private int pixelWidthToStringLength(String text, double textWidth) {
        int length = 0;
        while (com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(text.substring(0,length), font) < textWidth) {
            length++;
        } return length-1;
    }

    /**
    * Creates the background (or text bubble) on which the text will be rendered
    * @param point whether or not there should be an arrow pointing to the user that sent the message
    */
    private void createBackground(boolean point) {
        double sx = type == MessageType.RECEIVER ? POS_X : POS_X+bubbleWidth-6;     // X-pos of bubble
        if (point) {        // renders the point
            Polygon polygon = new Polygon(new double[]{sx, bubbleHeight - 15 - 5, sx, bubbleHeight - 5, type == MessageType.RECEIVER ? sx + 10 : sx - 10, bubbleHeight - 15 - 5});
            polygon.setFill(type == MessageType.RECEIVER ? Colour.RECEIVED_MESSAGE : Colour.SENT_MESSAGE);
            getChildren().add(1, polygon);
        } else {
            CELL_SPACING -= 5;      // need less spacing when there is no point
        }
        // Create the bubble
        bubble = new Rectangle(0,1,bubbleWidth-6,bubbleHeight-15);
        bubble.setFill(type == MessageType.RECEIVER ? Colour.RECEIVED_MESSAGE : Colour.SENT_MESSAGE);
        bubble.setX(POS_X);
        bubble.setY(0);
        bubble.setArcWidth(10); // Rounded corneres
        bubble.setArcHeight(10);
        bubble.toBack();

        // Dummy bubble that is transparent - required to create spacing between this message and the next message
        Rectangle rect = new Rectangle(bubbleWidth-6,bubbleHeight-15,100,CELL_SPACING+4);
        rect.setFill(Color.TRANSPARENT);
        getChildren().add(0,bubble);
        getChildren().add(rect);

    }
}
