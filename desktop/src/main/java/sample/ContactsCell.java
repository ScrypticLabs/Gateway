/* 
* @File: ContactsCell.java
* @Author: Abhi Gupta
* 
* THIS CAN BE FURTHER IMPLEMENTED IN THE FUTURE
*/

package sample;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ContactsCell extends ListTableViewCell {

    public ContactsCell(String name, String lastMessage, String time) {
        super();
        addCellBackground();
        addCellSeperator();
        addName(name);
        addLastMessage(lastMessage);
        addTimeStamp(time);
        if ((name.indexOf(" ") > 0)) {
            addIcon(name);
        } else {
            addIcon();
        }
    }

    protected void addName(String nom) {
        name = new Label(nom);
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

    protected void addIcon(String nom) {
        Text initials = new Text((nom.charAt(0)+"")+(nom.charAt(nom.indexOf(" ")+1)+""));
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
}
