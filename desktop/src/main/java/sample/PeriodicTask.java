/* 
* @File: PeriodicTask.java
* @Author: Abhi Gupta
* 
* Though it is important to be able to send messages and save them in the database, it is also
* crucial to be able to get them in real-time even after the app has been initialized. Hence, 
* this class implements a timer that takes in a task and performs it periodically on a seperate
* thread (a Task could be listening for messages or checking if a connection to the server is alive).
*/

package sample;

import java.util.*;

public class PeriodicTask extends TimerTask {
    private int timeInterval;
    private Timer timer;
    private Task task;

    public PeriodicTask(Task task, int timeInterval){
        timer = new Timer();
        this.timeInterval=timeInterval;
        this.task = task;
    }

    public void startTask() {
        timer.schedule(this, 0, timeInterval);
    }

    public void run() {
        task.run();
    }
}
