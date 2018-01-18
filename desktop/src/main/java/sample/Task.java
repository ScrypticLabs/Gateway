/* 
* @File: Task.java
* @Author: Abhi Gupta
* 
* This class represents the task that the class PeriodicTask will perform each interval of time. The defined task
* is to listen to the user's queue of messages located in the database for new conversations, notifications and messages
* after the app has been initialized with its data for the first time. This will allow the app to be updated with 
* messages in realtime and allow the user to engage in new conversations during the use of the app.
*/

package sample;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.callback.FindCallback;
import java.util.List;
import java.util.Map;

public class Task {
    private List<ParseObject> newMessages;              // all of the data in the queue 
    private String queueClass;                          // the reference to the user's queue in the database
    private Map<Status, CustomTableView> listViews;     // all of the tabs in home such as conversations and gateway
    private MenuBar menuBar;                            

    /**
    * Constructor.
    * Creates a new task with the required parameters to retrieve the user's queue from the database
    */
    public Task(String queueClass, List<ParseObject> newMessages, Map<Status, CustomTableView> listViews, MenuBar menuBar) {
        this.queueClass = queueClass;
        this.newMessages = newMessages;
        this.listViews = listViews;
        this.menuBar = menuBar;
    }


    /**
    * This method is called periodically to check for new messages or data rows in the user's queue. Once the data is retrieved, either
    * the Conversations tab or the Gateway tab will handle how to respond to this data. The data is also mainly filtered by each tab to
    * provide notifcations for the right messages.
    */
    private void refreshQueue() {
        ParseQuery<ParseObject> messages = ParseQuery.getQuery(queueClass);     // the queue's messages
        Status status = menuBar.getCurrentStatus();
        if (status == Status.GATEWAY) {
//            messages.whereEqualTo("messageFromString", Database.CHATBOT_ID);
        } else {
            messages.whereNotEqualTo("messageFromString", Database.CHATBOT_ID);     // only want data from users other than AI when in the 'Convesations' tab
        } // asynchronously retrieve the queue
        messages.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    newMessages = objects;
                    listViews.get(menuBar.getCurrentStatus()).handleQueue(newMessages);     // handle the queue by 'Conversations' or "Gateway"
                } else {
                }
            }
        });
    }

    public void run() {
        refreshQueue();
    }
}
