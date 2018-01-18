/* 
* @File: MessageViewCell.java
* @Author: Abhi Gupta
* 
*/

package sample;

public class MessageViewCell extends ListTableViewCell implements MessagesViewController {
    private iMessage message;

    public MessageViewCell(String message, MessageType type, boolean point, String time) {
        super();
        getStyleClass().add("gateway-cell");

        this.message = new iMessage(message, type, point, time, VIEW_MAX_WIDTH);
        this.message.setLayoutX(10);
        this.message.setLayoutY(300);

        if (type == MessageType.RECEIVER) {
            receiveMessage(this.message);
        } else {
            sendMessage(this.message);
        }
        getChildren().add(this.message);
    }

    private void receiveMessage(iMessage message) {}

    private void sendMessage(iMessage message ) {}

    @Override
    protected void highlight() {}

    @Override
    protected void removeHighlight() {}
}
