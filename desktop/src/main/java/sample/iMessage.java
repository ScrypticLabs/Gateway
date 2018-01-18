/* 
* @File: iMessage.java
* @Author: Abhi Gupta
* 
* This class wraps the Bubble class by not only rendering the bubble but by also providing additional methods
* that can be used by clients to manipulate a message (change its content). This class would allow for messages to 
* be deleted or modified after they have been sent.
*/

package sample;

import javafx.scene.layout.Pane;

public class iMessage extends Pane implements MessagesViewController, ViewController {
    private String text;
    private MessageType type;
    private String time;
    private Bubble bubble;

    public iMessage(String message, MessageType type, boolean point, String time, double maxWidth) {
        text = message;
        this.type = type;
        this.time = time;
        setLayoutX(0); setLayoutY(0);
        bubble = new Bubble(message, type, point, maxWidth);
        bubble.setLayoutX(6);
        bubble.setLayoutY(0);
        getChildren().add(bubble);

    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public MessageType getType() {
        return type;
    }

}
