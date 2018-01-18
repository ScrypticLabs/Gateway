/* 
* @File: Groups.java
* @Author: Abhi Gupta
* 
* THIS CAN BE FURTHER IMPLEMENTED IN THE FUTURE
*/

package sample;

import javafx.scene.input.MouseEvent;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseUser;
import java.util.List;

public class Groups extends CustomTableView {
    ParseUser user;

    public Groups(ParseUser user) {
        super();
        this.user = user;
    }

    protected void loadTableCells(List<ParseObject> messages) {
        for (int i = 1; i <= 4; ++i) {
            GroupsCell cell = new GroupsCell("Mark Johnson","Deal! Let's meet tomorrow . . .", "3:39 PM");
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

    protected void handleQueue(List<ParseObject> newMessages) {
        loadTableCells(newMessages);
        for (ParseObject object : newMessages) {
            try {
                object.delete();
            } catch (ParseException error) {
                error.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
    }

    public boolean newConversation(String contacts) {
        return false;
    }
}
