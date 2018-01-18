/* 
* @File: RecentCell.java
* @Author: Abhi Gupta
* 
* Description
*/

package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.callback.FindCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecentCell extends ListTableViewCell {
    private ParseObject user;
    protected ParseObject recipient;
    private ParseObject chatroom;
    private String fullname;
    protected String conversationID;
    private String conversationClass;
    private List<ParseObject> chatMessages;
    private MessageView messageView;
    private String newMessage = "";


    public RecentCell(ParseObject user, ParseObject recipient, ParseObject chatroom, String lastMessage, String time, MessageView messageView) {
        super();

        this.user = user;
        this.recipient = recipient;
        this.chatroom = chatroom;
        this.messageView = messageView;

        conversationID = this.chatroom.getObjectId();
        conversationClass = Database.CONVERSATION_PREFIX+conversationID;

        addCellBackground();
        addCellSeperator();
        fullname = this.recipient.getString("firstName")+" "+this.recipient.getString("lastName");
        addName(this.recipient.getString("firstName"), this.recipient.getString("lastName"));
        addLastMessage(lastMessage);
        addTimeStamp(time);
        if ((fullname.indexOf(" ") > 0)) {
            addIcon(this.recipient.getString("firstName"), this.recipient.getString("lastName"));
        } else {
            addIcon();
        }
    }

    protected void addName(String firstName, String lastName) {
        name = new Label(firstName+" "+lastName);
        name.getStyleClass().add("conversation-name");
        name.setLayoutX(72);
        name.setLayoutY(16);
        name.setTextFill(Colour.LIGHT_GREEN);
        pane.getChildren().add(name);
    }

    protected void addLastMessage(String message) {
        lastMessage = new Label(message);
        lastMessage.getStyleClass().add("conversation-last-message");
        lastMessage.setLayoutX(72);
        lastMessage.setLayoutY(40);
        lastMessage.setTextFill(Colour.SEMI_WHITE);
        pane.getChildren().add(lastMessage);
    }

    protected void addIcon(String firstName, String lastName) {
        Text initials = new Text((firstName.charAt(0)+"")+(lastName.charAt(0)+""));
        initials.getStyleClass().add("conversation-initials");
        initials.setLayoutX(16);
        initials.setLayoutY(44);
        initials.setFill(Colour.SEMI_WHITE);

        Circle circle = new Circle(34,CELL_HEIGHT/2,25, Colour.ICON);
        pane.getChildren().addAll(circle, initials);
    }

    protected void addIcon() {
        Text initials = new Text(Icons.LOGO);
        initials.getStyleClass().add("conversation-gateway");
        initials.setLayoutX(14);
        initials.setLayoutY(47);
        initials.setFill(Colour.SEMI_WHITE);

        Circle circle = new Circle(34,CELL_HEIGHT/2,25, Colour.ICON);
        pane.getChildren().addAll(circle, initials);
    }

    @Override
    protected void highlight() {
        pane.getChildren().removeAll(rect, line, name, time);
        rect.setFill(Colour.LIGHT_GREEN);
        pane.getChildren().add(0, rect);
        name.setTextFill(Colour.CELL);
        pane.getChildren().add(2, name);
        time.setTextFill(Colour.CELL);
        pane.getChildren().add(4, time);
    }

    @Override
    protected void removeHighlight() {
        pane.getChildren().removeAll(rect, name, time);
        rect.setFill(Colour.CELL);
        pane.getChildren().add(0, rect);
        pane.getChildren().add(1,line);
        name.setTextFill(Colour.LIGHT_GREEN);
        pane.getChildren().add(2, name);
        time.setTextFill(Colour.SEMI_WHITE);
        pane.getChildren().add(4, time);
    }

    protected void showConversation() {
        loadMessages();
    }

    protected void hideConversation() { messageView.tableCells.clear(); }

    private void loadMessages() {
        chatMessages = null;
        ParseQuery<ParseObject> messages = ParseQuery.getQuery(conversationClass);
        messages.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    chatMessages = objects;
                    messageView.renderNewConversation(chatMessages);
                } else {
                    // messageView.renderBlankConversation();
                }
            }
        });

    }

    public void sendMessage(String message) {
        System.out.println(message);
        HttpClient.sendMessage(new HashMap<String, String>() {
            {
                put("from", user.getObjectId()+"");
                put("to", chatroom.getObjectId()+"");
                put("message", message);
            }
        });
        ParseObject text = new ParseObject(Database.CONVERSATION_PREFIX+chatroom.getObjectId());       // dont want to save this object as HttpClient takes care of that, just want to pass data as a ParseObject
        text.put("messageFrom", user.getObjectId()+"");
        text.put("to", chatroom.getObjectId()+"");
        text.put("message", message);
        messageView.addMessage(new ArrayList<ParseObject>(Arrays.asList(text)));

        // If the user wants to be able to contact gateway within conversations with other users, uncomment the code below

//        String[] words = message.toLowerCase().split(" ");
//        List<String> keywords = new ArrayList(Arrays.asList(words));
//        for (String word : keywords) {
//            if (!word.equals("gateway")) {
//                newMessage += word+" ";
//            }
//        }
//        System.out.println(newMessage);
//        if (keywords.contains("gateway")) {
//            String reply = HttpClient.aiMessage(new HashMap<String, String>() {
//                {
//                    put("lang", "EN");
//                    put("sessionId", chatroom.getObjectId() + "");
//                    put("query", newMessage);
//                }
//            });
//            HttpClient.sendMessage(new HashMap<String, String>() {
//                {
//                    put("from", "D242SF2" + "");
//                    put("to", chatroom.getObjectId() + "");
//                    put("message", reply);
//                }
//            });
//            ParseObject response = new ParseObject(Database.CONVERSATION_PREFIX + chatroom.getObjectId());       // dont want to save this object as HttpClient takes care of that, just want to pass data as a ParseObject
//            response.put("messageFrom", "D242SF2" + "");
//            response.put("to", chatroom.getObjectId() + "");
//            response.put("message", reply);
//
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    messageView.addMessage(new ArrayList<ParseObject>(Arrays.asList(response)));
//                }
//            });
//        }
//        newMessage = "";
    }
}
