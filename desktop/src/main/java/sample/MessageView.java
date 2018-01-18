/* 
* @File: MessageView.java
* @Author: Abhi Gupta
* 
* This 
* Description
*/

package sample;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import org.parse4j.*;
import java.util.List;

public class MessageView extends CustomTableView implements MessagesViewController {
    private ParseUser user;
    private String userClass;
    private String conversationID;
    private String conversationClass;
    private String queueClass;
    private List<ParseObject> chatMessages;
    private ParseObject group;

    public MessageView(ParseUser user) {
        super();
        this.user = user;
        userClass = Database.USER_PREFIX+user.getObjectId();
        queueClass = Database.QUEUE_PREFIX+user.getObjectId();
        setLayoutX(0);      // the x,y will be set in home
        setLayoutY(0);
        prefWidth(VIEW_WIDTH);
        prefHeight(VIEW_MAX_HEIGHT);
        maxWidth(VIEW_WIDTH);
        maxHeight(VIEW_MAX_HEIGHT);
    }

    public void renderNewConversation(List<ParseObject> chatMessages) {
        tableCells.clear();                 // remove all the messages that belonged to the previous conversation
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadTableCells(chatMessages);
            }
        });
    }

    public void renderBlankConversation() {
        tableCells.clear();
    }

    protected void loadTableCells(List<ParseObject> chatMessages) {
        System.out.println("Loading table cells . . .");
        System.out.println(chatMessages);
        for (int i = 0; i < chatMessages.size(); ++i) {
            boolean point = true;
            ParseObject message = chatMessages.get(i);
            ParseObject nextMessage = null;
            if (i+1 < chatMessages.size()-1) nextMessage = chatMessages.get(i+1);
            if (nextMessage != null && message.getParseObject("messageFrom").getObjectId().equals(nextMessage.getParseObject("messageFrom").getObjectId())) point = false;
            if (i+1 >= chatMessages.size() && !point) point = true;
            MessageViewCell cell = new MessageViewCell(message.getString("message"), message.getParseObject("messageFrom").hasSameId(user) ? MessageType.SENDER : MessageType.RECEIVER, point, "3:06 PM");
            cell.setTranslateY(10);
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (selectedCell != cell) {
                    if (selectedCell != null) {
                        selectedCell.removeHighlight();
                    }
                    cell.highlight();
                    selectedCell = cell;
                }
            });
            tableCells.add(cell);
        }
    }

    protected void addTableCell(List<ParseObject> chatMessages) {
        System.out.println("Loading table cells . . .");
        System.out.println(chatMessages);
        for (int i = 0; i < chatMessages.size(); ++i) {
            boolean point = true;
            ParseObject message = chatMessages.get(i);
            ParseObject nextMessage = null;
            if (i+1 < chatMessages.size()-1) nextMessage = chatMessages.get(i+1);
            if (i+1 >= chatMessages.size() && !point) point = true;
            MessageViewCell cell = new MessageViewCell(message.getString("message"), message.getString("messageFrom").equals(user.getObjectId()+"") ? MessageType.SENDER : MessageType.RECEIVER, point, "3:06 PM");
            cell.setTranslateY(10);
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (selectedCell != cell) {
                    if (selectedCell != null) {
                        selectedCell.removeHighlight();
                    }
                    cell.highlight();
                    selectedCell = cell;
                }
            });
            tableCells.add(cell);
        }
    }

    protected void addMessage(List<ParseObject> chatMessages) {
        addTableCell(chatMessages);
    }

    protected void handleQueue(List<ParseObject> newMessages) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadTableCells(newMessages);
            }
        });
        while (newMessages != null && !newMessages.isEmpty()) {
            try {
                newMessages.get(0).delete();
            } catch (ParseException error) {
                System.out.println(error);
            }
        }
    }

    public void sendMessage(String message) {
    }
}