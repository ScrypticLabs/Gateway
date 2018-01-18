/* 
* @File: GatewayCell.java
* @Author: Abhi Gupta
* 
* This class represents a custom cell that is used to render a message when the user is talking
* to the AI
*/

package sample;

public class GatewayCell extends ListTableViewCell implements MessagesViewController {
    private iMessage message;       // the message


    /**
    * Constructor.
    * Creates a new table cell with the parameters of a message (time - time stamp, point - whether or not the message has an arrow pointing
    * to the sender, type - incoming or outgoing message)
    */
    public GatewayCell(String message, MessageType type, boolean point, String time) {
        super();
        getStyleClass().add("gateway-cell");
        this.message = new iMessage(message, type, point, time, BUBBLE_MAX_WIDTH);      // creates the message
        this.message.setLayoutX(10);                                                    // renders the message on to the cell at specific x,y
        this.message.setLayoutY(300);
        if (type == MessageType.RECEIVER) {
            receiveMessage(this.message);
        } else {
            sendMessage(this.message);
        }
        getChildren().add(this.message);
    }

    private void receiveMessage(iMessage message) {

    }

    private void sendMessage(iMessage message ) {

    }

    @Override
    protected void highlight() {}

    @Override
    protected void removeHighlight() {}
}
