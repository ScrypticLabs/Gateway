/* 
* @File: MenuBar.java
* @Author: Abhi Gupta
* 
* The menu bar ensures that only certain tabs such "Gateway" or "Conversations" are activated so that events are only triggered from
* each view when its corresponding class is being rendered. This class also allows multiple "screens" or views to be rendered on the 
* same graph by managing the scene's nodes and ensuring that only the ones that should be visible at the time are part of its children.
*/

package sample;

import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

public class MenuBar extends Group implements ViewController, TableViewController {
    private final double HEADER_HEIGHT = 50;
    private final double STATUS_BAR_HEIGHT = 4;
    private Line statusIndicator;
    private final double STATUS_INDICATOR_WIDTH = 182.5*2;
    protected Status status = Status.GATEWAY;       // Default View
    private Map positions;                          // x-position of each tab and its contents
    private Map<Status, Button> buttons;
    private final double DURATION = 500;            // 1/2 second animation when switching tabs
    protected Map<Status, CustomTableView> listViews;
    private ObservableList<Node> homeChildren;      // the graph's children
    private Line seperator;
    private MessageBar messageBar;
    private MessageView messageView;
    private ContactBar contactBar;

    public MenuBar(Map<Status,CustomTableView> listViews, MessageBar messageBar, MessageView messageView, ContactBar contactBar, ObservableList<Node> homeChildren) {
        super();

        this.listViews = listViews;
        this.messageBar = messageBar;
        this.messageView = messageView;
        this.contactBar = contactBar;
        this.homeChildren = homeChildren;

        createHeader();

        positions = new HashMap<Status, Double>();
        // positions.put(Status.GROUPS, STATUS_INDICATOR_WIDTH);
        positions.put(Status.RECENT, STATUS_INDICATOR_WIDTH);
        positions.put(Status.GATEWAY, STATUS_INDICATOR_WIDTH*2);
        // positions.put(Status.CONTACTS, STATUS_INDICATOR_WIDTH*4);

        homeChildren.add(listViews.get(Status.GATEWAY));
        createStatusIndicator();
        buttons = new HashMap<>();
        createButtons();
        setLayoutX(0); setLayoutY(0);
        addEventListeners();
    }

    private void createHeader() {   // the green background
        Rectangle header = new Rectangle(0,0,ViewController.SCREEN_WIDTH,HEADER_HEIGHT);
        header.setFill(Colour.LIGHT_GREEN);
        Line statusBar = new Line(0,HEADER_HEIGHT,ViewController.SCREEN_WIDTH,HEADER_HEIGHT);
        statusBar.setStrokeWidth(STATUS_BAR_HEIGHT);
        statusBar.setStroke(Colour.DARK_GREEN);
        getChildren().addAll(header,statusBar);
    }

    private void createStatusIndicator() {      // the white line under the selected tab
        statusIndicator = new Line(((Double)positions.get(status))-STATUS_INDICATOR_WIDTH,HEADER_HEIGHT,((Double)positions.get(status)),HEADER_HEIGHT);
        statusIndicator.setStrokeWidth(STATUS_BAR_HEIGHT);
        statusIndicator.setStroke(Color.WHITE);
        getChildren().add(statusIndicator);
    }

    private void createButtons() {
        // Button groups = new Button("G R O U P S");
        // groups.setLayoutY(12);
        // groups.setLayoutX(((Double)positions.get(Status.GROUPS))-STATUS_INDICATOR_WIDTH);
        // groups.getStyleClass().add("menu-bar-button");
        // groups.setPrefSize(STATUS_INDICATOR_WIDTH,HEADER_HEIGHT);
        // buttons.put(Status.GROUPS, groups);
        Button people = new Button("C O N V E R S A T I O N S");
        people.setLayoutX(((Double)positions.get(Status.RECENT))-STATUS_INDICATOR_WIDTH);
        people.setLayoutY(12);
        people.getStyleClass().add("menu-bar-button");
        people.setPrefSize(STATUS_INDICATOR_WIDTH,HEADER_HEIGHT);
        buttons.put(Status.RECENT, people);

        Button gateway = new Button("G A T E W A Y");
        gateway.setLayoutX(((Double)positions.get(Status.GATEWAY))-STATUS_INDICATOR_WIDTH);
        gateway.setLayoutY(12);
        gateway.getStyleClass().add("menu-bar-button-selected");
        gateway.setPrefSize(STATUS_INDICATOR_WIDTH,HEADER_HEIGHT);
        buttons.put(Status.GATEWAY, gateway);

        // Button settings = new Button("C O N T A C T S");
        // settings.setLayoutX(((Double)positions.get(Status.CONTACTS))-STATUS_INDICATOR_WIDTH);
        // settings.setLayoutY(12);
        // settings.getStyleClass().add("menu-bar-button");
        // settings.setPrefSize(STATUS_INDICATOR_WIDTH,HEADER_HEIGHT);
        // buttons.put(Status.CONTACTS, settings);
        getChildren().addAll(people, gateway);
    }

    private void addEventListeners() {
        for (Map.Entry<Status, Button> entry : buttons.entrySet()) {
            Status key = entry.getKey();    // the tab that the event handler is being added to
            Button value = entry.getValue();
            value.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (status != key) {        // indicates that the user wants to go to a new tab
                    Path path = new Path();
                    path.getElements().addAll(new MoveTo(((Double) positions.get(status)) - .5 * STATUS_INDICATOR_WIDTH, HEADER_HEIGHT), new HLineTo(((Double) positions.get(key)) - .5 * STATUS_INDICATOR_WIDTH));
                    path.setFill(null);
                    getChildren().add(path);
                    PathTransition pt = new PathTransition(Duration.millis(DURATION), path, statusIndicator);
                    pt.play();
                    pt.setAutoReverse(true);
                    // after the animation take place, the new tab's ui must be rendered

                    pt.setOnFinished(f -> {
                        // LOAD DATA BEFORE RENDERING UI
                        if (key == Status.RECENT) {
                            if (!((Recent) listViews.get(key)).isDataLoaded()) {
                                ((Recent) listViews.get(key)).loadData();
                            }
                        }
                        // RENDER UI
                        buttons.get(status).getStyleClass().remove("menu-bar-button-selected");
                        buttons.get(status).getStyleClass().add("menu-bar-button");
                        value.getStyleClass().remove("menu-bar-button");
                        value.getStyleClass().add("menu-bar-button-selected");
                        if (key != Status.GATEWAY) {
                            if (!homeChildren.contains(messageView))
                                homeChildren.add(messageView);
                            if (!homeChildren.contains(seperator))
                                addSeperator();
                            if (!homeChildren.contains(contactBar))
                                homeChildren.add(contactBar);
                        } else {
                            if (homeChildren.contains(messageView))
                                homeChildren.remove(messageView);
                            if (homeChildren.contains(seperator))
                                homeChildren.remove(seperator);
                            if (homeChildren.contains(contactBar))
                                homeChildren.remove(contactBar);
                        }
                        if (homeChildren.contains(listViews.get(status))) {
                            homeChildren.remove(listViews.get(status));
                        }
                        if (key != Status.GATEWAY) {
                            messageBar.setSheet(Status.RECENT);
                        } else {
                            messageBar.setSheet(Status.GATEWAY);
                        }
                        // update its status as being the one that is active
                        homeChildren.add(listViews.get(key));
                        messageBar.toFront();
                        status = key;
                    });
                    getChildren().remove(path);     // remove the animation once it has been shown
                }
            });
        }

        // Keyboard Input - when enter is hit it indicates that the user wishes to send a message
        messageBar.getMessage().setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (code.equals("ENTER"))
                listViews.get(status).sendMessage(messageBar.getMessage().getText());   // the message to be sent is handled by the apropriate tab

        });
        // the message is actually delivered when the user releases the enter button
        messageBar.getMessage().setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            if (code.equals("ENTER"))
                messageBar.getMessage().clear();
        });
    }

    // adds a line between the contacts view and where the messages are actually being rendered
    private void addSeperator() {
        seperator = new Line(CELL_WIDTH,OFFSET+2,CELL_WIDTH,SCREEN_HEIGHT-2);
        seperator.setStrokeWidth(2);
        seperator.setStroke(Colour.LINE);
        homeChildren.add(seperator);
    }

    public Status getCurrentStatus() { return status; }
}
