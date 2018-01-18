/* 
* @File: ListTableViewCell.java
* @Author: Abhi Gupta
* 
* This class is a generic table view cell that will be a parent to all other
* classes using a list view to display rows of data (cells)
*/

package sample;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public abstract class ListTableViewCell extends Group implements TableViewController {
    protected Group pane;
    protected int STROKE_WIDTH = 1;
    protected Label name, lastMessage, time;
    protected Rectangle rect;
    protected Line line;

    public ListTableViewCell() {
        super();
        prefWidth(CELL_WIDTH);
        prefHeight(CELL_HEIGHT);
        pane = new Group();
        pane.prefWidth(CELL_WIDTH);
        pane.prefHeight(CELL_HEIGHT);
        getChildren().add(pane);
    }

    // a line seperating this cell from the cell above
    protected void addCellSeperator() {
        line = new Line(10, CELL_HEIGHT-STROKE_WIDTH, 270, CELL_HEIGHT-STROKE_WIDTH);
        line.setStrokeWidth(STROKE_WIDTH);
        line.setStroke(Colour.LINE);
        pane.getChildren().addAll(line);
    }

    protected void addCellBackground() {
        rect = new Rectangle(-1, 0, CELL_WIDTH, CELL_HEIGHT);
        rect.setFill(Colour.CELL);
        pane.getChildren().add(rect);
    }

    protected void addTimeStamp(String timeStamp) {
        time = new Label(timeStamp);
        time.getStyleClass().add("conversation-time-stamp");
        time.setTextFill(Colour.SEMI_WHITE);
        time.setLayoutX(210);
        time.setLayoutY(16);
        pane.getChildren().add(time);
    }

    // the method is called when the cell is clicked
    protected abstract void highlight();

    // the method is called when any other cell except this cell is clicked
    protected abstract void removeHighlight();
}
