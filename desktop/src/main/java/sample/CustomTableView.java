/* 
* @File: CustomTableView.java
* @Author: Abhi Gupta
* 
* This class is the basic strucuture for any view that will be presenting a table of data.
* This data can be in the form of messages or contacts, but it's most basic generic implementation
* is this class.
*/

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import org.parse4j.ParseObject;
import java.util.List;


public abstract class CustomTableView extends ListView implements TableViewController {
    protected final ObservableList<Group> tableCells = FXCollections.observableArrayList();     // all of the cells
    protected ListTableViewCell selectedCell;

    public CustomTableView() {
        super();
        setItems(tableCells);
        setBackground(Background.EMPTY);
    }

    protected abstract void loadTableCells(List<ParseObject> messages);

    protected abstract void handleQueue(List<ParseObject> newMessages);

    protected abstract void sendMessage(String message);

    public boolean newConversation(String contacts) { return false; }

}
