/* 
* @File: Recent.java
* @Author: Abhi Gupta
*
* This class renders all of the chat components and messages when the user is talking to other contacts (not the AI).
* When the tab 'Conversations' is selected, this class becomes active and also delivers notifications to the user
* when he recieves a new message from an existing contact or an entirely new contact. Though this class is similar
* to 'Gateway', each conversation and its respective messages are associated with each cell in the list of conversations
* located on the left side of the screen. It is also unique because all of the messages are sent to the 'MessageView' class
* when the user selects a conversation to be rendered, hence reducing latency by only having to retrieve the newset messages
* for a conversation and not the entire conversation.
*/

package sample;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.json.JSONArray;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.ParseUser;
import org.parse4j.callback.FindCallback;
import org.parse4j.callback.SaveCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Recent extends CustomTableView implements Database {
    private ParseUser user;
    private String userClass;
    private String queueClass;
    private List<ParseObject> conversations;
    private boolean dataLoaded = false;     // whether or not the newest data has been rendered
    private boolean firstCell = true;
    private MessageView messageView;
    private RecentCell top;
    private TrayNotification notificationCenter;

    public Recent(ParseUser user, MessageView messageView, TrayNotification notificationCenter) {
        super();
        this.user = user;
        this.userClass = Database.USER_PREFIX+user.getObjectId();
        this.queueClass = Database.QUEUE_PREFIX+user.getObjectId();
        this.messageView = messageView;
        this.notificationCenter = notificationCenter;
    }

    protected void loadData() {
        getConversations();
        dataLoaded = true;
    }

    protected boolean isDataLoaded() {
        return dataLoaded;
    }

    protected void loadTableCells(List<ParseObject> conversations)  {
        for (ParseObject convo: conversations) {
            addConversation(convo);
        }
    }

    // this method is triggered when the user wishes to send a message to a new person (to iniate a new conversation) from the contact bar
    private void addConversation(ParseObject convo) {                               // have to make nested ayschronous calls for code to work "synchronously"
        List<String> peopleInChat = new ArrayList<String>(Arrays.asList(convo.getString("Users").split(",")));
        ParseObject chatRoomID = convo.getParseObject("messageTo");
        if (peopleInChat.contains(user.getObjectId())) peopleInChat.remove(peopleInChat.indexOf(user.getObjectId()));

        ParseQuery<ParseObject> users = ParseQuery.getQuery(Database.USERS);
        users.whereEqualTo("objectId", peopleInChat.get(0));
        users.selectKeys(Arrays.asList("firstName", "lastName", "objectId", "email", "username"));
        try {
            List<ParseObject> results = users.find();
            ParseObject recipient = results.get(0);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    addTableCell(recipient, chatRoomID);

                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void addTableCell(ParseObject recipient, ParseObject chatroom) {
        System.out.println(recipient.getString("firstName")+":  "+recipient.getObjectId());
        RecentCell cell = new RecentCell(user, recipient, chatroom, "New Message . . . .", "", messageView);
        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (selectedCell != cell) {
                if (selectedCell != null) {
                    selectedCell.removeHighlight();
                    ((RecentCell)selectedCell).hideConversation();
                }
                cell.highlight();
                selectedCell = cell;
                cell.showConversation();
            } else {
            }
        });
        tableCells.add(cell);
        if (firstCell) {
            top = cell;
            selectedCell = cell;
            cell.showConversation();
            cell.highlight();
            firstCell = false;
        }
    }

    protected void getConversations() {
        ParseQuery<ParseObject> convos = ParseQuery.getQuery(userClass);
        convos.whereNotEqualTo("conversationName", Database.CHATBOT_CHAT_NAME);
        convos.selectKeys(Arrays.asList("Users", "conversationName", "objectId", "messageFrom", "messageTo", "updatedAt", "createdAt"));
        convos.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    conversations = objects;
                    loadTableCells(conversations);
                } else {
                    System.out.println("Could not get messages!");
                }
            }
        });
    }

    protected void handleQueue(List<ParseObject> newMessages) {
        List messagesForCurrentChat = new ArrayList<ParseObject>();
        List<ParseObject> backgroundMessages = new ArrayList<ParseObject>();
        List<ParseObject> messagesFromNewContact = new ArrayList<ParseObject>();

        for (ParseObject chatMessage : newMessages) {
            if (!contactExists(chatMessage)) {
                messagesFromNewContact.add(chatMessage);
            } else if (!messageBelongsToCurrentChat(chatMessage)) {
                backgroundMessages.add(chatMessage);
            } else {
                messagesForCurrentChat.add(chatMessage);
            }
        }
        if (!messagesFromNewContact.isEmpty()) {
            for (ParseObject message : messagesFromNewContact) {
                handleNewConversation(message);      // display a conversation that another person in the world may have initated with you
            }
        }
        if (!messagesForCurrentChat.isEmpty()) {
            messageView.handleQueue(messagesForCurrentChat);    // display all the incoming messaeges from the person the user is currently talking to (in the current chat session)
        }
        if (!backgroundMessages.isEmpty()) {
            if (backgroundMessages.size() > 1) {
                renderNotificationOnMainThread("Gateway", "You have "+backgroundMessages.size()+" new messages");      // only render one notification at a time
            } else {
                renderNotificationOnMainThread(backgroundMessages.get(0));      // only render one notification at a time
            }
            while (!backgroundMessages.isEmpty()) {
                try {
                    backgroundMessages.get(0).delete();
                } catch (ParseException error) {
                    System.out.println(error);
                }
            }
        }
    }

    private void handleNewConversation(ParseObject message) {
        // chatMessages is a row from queue, need to pass ParseObject recipient, ParseObject group
        ParseObject group = message.getParseObject("group");

        ParseQuery<ParseObject> users = ParseQuery.getQuery(Database.USERS);
        users.whereEqualTo("objectId", message.getString("messageFromString"));
        users.selectKeys(Arrays.asList("firstName", "lastName", "objectId", "username", "email"));
        try {
            List<ParseObject> results = users.find();
            ParseObject recipient = results.get(0);
            addTableCell(recipient, group);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean contactExists(ParseObject chatMessage) {
        for (Group conversation : tableCells) {
            if ((Database.CONVERSATION_PREFIX+((RecentCell)conversation).conversationID).equals(chatMessage.getString("convoID"))) {
                return true;
            }
        } return false;
    }

    private boolean messageBelongsToCurrentChat(ParseObject chatMessage) {
        if ((Database.CONVERSATION_PREFIX+((RecentCell)selectedCell).conversationID).equals(chatMessage.getString("convoID"))) {
            return true;
        }
        return false;

    }

    private void renderNotificationOnMainThread(ParseObject chatMessage) {
        ParseQuery<ParseObject> users = ParseQuery.getQuery(Database.USERS);
        users.whereEqualTo("objectId", chatMessage.getString("messageFromString"));
        users.selectKeys(Arrays.asList("firstName", "lastName", "objectId", "email", "username"));
        try {
            List<ParseObject> results = users.find();
            ParseObject recipient = results.get(0);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (!(Database.CONVERSATION_PREFIX+((RecentCell)selectedCell).conversationID).equals(chatMessage.getString("convoID"))) {
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

    public void sendMessage(String message) {
        ((RecentCell)selectedCell).sendMessage(message);
    }

    public String getCurrentChatID() {
        try {
            String id = ((RecentCell) selectedCell).conversationID;
            if (id == null) {
                return "";
            } else {
                return id;
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public boolean newConversation(String contact) {
        for (Group cell : tableCells) {
            if (((RecentCell)cell).recipient.getString("email").equals(contact))
                return false;
        }
        ParseObject recipient;
        ParseQuery<ParseObject> users = ParseQuery.getQuery(Database.USERS);
        users.whereEqualTo("email", contact);
        users.selectKeys(Arrays.asList("firstName", "lastName", "objectId", "updatedAt", "createdAt", "email", "username"));
        try {
            List<ParseObject> results = users.find();
            System.out.println(results);
            recipient = results.get(0);
            createConversationOnServer(recipient);
            return true;

        } catch (ParseException e) {
            System.out.println("could not contact new peson");
            e.printStackTrace();
        } catch (NullPointerException f) {
            f.printStackTrace();
        }
        return false;
    }

    private void createConversationOnServer(ParseObject recipient) {
        System.out.println("servering...");
        createChatroom(recipient);
    }

    private void createChatroom(ParseObject recipient) {
        final ParseObject group = new ParseObject("Groups");
        group.put("Name", "Recent");
        group.put("Users", user.getObjectId()+","+recipient.getObjectId());
        group.put("UserPointers", new JSONArray(new ArrayList<ParseObject>(Arrays.asList(user, recipient))));
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                System.out.println("object created at "+group.getCreatedAt());
                System.out.println(group.getObjectId());
                addConversationToUser(user, group);
                addConversationToUser(recipient, group);
                addTableCell(recipient, group);
                // sendMessage("", group);       // show the new convo only after a message has been sent
            }
        });
    }

    private void addConversationToUser(ParseObject user, ParseObject chatroom) {
        final ParseObject userClass = new ParseObject(Database.USER_PREFIX+user.getObjectId());
        userClass.put("Users", chatroom.getString("Users"));
        userClass.put("conversationName", chatroom.getString("Name"));
        userClass.put("messageFrom", user);
        userClass.put("messageTo", chatroom);
        userClass.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                System.out.println("Convo added to user with id: "+userClass.getObjectId());
            }
        });
    }

    public void sendMessage(String message, ParseObject group) {
        HttpClient.sendMessage(new HashMap<String, String>() {
            {
                put("from", user.getObjectId()+"");
                put("to", group.getObjectId()+"");
                put("message", message);
            }
        });
        ParseObject text = new ParseObject(Database.CONVERSATION_PREFIX+group.getObjectId());       // dont want to save this object as HttpClient takes care of that, just want to pass data as a ParseObject
        text.put("messageFrom", user.getObjectId()+"");
        text.put("to", group.getObjectId()+"");
        text.put("message", message);
    }
}



