/* 
* @File: Home.java
* @Author: Abhi Gupta
* 
* This class handles the main activity of the user once he has successfully signed up / logged in. All of the
* chat componenets are connected to each other and rendered based on whether the user is talking to the AI or to his
* friends. Moreover, the UI and the data also interact with each other in this class where certain methods allow 
* for data from the server to be seemlessly integrated in the chatting scene.
*/

package sample;

import javafx.scene.Group;
import javafx.util.Duration;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Group implements ViewController, TableViewController, MessagesViewController {
    protected Toolbar toolbar;
    private MenuBar menuBar;
    private Map<Status, CustomTableView> listViews; // 'Conversations' and 'Gateway' tabs
    private MessageBar messageBar;
    private MessageView messageView;                // renders all of the messages in the 'Conversations' tab
    private String queueClass;
    private ParseUser user;
    private List<ParseObject> newMessages;
    private PeriodicTask messageListener;
    private Task updateData;
    private final double DELAY = 1.0;    //  TIME TO WAIT BEFORE RETRIEVING DATA
    private ContactBar contactBar;
    private TrayNotification notificationCenter;

    public Home(TrayNotification notificationCenter) {
        super();
        listViews = new HashMap<>();
        this.notificationCenter = notificationCenter;
        setLayoutX(0); setLayoutY(0);
    }

    public boolean loginUser(String email, String password) {
        System.out.println("Logging in user . . .");
        try {
            user = ParseUser.login(email, password);    // once the user has logged in, the ui can be rendered with the appropriate data
            messageView = new MessageView(user);
            renderUI();
            queueClass = Database.QUEUE_PREFIX+user.getObjectId();
            updateData = new Task(queueClass, newMessages, listViews, menuBar);
            messageListener = new PeriodicTask(updateData, (int)(DELAY*1000));
            messageListener.startTask();                // start listening for new messages since the app has now been initialized with the pre-existing messages
            System.out.println("User logged in successfully!");
            return true;
        } catch(ParseException e) {
            // Notify the user that he failed to login
            this.notificationCenter.setHeader("Login Failed!");
            this.notificationCenter.setMessage("Invalid Username or Password");
            this.notificationCenter.showAndDismiss(Duration.seconds(2));
            System.out.println("User was not logged in!");
            return false;
        }
    }

    private void renderUI() {
        // the table height and width constraints in which all of the conversations' messages will be rendered
        messageView.setLayoutX(MessagesViewController.VIEW_POS_X);
        messageView.setLayoutY(MessagesViewController.VIEW_POS_Y);
        messageView.prefHeight(VIEW_MAX_HEIGHT);
        messageView.prefWidth(VIEW_WIDTH);
        messageView.setMinWidth(VIEW_WIDTH);
        messageView.setMinHeight(VIEW_MAX_HEIGHT);
        messageView.maxHeight(VIEW_MAX_HEIGHT);
        messageView.getStyleClass().add("gateway-table-view");

        CustomTableView recent = new Recent(user, messageView, notificationCenter);
        CustomTableView gateway = new Gateway(user, notificationCenter);
        // CustomTableView groups = new Groups(user);                                   // can be implemented in the future
        // CustomTableView contacts = new Contacts(user);

        listViews.put(Status.RECENT, recent);
        // listViews.put(Status.GROUPS, groups);
        // listViews.put(Status.CONTACTS, contacts);
        listViews.put(Status.GATEWAY, gateway);

        // sets constraints on each tab: 'Conversations' and 'Gateway'
        for (Map.Entry<Status, CustomTableView> entry : listViews.entrySet()) {
            CustomTableView value = entry.getValue();
            value.setPrefHeight(TABLE_HEIGHT-35);
            if (entry.getKey() != Status.GATEWAY) {
                value.setLayoutY(OFFSET+35);
                value.setPrefWidth(TABLE_WIDTH);
                value.getStyleClass().add("scrollview-bar");
            } else {
                value.setLayoutY(OFFSET);
                value.setPrefWidth(ViewController.SCREEN_WIDTH);
                value.setPrefHeight(ViewController.SCREEN_HEIGHT-OFFSET-BAR_HEIGHT);
                value.getStyleClass().add("gateway-table-view");
            }
        }

        contactBar = new ContactBar();
        contactBar.setLayoutY(OFFSET);
        contactBar.setLayoutX(0);

        messageBar = new MessageBar();
        menuBar = new MenuBar(listViews, messageBar, messageView, contactBar, getChildren());

        contactBar.setMenu(menuBar);
        getChildren().addAll(menuBar, messageBar, messageView);
    }

    public void setUser(ParseUser user) { this.user = user; }

    @Override
    public String toString() {
        return "Home";
    }
}
