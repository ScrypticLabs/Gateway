/* 
* @File: MessageBar.java
* @Author: Abhi Gupta
* 
* This class handles the text area in which the user types his message before sending it to a recipient.
*/

package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;


public class MessageBar extends Group implements ViewController, MessagesViewController{
    private Rectangle bar;                          // the background of the bar
    private TextArea message;                       // the textfield where the user types
    private Font font = new Font("avenirnext-ultralight",14);
    // the max pixel width values of any text before a new line is created
    private final double NON_GATEWAY_THRESHOLD = 414.904;   // when chatting with people
    private final double GATEWAY_THRESHOLD = 679.5;         // when chatting with the AI
    private double TEXT_WIDTH_THRESHOLD = GATEWAY_THRESHOLD;
    // keeps track of when a new line has been added or deleted
    private int currentLineNumber = 0;
    private int lastLineNumber = 0;
    private int POS_Y = BAR_Y;

    private final int LINE_NUMBER_THRESHOLD = 2;        // the number of lines you can have in the text field before scrolling is enabled
    private final int INCREASE_HEIGHT_BY = 20;          // each line is approx. 20 in height

    public MessageBar() {
        super();
        prefWidth(BAR_GATEWAY_WIDTH);
        prefHeight(BAR_HEIGHT);
        createBackground();
        createTextField();
        setLayoutY(POS_Y);
        setLayoutX(0);
    }

    private void createBackground() {
        bar = new Rectangle(0,0,BAR_GATEWAY_WIDTH,BAR_HEIGHT);
        bar.setFill(Colour.DARK_GREEN);
        getChildren().add(bar);
    }

    private void createTextField() {
        message = new TextArea();
        message.setPromptText("Type a message . . .");
        message.getStyleClass().add("message-bar-text-field-gateway");
        message.setLayoutX(6);
        message.setLayoutY(6);
        message.setWrapText(true);

        message.setMaxSize(420,28);
        // Default Text Event Listener
        message.styleProperty().bind(
                Bindings
                        .when(message.focusedProperty())
                        .then("-fx-prompt-text-fill: transparent;")
                        .otherwise("-fx-prompt-text-fill: #7E97A5;"));

        // listens for changes to the text field (new message is being created or deleted)
        message.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                float width = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(message.getText(), font);   // the pixel width of the current text
                lastLineNumber = currentLineNumber;

                // maps the pixel width of the text to the number of lines that it would equate to
                if (width >= 0 && width < TEXT_WIDTH_THRESHOLD) {
                    currentLineNumber = 1;
                } else if (width >= TEXT_WIDTH_THRESHOLD && width < TEXT_WIDTH_THRESHOLD * 2) {
                    currentLineNumber = 2;
                } else if (width >= TEXT_WIDTH_THRESHOLD * 2 && width < TEXT_WIDTH_THRESHOLD * 3) {
                    // currentLineNumber = 3;
                }

                // when a line has been created or deleted, the message bar height has to increased or decreased
                if (lastLineNumber < currentLineNumber) {
                    // message bar height is decreased
                    getChildren().remove(bar);
                    bar.setHeight(BAR_HEIGHT+ (INCREASE_HEIGHT_BY) * (currentLineNumber-1));
                    getChildren().add(0, bar);
                    message.setMaxHeight(28 + INCREASE_HEIGHT_BY * (currentLineNumber-1));
                    POS_Y = BAR_Y - INCREASE_HEIGHT_BY*(currentLineNumber-1);
                    setLayoutY(POS_Y);
                } else if (lastLineNumber > currentLineNumber) {
                    // message bar height is increased
                    getChildren().remove(bar);
                    bar.setHeight(BAR_HEIGHT+ (INCREASE_HEIGHT_BY) * (currentLineNumber-1));
                    getChildren().add(0, bar);
                    message.setMaxHeight(28 + INCREASE_HEIGHT_BY * (currentLineNumber-1));
                    POS_Y = BAR_Y + INCREASE_HEIGHT_BY*(currentLineNumber-1);
                    setLayoutY(POS_Y);
                }
            }
        });

        getChildren().add(message);
    }

    // the text field's width, height and other properties are dependent on if the user is talking to the AI or to his friends, hence the properties can be set here based on this status
    public void setSheet(Status status) {
        if (status != Status.GATEWAY) {
            // SMALLER TEXT AREA BECAUSE OF SIDE SCROLLER FOR CONTACTS
            TEXT_WIDTH_THRESHOLD = NON_GATEWAY_THRESHOLD;
            message.clear();
            message.setLayoutX(TableViewController.CELL_WIDTH+6);
            message.getStyleClass().remove("message-bar-text-field-gateway");
            message.getStyleClass().add("message-bar-text-field");
            getChildren().remove(bar);
            bar.setX(BAR_X);
            getChildren().add(0, bar);
        } else {
            // LARGER TEXT AREA - ALSO THE DEFAULT TEXT AREA
            TEXT_WIDTH_THRESHOLD = GATEWAY_THRESHOLD;
            message.clear();
            message.setLayoutX(6);
            message.getStyleClass().remove("message-bar-text-field");
            message.getStyleClass().add("message-bar-text-field-gateway");
            getChildren().remove(bar);
            bar.setX(0);
            getChildren().add(0, bar);
        }
    }

    public TextArea getMessage() { return message; }
}
