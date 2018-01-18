/* 
* @File: ContactBar.java
* @Author: Abhi Gupta
* 
* This class renders the Contacts Bar which allows a user to start a new conversation with a recipient given
* that a valid email address or user name is provided
*/

package sample;

import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ContactBar extends Group {
    private final int HEIGHT = 35;
    private final int WIDTH = TableViewController.CELL_WIDTH;
    private Rectangle bar;          // background of contact bar
    private TextField contact;
    private Button button;          // compose message button
    private MenuBar menuBar;        // reference to the menu bar to keep track of whether or not the contact bar should be rendered

    public ContactBar() {
        createBackground();
        createTextField();
        createButton();
        addEventHandlers();
    }

    private void createBackground() {
        bar = new Rectangle(0,0,WIDTH,HEIGHT);
        bar.setFill(Colour.SENT_MESSAGE);
        getChildren().add(bar);
    }

    private void createTextField() {
        // User ID
        contact = new TextField();
        contact.setPromptText("Enter new recipient's email address");
        contact.getStyleClass().add("contact-field");
        contact.setLayoutX(8);
        contact.setLayoutY(7);
        // Default Text Event Listener with Text Fill
        contact.styleProperty().bind(
                Bindings
                        .when(contact.focusedProperty())
                        .then("-fx-prompt-text-fill: transparent;")
                        .otherwise("-fx-prompt-text-fill: #D8D8D8;"));
        contact.setPrefWidth(WIDTH-44);
        contact.setPrefHeight(HEIGHT-7-7);
        getChildren().add(contact);
    }

    private void createButton() {
        button = new Button(Icons.COMPOSE);
        button.setLayoutX(WIDTH-44+14);
        button.setLayoutY(5);
        button.getStyleClass().add("contact-button");
        getChildren().add(button);
    }

    private void addEventHandlers() {
        // Keyboard Input
        contact.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (code.equals("ENTER")) {
                // System.out.println("Enter pressed");
            }
        });
        contact.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            if (code.equals("ENTER")) {
                // if a new conversation was successfully created, the text field should now be cleared
                if (menuBar.listViews.get(menuBar.status).newConversation(contact.getText())) {     
                    contact.clear();
                }
            }
        });
        // if a new conversation was successfully created, the text field should now be cleared (for those who prefer to click than enter)
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (menuBar.listViews.get(menuBar.status).newConversation(contact.getText())) {
                contact.clear();
            }
        });
    }

    protected void setMenu(MenuBar menuBar) {
        this.menuBar = menuBar;
    }
}
