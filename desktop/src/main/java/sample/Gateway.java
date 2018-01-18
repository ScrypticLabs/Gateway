/* 
* @File: Gateway.java
* @Author: Abhi Gupta
* 
* This class handles the UI and conversation of the user with the AI. All database calls are asynchronous and done off the main thread.
*/

package sample;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.parse4j.*;
import org.parse4j.callback.FindCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Gateway extends CustomTableView implements MessagesViewController {
    // User Database Information
    private ParseUser user;
    private String userClass;
    private String conversationID;
    private String conversationClass;
    private String queueClass;
    private List<ParseObject> chatMessages;         // messages exchanged between parties
    private ParseObject group;                      // the chat room
    private TrayNotification notificationCenter;    
    private boolean replyLoaded = false;

    public Gateway(ParseUser user, TrayNotification notificationCenter) {
        super();
        this.user = user;
        this.notificationCenter = notificationCenter;
        userClass = Database.USER_PREFIX+user.getObjectId();    // used to reference the user's conservations in database
        queueClass = Database.QUEUE_PREFIX+user.getObjectId();  // used to reference the user's list of messages waiting to be displayed in chat
        getConversation();                                      // gets all the conversations in the database assoicated with this user
        maxHeight(ViewController.SCREEN_HEIGHT-OFFSET-10-50);   // the UI display height
    }


    /**
    * Gets all of the user's conversations from the database
    */
    private void getConversation() {
        String key = Database.CHATBOT_ID+","+user.getObjectId();
        ParseQuery<ParseObject> conversations = ParseQuery.getQuery(userClass);
        conversations.whereEqualTo("Users", key);
        conversations.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                ParseObject conversation = objects.get(0);
                if (conversation != null) {
                    group = conversation.getParseObject("messageTo");   // get the chatroom of the first conversation - which is also the convo with the AI
                    conversationID = group.getObjectId();
                    conversationClass = Database.CONVERSATION_PREFIX+conversationID;
                    loadMessages();                                     // load all messages found in corresponding chat room
                } else {
                    System.out.println("Could not get conversation!");
                }
            }
        });
    }


    /**
    * Gets all of the messages from the chatroom that he AI and the user are in and renders them on the screen
    */
    private void loadMessages() {
        ParseQuery<ParseObject> messages = ParseQuery.getQuery(conversationClass);
        messages.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    chatMessages = objects;             // list of chat messages      
                    loadTableCells(chatMessages);       // renders each chat message on to the screen
                } else {
                    System.out.println("Could not get messages!");
                }
            }
        });

    }


    /**
    * Renders a list of messages given an amount of chat message objects from the database
    */
    protected void loadTableCells(List<ParseObject> chatMessages) {
        System.out.println("Loading table cells . . .");
        for (int i = 0; i < chatMessages.size(); ++i) {
            boolean point = true;                           // whether or not there should be arrow pointing to the sender
            ParseObject message = chatMessages.get(i);      
            ParseObject nextMessage = null;
            if (i+1 < chatMessages.size()-1) nextMessage = chatMessages.get(i+1);
            // when the next message and current message are from the same user there should only be a point at the last message
            if (nextMessage != null && message.getParseObject("messageFrom").getObjectId().equals(nextMessage.getParseObject("messageFrom").getObjectId())) point = false;
            if (i+1 >= chatMessages.size() && !point) point = true;
            // Renders the message by loading a cell into this table view (which is this class)
            GatewayCell cell = new GatewayCell(message.getString("message"), message.getParseObject("messageFrom").hasSameId(user) ? MessageType.SENDER : MessageType.RECEIVER, point, "3:06 PM");
            cell.setTranslateY(10);
            // Event Handler for the message is created on spot - messages aren't interactive at the moment but can be implemented in the event handler below
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (selectedCell != cell) {
                    if (selectedCell != null) {
                        selectedCell.removeHighlight();
                    }
                    cell.highlight();
                    selectedCell = cell;
                }
            });
            // add the cell to the list of cells (or messages) rendered on the table view
            tableCells.add(cell);
        }
    }

    /**
    * Renders a single message given that the amount of chat message objects from the database is one
    */
    protected void addTableCell(List<ParseObject> chatMessages) {
        System.out.println("Loading table cells . . .");
        for (int i = 0; i < chatMessages.size(); ++i) {
            boolean point = true;
            ParseObject message = chatMessages.get(i);
            ParseObject nextMessage = null;
            if (i+1 < chatMessages.size()-1) nextMessage = chatMessages.get(i+1);
            if (i+1 >= chatMessages.size() && !point) point = true;
            GatewayCell cell = new GatewayCell(message.getString("message"), message.getString("messageFrom").equals(user.getObjectId()+"") ? MessageType.SENDER : MessageType.RECEIVER, point, "3:06 PM");
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

    // protected void addTableCell(List<ParseObject> chatMessages, boolean replyLoaded) {
    //     if (replyLoaded) return;
    //     System.out.println("Loading table cells . . .");
    //     System.out.println(chatMessages);
    //     for (int i = 0; i < chatMessages.size(); ++i) {
    //         boolean point = true;
    //         ParseObject message = chatMessages.get(i);
    //         ParseObject nextMessage = null;
    //         if (i+1 < chatMessages.size()-1) nextMessage = chatMessages.get(i+1);
    //         if (i+1 >= chatMessages.size() && !point) point = true;
    //         GatewayCell cell = new GatewayCell(message.getString("message"), message.getString("messageFrom").equals(user.getObjectId()+"") ? MessageType.SENDER : MessageType.RECEIVER, point, "3:06 PM");
    //         cell.setTranslateY(10);
    //         cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
    //             if (selectedCell != cell) {
    //                 if (selectedCell != null) {
    //                     selectedCell.removeHighlight();
    //                 }
    //                 cell.highlight();
    //                 selectedCell = cell;
    //             }
    //         });
    //         tableCells.add(cell);
    //     }
    // }

    /**
    * Renders messages that the user may have received during the use of the app and hence not displayed when the app was first opened up
    */
    protected void singleHandleQueue(List<ParseObject> newMessages) {
        Platform.runLater(new Runnable() {      // UI changes must be made on main-thread, but since this method is called from off the main thread
            @Override                           // it must be attached back on to the main-thread
            public void run() {
                loadTableCells(newMessages);
            }
        });
        while (newMessages != null) {           // after the new messages have been rendered, they are no longer "new" or waiting to be displayed
            try {
                newMessages.get(0).delete();    // hence the queue must be emptied of these messages
            } catch (ParseException error) {
                System.out.println(error);
            }
        }
    }

    /**
    * Handles the queue of new messages received after the app was initialized
    */
    protected void handleQueue(List<ParseObject> newMessages) {
        List messagesForCurrentChat = new ArrayList<ParseObject>();             // the messages from the queue corresponding to this chat
        List<ParseObject> backgroundMessages = new ArrayList<ParseObject>();    // messages from other conversations

        // Filters the queue based on what conversations these new messages belong to
        for (ParseObject chatMessage : newMessages) {
            if (!messageBelongsToCurrentChat(chatMessage)) {
                backgroundMessages.add(chatMessage);
            } else {
                messagesForCurrentChat.add(chatMessage);
            }
        }

        if (!messagesForCurrentChat.isEmpty()) {
            singleHandleQueue(messagesForCurrentChat);    // display all the incoming messaeges from the person the user is currently talking to (in the current chat session)
        }

        // the user has to be notified of any messages that he may have received from his friends while he was talking to the AI
        if (!backgroundMessages.isEmpty()) {
            if (backgroundMessages.size() > 1) {
                renderNotificationOnMainThread("Gateway", "You have "+backgroundMessages.size()+" new messages");      // only render one notification at a time
            } else {
                renderNotificationOnMainThread(backgroundMessages.get(0));                                             // display the message and person who sent the message
            }
            while (!backgroundMessages.isEmpty()) {     // delete these messages from the queue to prevent notifcations from being rendered endlessly
                try {
                    backgroundMessages.get(0).delete();
                } catch (ParseException error) {
                    System.out.println(error);
                }
            }
        }
    }

    /**
    * Renders a notification on the top right of the screen with the specified information
    */
    private void renderNotificationOnMainThread(String notifier, String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                notificationCenter.setHeader(notifier);
                notificationCenter.setMessage(message);
                notificationCenter.showAndDismiss(Duration.seconds(2));
            }
        });
    }

    /**
    * Renders a notification on the top right of the screen with the sender's name and message
    */
    private void renderNotificationOnMainThread(ParseObject chatMessage) {
        ParseQuery<ParseObject> users = ParseQuery.getQuery(Database.USERS);            // query to get the sender's name from his ID
        users.whereEqualTo("objectId", chatMessage.getString("messageFromString"));
        users.selectKeys(Arrays.asList("firstName", "lastName", "objectId", "email", "username"));
        try {
            List<ParseObject> results = users.find();
            ParseObject recipient = results.get(0);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (!(Database.CONVERSATION_PREFIX+conversationID).equals(chatMessage.getString("convoID"))) {
                        notificationCenter.setHeader(recipient.getString("firstName")+" "+recipient.getString("lastName"));
                        notificationCenter.setMessage(chatMessage.getString("message"));
                        notificationCenter.showAndDismiss(Duration.seconds(2));
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
    * Checks if the message was received by a valid contact
    */
    private boolean contactExists(ParseObject chatMessage) {
        for (Group conversation : tableCells) {
            if ((Database.CONVERSATION_PREFIX+((RecentCell)conversation).conversationID).equals(chatMessage.getString("convoID"))) {
                return true;
            }
        } return false;
    }

    /**
    * Checks if the message belongs to this conversation
    */
    private boolean messageBelongsToCurrentChat(ParseObject chatMessage) {
        if ((Database.CONVERSATION_PREFIX+conversationID).equals(chatMessage.getString("convoID"))) {
            return true;
        }
        return false;

    }

    /**
    * Sends a message to the AI and triggers the server to reply back with an "intelligent" response
    */
    public void sendMessage(String message) {
        // Sends the user's message to the server
        HttpClient.sendMessage(new HashMap<String, String>() {
            {
                put("from", user.getObjectId()+"");
                put("to", group.getObjectId()+"");
                put("message", message);
            }
        });
        // the user's message is then rendered on the screen
        ParseObject text = new ParseObject(Database.CONVERSATION_PREFIX+group.getObjectId());       // dont want to save this object as HttpClient takes care of that, just want to pass data as a ParseObject
        text.put("messageFrom", user.getObjectId()+"");
        text.put("to", group.getObjectId()+"");
        text.put("message", message);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                addTableCell(new ArrayList<ParseObject>(Arrays.asList(text)));
            }
        });

        // the reply from the AI is retrieved via a js backend that uses machine-learning to process natural language (API) configured with a custom, trained model for this app
        // so that the replies make sense and the AI is able to handle the user's questions
        String reply = HttpClient.aiMessage(new HashMap<String, String>() {
            {
                put("lang", "EN");
                put("sessionId", group.getObjectId()+"");
                put("query", message);
            }
        });

        // the reply is sent to the server to be saved as a message from the AI
        HttpClient.sendMessage(new HashMap<String, String>() {
            {
                put("from", Database.CHATBOT_ID+"");
                put("to", group.getObjectId()+"");
                put("message", reply);
            }
        });

        // the reply is finally rendered on the screen
        ParseObject response = new ParseObject(Database.CONVERSATION_PREFIX+group.getObjectId());       // dont want to save this object as HttpClient takes care of that, just want to pass data as a ParseObject
        response.put("messageFrom", Database.CHATBOT_ID+"");
        response.put("to", group.getObjectId()+"");
        response.put("message", reply);
    }
}